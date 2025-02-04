package com.and04.naturealbum.data.dto

data class FirebaseLabel(
    val backgroundColor: String,
    val thumbnailUri: String,
    val fileName: String
)

data class FirebaseLabelResponse(
    val labelName: String = "",
    val backgroundColor: String = "",
    val thumbnailUri: String = "",
    val fileName: String = ""
)

data class FirebasePhotoInfo(
    val uri: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String,
    val datetime: String
)

data class FirebasePhotoInfoResponse(
    val fileName: String = "",
    val uri: String = "",
    val label: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String = "",
    val datetime: String = ""
): Sync

data class FirebaseFriend(
    val user: FirestoreUser = FirestoreUser(),
    val addedAt: String = ""
)

data class FirebaseFriendRequest(
    val user: FirestoreUser = FirestoreUser(),
    val requestedAt: String = "",
    val status: FriendStatus = FriendStatus.NORMAL,
)

data class FirestoreUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
) {
    companion object {
        const val UNKNOWN = "Unknown"
        const val EMPTY = ""
    }
}

data class FirestoreUserWithStatus(
    val user: FirestoreUser = FirestoreUser(),
    val status: FriendStatus = FriendStatus.NORMAL,
)

enum class FriendStatus() {
    NORMAL,
    SENT,
    RECEIVED,
    FRIEND;
}
