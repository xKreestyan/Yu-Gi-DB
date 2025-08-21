package com.example.yu_gi_db.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yu_gi_db.domain.repository.YuGiRepoInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class YuGiViewModel @Inject constructor(
    private val repo: YuGiRepoInterface
): ViewModel() {


}