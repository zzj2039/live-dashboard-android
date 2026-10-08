package com.example.livedashboardandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livedashboardandroid.api.ApiService
import com.example.livedashboardandroid.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    
    private val _currentData = MutableStateFlow<CurrentResponse?>(null)
    val currentData: StateFlow<CurrentResponse?> = _currentData.asStateFlow()

    private val _timelineData = MutableStateFlow<TimelineResponse?>(null)
    val timelineData: StateFlow<TimelineResponse?> = _timelineData.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurrentData()
        loadTimelineData()
    }

    fun loadCurrentData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val data = repository.getCurrent()
                _currentData.value = data
            } catch (e: Exception) {
                _error.value = "Failed to load current data: ${e.message}"
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
                val data = repository.getTimeline(date)
                _timelineData.value = data
            } catch (e: Exception) {
                _error.value = "Failed to load timeline data: ${e.message}"
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