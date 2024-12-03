package com.and04.naturealbum.data.repository.firebase

import com.and04.naturealbum.data.datasource.FirebaseDataSource
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUser
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FriendRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    // 친구 요청 보냈을 경우
    suspend fun sendFriendRequest(uid: String, targetUid: String): Boolean {
        val requestTime = LocalDateTime.now().toString()

        val currentUserSnapshot = firebaseDataSource.getUser(uid)
        val targetUserSnapshot = firebaseDataSource.getUser(targetUid)

        if (!currentUserSnapshot.exists() || !targetUserSnapshot.exists()) return false

        val currentUser =
            currentUserSnapshot.toObject(FirestoreUser::class.java)?.copy(uid = uid)
                ?: return false
        val targetUser =
            targetUserSnapshot.toObject(FirestoreUser::class.java)?.copy(uid = targetUid)
                ?: return false

        // 친구 요청 데이터 생성
        val friendRequest = FirebaseFriendRequest(
            user = targetUser,
            requestedAt = requestTime,
            status = FriendStatus.SENT
        )
        val targetFriendRequest = FirebaseFriendRequest(
            user = currentUser,
            requestedAt = requestTime,
            status = FriendStatus.RECEIVED
        )

        // Firestore 트랜잭션으로 요청 저장
        return firebaseDataSource
            .setTransactionFriendRequest(
                uid,
                targetUid,
                friendRequest,
                targetFriendRequest
            ).isSuccess
    }


    // 수락했을 경우
    suspend fun acceptFriendRequest(uid: String, targetUid: String): Boolean {
        val addedTime = LocalDateTime.now().toString() // 문자열로 변환

        // 요청자와 대상자의 사용자 정보 가져오기
        val currentUser = firebaseDataSource.getUser(uid)
            .toObject(FirestoreUser::class.java) ?: return false
        val targetUser = firebaseDataSource.getUser(targetUid)
            .toObject(FirestoreUser::class.java) ?: return false

        // FirebaseFriend 데이터 생성
        val uidFriendData = FirebaseFriend(user = targetUser, addedAt = addedTime)
        val targetUidFriendData = FirebaseFriend(user = currentUser, addedAt = addedTime)

        return firebaseDataSource
            .acceptTransactionFriendRequest(
                uid,
                targetUid,
                uidFriendData,
                targetUidFriendData
            ).isSuccess
    }

    // 거절했을 경우
    suspend fun rejectFriendRequest(uid: String, targetUid: String): Boolean {
        return firebaseDataSource.deleteTransactionFriendRequest(uid, targetUid).isSuccess
    }

    // 검색했을 경우
    fun searchUsersAsFlow(
        uid: String,
        query: String
    ): Flow<Map<String, FirestoreUserWithStatus>> =
        callbackFlow {
            if (query.isBlank()) {
                trySend(emptyMap())
                close()
                return@callbackFlow
            }
            val jobList = mutableListOf<Job>()

            val listener = firebaseDataSource.searchUsers(query)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        trySend(emptyMap())
                        return@addSnapshotListener
                    }

                    val userMap = mutableMapOf<String, FirestoreUserWithStatus>()

                    snapshot?.documents?.forEach { userDoc ->
                        val job = launch {
                            if (userDoc.id == uid) return@launch

                            val user = userDoc.toObject(FirestoreUser::class.java) ?: return@launch
                            var friendStatus = FriendStatus.NORMAL

                            try {
                                val (friendRequestDoc, friendDoc) = listOf(
                                    firebaseDataSource
                                        .getFriendRequestDoc(userDoc.id, uid)
                                        .get(),

                                    firebaseDataSource
                                        .getFriendDoc(uid, userDoc.id)
                                        .get()
                                ).map { taskResult -> taskResult.await() }

                                // 친구 상태 결정
                                friendStatus = when {
                                    friendDoc.exists() -> FriendStatus.FRIEND
                                    friendRequestDoc.exists() -> {
                                        val request =
                                            friendRequestDoc.toObject(FirebaseFriendRequest::class.java)
                                        // 상대방(equest?.status) 기준 => 현재 uid 에게 보냈는지, 받았는지 확인
                                        if (request?.status == FriendStatus.RECEIVED) {
                                            FriendStatus.SENT // 현재 uid 기준 [상대방 RECEIVED : 나  SENT]
                                        } else {
                                            FriendStatus.RECEIVED // 현재 uid 기준 [상대방 SENT : 나  RECEIVED]
                                        }
                                    }

                                    else -> FriendStatus.NORMAL
                                }

                                userMap[userDoc.id] = FirestoreUserWithStatus(
                                    user = user,
                                    status = friendStatus
                                )

                                trySend(userMap).isSuccess
                            } catch (ex: CancellationException) {
                                this@launch.cancel()
                            } catch (ex: Exception) {
                                this@launch.cancel()
                            }
                        }

                        jobList.add(job)
                    }
                }

            awaitClose {
                listener.remove()
                jobList.forEach { job: Job -> job.cancel() }
                jobList.clear()
            }
        }

    fun getFriendsAsFlow(uid: String): Flow<List<FirebaseFriend>> = callbackFlow {
        val listener = firebaseDataSource.getUserFriends(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val friendList = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    try {
                        documentSnapshot.toObject(FirebaseFriend::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(friendList)
            }

        awaitClose { listener.remove() }
    }

    fun getReceivedFriendRequestsAsFlow(uid: String): Flow<List<FirebaseFriendRequest>> =
        callbackFlow {
            val listener = firebaseDataSource.getReceivedFriendRequests(uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val receivedFriendRequestList =
                        snapshot?.documents?.mapNotNull { documentSnapshot ->
                            try {
                                val friendRequest =
                                    documentSnapshot.toObject(FirebaseFriendRequest::class.java)
                                if (friendRequest?.status == FriendStatus.RECEIVED) {
                                    friendRequest
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()

                    trySend(receivedFriendRequestList)
                }

            awaitClose { listener.remove() }
        }
}
