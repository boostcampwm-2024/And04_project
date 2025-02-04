package com.and04.naturealbum.ui.mypage.friendsearch

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus
import com.and04.naturealbum.ui.mypage.NoNetworkSocialContent
import com.and04.naturealbum.utils.network.NetworkState.DISCONNECTED
import com.and04.naturealbum.utils.network.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendSearchScreen(
    onBack: () -> Unit,
    friendViewModel: FriendViewModel,
    networkViewModel: NetworkViewModel,
) {
    val context = LocalContext.current
    val friendRequestStatus by friendViewModel.friendRequestStatus.collectAsStateWithLifecycle()
    val userWithStatusList by friendViewModel.searchResults.collectAsStateWithLifecycle()
    val networkState = networkViewModel.networkState.collectAsStateWithLifecycle()
    var textFieldState by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(friendRequestStatus) {
        friendRequestStatus?.let { success ->
            val message = if (success) {
                context.getString(R.string.friend_search_screen_friend_request_success)
            } else {
                context.getString(R.string.friend_search_screen_friend_request_fail)
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            friendViewModel.setFriendRequestStatusNull()
        }
    }

    LaunchedEffect(Unit) {
        friendViewModel.updateSearchQuery("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.friend_search_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState,
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expandedChange -> expanded = expandedChange },
                        placeholder = {
                            Text(text = stringResource(R.string.my_page_search_hint))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.my_page_search_bar_search_icon)
                            )
                        },
                        onQueryChange = { query ->
                            textFieldState = query
                            friendViewModel.updateSearchQuery(query)
                        },
                    )
                },
                expanded = false,
                onExpandedChange = {
                },
            ) {
            }

            if (networkState.value == DISCONNECTED) {
                NoNetworkSocialContent()
            } else {
                RequestedList(
                    userWithStatusList = userWithStatusList,
                    sendFriendRequest = friendViewModel::sendFriendRequest
                )
            }
        }
    }
}

@Composable
fun RequestedList(
    userWithStatusList: Map<String, FirestoreUserWithStatus>,
    sendFriendRequest: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (userWithStatusList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.freind_search_screen_no_result),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            items(userWithStatusList.entries.toList(), key = { entry -> entry.key }) { entry ->
                RequestedItem(
                    userWithStatus = entry.value,
                    sendFriendRequest = sendFriendRequest
                )
            }
        }
    }
}

@Composable
fun RequestedItem(
    userWithStatus: FirestoreUserWithStatus,
    sendFriendRequest: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = userWithStatus.user.photoUrl,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = userWithStatus.user.email,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            SuggestionChip(
                onClick = {
                    if (userWithStatus.status == FriendStatus.NORMAL) {
                        sendFriendRequest(userWithStatus.user.uid)
                    }
                },
                enabled = userWithStatus.status == FriendStatus.NORMAL,
                label = {
                    val text = when (userWithStatus.status) {
                        FriendStatus.SENT -> stringResource(R.string.my_page_friend_requested)
                        FriendStatus.RECEIVED -> stringResource(R.string.my_page_friend_request_received)
                        FriendStatus.FRIEND -> stringResource(R.string.my_page_friend)
                        else -> stringResource(R.string.my_page_friend_request)
                    }
                    Text(text = text)
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = when (userWithStatus.status) {
                        FriendStatus.NORMAL -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    labelColor = when (userWithStatus.status) {
                        FriendStatus.NORMAL -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSecondary
                    }
                )
            )
        }
    }

    HorizontalDivider(thickness = 1.dp)
}
