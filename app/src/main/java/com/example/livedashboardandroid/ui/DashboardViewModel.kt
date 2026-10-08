package com.example.livedashboardandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.livedashboardandroid.api.CurrentResponse
import com.example.livedashboardandroid.api.TimelineResponse
import com.example.livedashboardandroid.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _currentData = MutableStateFlow<CurrentResponse?>(null)
    val currentData = _currentData.asLiveData()

    private val _timelineData = MutableStateFlow<TimelineResponse?>(null)
    val timelineData = _timelineData.asLiveData()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asLiveData()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asLiveData()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asLiveData()

    init {
        loadCurrentData()
        loadTimelineData()
    }

    fun loadCurrentData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _currentData.value = repository.getCurrent()
            } catch (e: Exception) {
                _error.value = "加载当前数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTimelineData(date: LocalDate = _selectedDate.value) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _timelineData.value = repository.getTimeline(date)
            } catch (e: Exception) {
                _error.value = "加载时间线失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadTimelineData(date)
    }
}
