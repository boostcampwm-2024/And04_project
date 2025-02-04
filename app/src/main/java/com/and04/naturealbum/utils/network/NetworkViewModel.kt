package com.and04.naturealbum.utils.network

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkManager: NetworkManager
) : ViewModel() {
    val networkState: StateFlow<Int> get() = networkManager.networkState
}
