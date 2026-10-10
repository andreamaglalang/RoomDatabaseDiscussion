
package com.example.roomdatabasediscussion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.roomdatabasediscussion.ui.StudentScreen
import com.example.roomdatabasediscussion.ui.theme.RoomDatabaseDiscussionTheme
import com.example.roomdatabasediscussion.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoomDatabaseDiscussionTheme {
                StudentScreen(viewModel = studentViewModel)
            }
        }
    }
}
