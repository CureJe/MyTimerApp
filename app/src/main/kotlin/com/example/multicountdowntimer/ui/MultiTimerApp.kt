package com.example.multicountdowntimer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multicountdowntimer.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MultiTimerApp(viewModel: TimerViewModel = viewModel()) {
    val timers by viewModel.timers.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateTimers()
            delay(1000)
        }
    }

    if (showDialog.value) {
        AddTimerDialog(onAdd = { label, time ->
            viewModel.addTimer(label, time)
            showDialog.value = false
        }, onCancel = { showDialog.value = false })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("MultiCountdown Timer") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Timer")
            }
        }
    ) { padding ->
        if (timers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("添加第一个计时器")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(timers) { timer ->
                    TimerCard(timer, onPause = { viewModel.pauseTimer(timer.id) }, onReset = { viewModel.resetTimer(timer.id) }, onDelete = { viewModel.deleteTimer(timer.id) })
                }
            }
        }
    }
}

@Composable
fun AddTimerDialog(onAdd: (String, Long) -> Unit, onCancel: () -> Unit) {
    // 简单对话框实现，输入标签和时间（秒）
    var label by remember { mutableStateOf("Timer") }
    var time by remember { mutableStateOf("300") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("添加计时器") },
        text = {
            Column {
                TextField(value = label, onValueChange = { label = it }, label = { Text("标签") })
                TextField(value = time, onValueChange = { time = it }, label = { Text("时间 (秒)") })
            }
        },
        confirmButton = { Button(onClick = { onAdd(label, time.toLongOrNull() ?: 300) }) { Text("添加") } },
        dismissButton = { Button(onClick = onCancel) { Text("取消") } }
    )
}

@Composable
fun TimerCard(timer: Timer, onPause: () -> Unit, onReset: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(timer.label, style = MaterialTheme.typography.titleMedium)
            Text(formatTime(timer.remainingTime), style = MaterialTheme.typography.headlineLarge)
            Row {
                Button(onClick = onPause) { Text(if (timer.isPaused) "恢复" else "暂停") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onReset) { Text("重置") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) { Text("删除") }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
