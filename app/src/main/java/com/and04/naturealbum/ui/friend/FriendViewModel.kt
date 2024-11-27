package com.and04.naturealbum.ui.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.ui.mypage.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FriendViewModel @Inject constructor(
    private val fireBaseRepository: FireBaseRepository,
) : ViewModel() {

    private val _receivedFriendRequests = MutableStateFlow<List<FirebaseFriendRequest>>(emptyList())
    val receivedFriendRequests: StateFlow<List<FirebaseFriendRequest>> = _receivedFriendRequests

    private val _friends = MutableStateFlow<List<FirebaseFriend>>(emptyList())
    val friends: StateFlow<List<FirebaseFriend>> = _friends

    private val _operationStatus = MutableStateFlow<String>("")

    private val _searchQuery = MutableStateFlow("")

    private val _searchResults = MutableStateFlow<List<FirestoreUserWithStatus>>(emptyList())
    val searchResults: StateFlow<List<FirestoreUserWithStatus>> = _searchResults

    private val debouncePeriod = 100L

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(debouncePeriod) // debouncePeriod 동안 입력 없을 때만 처리
                .filter { query -> query.isNotBlank() } // 빈 쿼리 무시
                .distinctUntilChanged() // 중복 값 방지
                .collect { query ->
                    UserManager.getUser()?.uid?.let { currentUid ->
                        fetchFilteredUsersAsFlow(currentUid, query)
                    }
                }
        }
        listenToFriends()
        listenToReceivedFriendRequests()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun fetchFilteredUsersAsFlow(currentUid: String, query: String) {
        viewModelScope.launch {
            fireBaseRepository.searchUsersAsFlow(currentUid, query).collectLatest { results ->
                _searchResults.value = results
            }
        }
    }

    private fun listenToFriends() {
        viewModelScope.launch {
            val uid = UserManager.getUser()?.uid ?: return@launch
            fireBaseRepository.getFriendsAsFlow(uid).collect { friends ->
                _friends.value = friends
            }
        }
    }

    private fun listenToReceivedFriendRequests() {
        viewModelScope.launch {
            val uid = UserManager.getUser()?.uid ?: return@launch
            fireBaseRepository.getReceivedFriendRequestsAsFlow(uid)
                .collect { receivedFriendRequests ->
                    _receivedFriendRequests.value = receivedFriendRequests
                }
        }
    }

    fun sendFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.sendFriendRequest(uid, targetUid)
            if (success) {
                // 검색 쿼리를 재설정하여 실시간 업데이트 반영
                // TODO. 재설정 하지 않고 할 수 있는 방법 생각해보기. 현재는 이렇게 해야 UI 바로 반영됨.
                val currentQuery = _searchQuery.value
                if (currentQuery.isNotBlank()) {
                    fetchFilteredUsersAsFlow(uid, currentQuery)
                }
            }
            _operationStatus.value =
                if (success) "친구 요청이 성공적으로 전송되었습니다." else "친구 요청 전송에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }

    fun acceptFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.acceptFriendRequest(uid, targetUid)
            _operationStatus.value =
                if (success) "친구 요청을 수락했습니다." else "친구 요청 수락에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }

    fun rejectFriendRequest(uid: String, targetUid: String) {
        viewModelScope.launch {
            val success = fireBaseRepository.rejectFriendRequest(uid, targetUid)
            _operationStatus.value =
                if (success) "친구 요청을 거절했습니다." else "친구 요청 거절에 실패했습니다."
            Log.d("FriendViewModel", _operationStatus.value)
        }
    }
}
