package com.example.multicountdowntimer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

data class Timer(val id: String = UUID.randomUUID().toString(), val label: String, var remainingTime: Long, var isPaused: Boolean = true)

class TimerViewModel : ViewModel() {
    private val _timers = MutableStateFlow<List<Timer>>(emptyList())
    val timers: StateFlow<List<Timer>> = _timers

    fun addTimer(label: String, timeInSeconds: Long) {
        val newTimer = Timer(label = label, remainingTime = timeInSeconds, isPaused = false)
        _timers.value = _timers.value + newTimer
    }

    fun pauseTimer(id: String) {
        _timers.value = _timers.value.map {
            if (it.id == id) it.copy(isPaused = !it.isPaused) else it
        }
    }

    fun resetTimer(id: String) {
        _timers.value = _timers.value.map {
            if (it.id == id) it.copy(remainingTime = 300, isPaused = true) else it
        }
    }

    fun deleteTimer(id: String) {
        _timers.value = _timers.value.filter { it.id != id }
    }

    fun updateTimers() {
        _timers.value = _timers.value.map {
            if (!it.isPaused && it.remainingTime > 0) {
                it.copy(remainingTime = it.remainingTime - 1)
            } else if (it.remainingTime <= 0) {
                // TODO: 触发通知，这里添加NotificationManager代码
                it.copy(isPaused = true)
            } else it
        }
    }
}
