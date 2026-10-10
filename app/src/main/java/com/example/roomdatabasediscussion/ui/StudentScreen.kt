
package com.example.roomdatabasediscussion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roomdatabasediscussion.data.Student
import com.example.roomdatabasediscussion.viewmodel.StudentViewModel

private val darkBlue = Color(0xFF174A66)
private val lightBackground = Color(0xFFEDF3F7)

@Composable
fun StudentScreen(viewModel: StudentViewModel) {

    val students by viewModel.students.collectAsStateWithLifecycle()
    val totalStudents by viewModel.totalStudents.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val courseError by viewModel.courseError.collectAsStateWithLifecycle()
    val ageError by viewModel.ageError.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("BSCpE") }
    var age by remember { mutableStateOf("") }

    var showForm by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    val courses = listOf("BSCpE", "BSCS", "BSEMC")
    val filters = listOf("All Courses", "BSCpE", "BSCS", "BSEMC")

    fun clearForm() {
        name = ""
        email = ""
        course = "BSCpE"
        age = ""
        selectedStudent = null
        showForm = false
        viewModel.clearErrors()
    }

    fun editStudent(student: Student) {
        selectedStudent = student
        name = student.name
        email = student.email
        course = student.course
        age = if (student.age == 0) "" else student.age.toString()
        showForm = true
        viewModel.clearErrors()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackground)
            .systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = darkBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        "Student Management",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Manage student information",
                        color = Color(0xFFDCECF3)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Total Students: $totalStudents",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.searchStudents(it) },
                label = { Text("Search Student by Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Text(
                "Filter by Course",
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )

            Spacer(Modifier.height(6.dp))

            CourseDropdown(
                options = filters,
                selected = selectedCourse,
                onSelected = { viewModel.filterStudents(it) }
            )
        }

        item {
            Button(
                onClick = {
                    if (showForm) {
                        clearForm()
                    } else {
                        clearForm()
                        showForm = true
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkBlue
                )
            ) {
                Text(
                    if (showForm) "Close Form"
                    else "Add New Student"
                )
            }
        }

        if (showForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            if (selectedStudent == null)
                                "Add Student"
                            else
                                "Edit Student",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                viewModel.clearNameError()
                            },
                            label = { Text("Student Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = nameError.isNotEmpty(),
                            supportingText = {
                                if (nameError.isNotEmpty()) {
                                    Text(nameError)
                                }
                            }
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                viewModel.clearEmailError()
                            },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            isError = emailError.isNotEmpty(),
                            supportingText = {
                                if (emailError.isNotEmpty()) {
                                    Text(emailError)
                                }
                            }
                        )

                        Text("Course")

                        CourseDropdown(
                            options = courses,
                            selected = course,
                            onSelected = {
                                course = it
                                viewModel.clearCourseError()
                            }
                        )

                        if (courseError.isNotEmpty()) {
                            Text(
                                courseError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        OutlinedTextField(
                            value = age,
                            onValueChange = {
                                age = it
                                viewModel.clearAgeError()
                            },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = ageError.isNotEmpty(),
                            supportingText = {
                                if (ageError.isNotEmpty()) {
                                    Text(ageError)
                                }
                            }
                        )

                        Button(
                            onClick = {
                                val student = selectedStudent

                                if (student == null) {
                                    viewModel.addStudent(
                                        name,
                                        email,
                                        course,
                                        age
                                    ) {
                                        clearForm()
                                    }
                                } else {
                                    viewModel.updateStudent(
                                        student,
                                        name,
                                        email,
                                        course,
                                        age
                                    ) {
                                        clearForm()
                                    }
                                }
                            },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = darkBlue
                            )
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    if (selectedStudent == null)
                                        "Save Student"
                                    else
                                        "Save Changes"
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { clearForm() },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }

        if (message.isNotEmpty()) {
            item {
                Text(
                    message,
                    color = darkBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Text(
                "Student List",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
        }

        if (students.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        "No students found.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
        }

        items(students, key = { it.id }) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(student.email)
                    Text("Course: ${student.course}")

                    Text(
                        if (student.age == 0)
                            "Age: Not specified"
                        else
                            "Age: ${student.age}"
                    )

                    Text(
                        "Student ID: ${student.id}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editStudent(student) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = { studentToDelete = student },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Delete",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Delete Student") },
            text = {
                Text(
                    "Are you sure you want to delete ${student.name}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStudent(student)
                        studentToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { studentToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Course") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
