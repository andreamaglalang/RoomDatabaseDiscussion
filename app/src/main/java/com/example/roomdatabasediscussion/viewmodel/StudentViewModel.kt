package com.example.roomdatabasediscussion.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabasediscussion.data.Student
import com.example.roomdatabasediscussion.data.StudentDatabase
import com.example.roomdatabasediscussion.data.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = StudentDatabase.getData(application)
    private val repository = StudentRepository(database.studentDAO())

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _totalStudents = MutableStateFlow(0)
    val totalStudents: StateFlow<Int> = _totalStudents.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedCourse = MutableStateFlow("All Courses")
    val selectedCourse: StateFlow<String> = _selectedCourse.asStateFlow()

    private val _nameError = MutableStateFlow("")
    val nameError: StateFlow<String> = _nameError.asStateFlow()

    private val _emailError = MutableStateFlow("")
    val emailError: StateFlow<String> = _emailError.asStateFlow()

    private val _courseError = MutableStateFlow("")
    val courseError: StateFlow<String> = _courseError.asStateFlow()

    private val _ageError = MutableStateFlow("")
    val ageError: StateFlow<String> = _ageError.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var studentJob: Job? = null

    init {
        loadStudents()

        viewModelScope.launch {
            repository.studentCount.collect { count ->
                _totalStudents.value = count
            }
        }
    }

    private fun loadStudents() {
        studentJob?.cancel()

        studentJob = viewModelScope.launch {
            val search = _searchText.value.trim()
            val course = _selectedCourse.value

            val studentFlow = when {
                search.isEmpty() && course == "All Courses" ->
                    repository.students

                search.isNotEmpty() && course == "All Courses" ->
                    repository.searchStudents(search)

                search.isEmpty() ->
                    repository.filterStudents(course)

                else ->
                    repository.searchAndFilterStudents(search, course)
            }

            studentFlow.collect { studentList ->
                _students.value = studentList
            }
        }
    }

    fun searchStudents(search: String) {
        _searchText.value = search
        loadStudents()
    }

    fun filterStudents(course: String) {
        _selectedCourse.value = course
        loadStudents()
    }

    fun clearErrors() {
        _nameError.value = ""
        _emailError.value = ""
        _courseError.value = ""
        _ageError.value = ""
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun clearNameError() {
        _nameError.value = ""
    }

    fun clearEmailError() {
        _emailError.value = ""
    }

    fun clearCourseError() {
        _courseError.value = ""
    }

    fun clearAgeError() {
        _ageError.value = ""
    }

    private fun validateStudent(
        name: String,
        email: String,
        course: String,
        age: String
    ): Boolean {
        clearErrors()

        var valid = true

        if (name.trim().isEmpty()) {
            _nameError.value = "Name cannot be empty"
            valid = false
        }

        if (email.trim().isEmpty()) {
            _emailError.value = "Email cannot be empty"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _emailError.value = "Enter a valid email address"
            valid = false
        }

        if (course !in listOf("BSCpE", "BSCS", "BSEMC")) {
            _courseError.value = "Please select a valid course"
            valid = false
        }

        val studentAge = age.trim().toIntOrNull()

        if (age.trim().isEmpty()) {
            _ageError.value = "Age cannot be empty"
            valid = false
        } else if (studentAge == null || studentAge !in 1..120) {
            _ageError.value = "Enter an age from 1 to 120"
            valid = false
        }

        return valid
    }

    fun addStudent(
        name: String,
        email: String,
        course: String,
        age: String,
        onSuccess: () -> Unit
    ) {
        if (_isSaving.value) return

        if (!validateStudent(name, email, course, age)) {
            return
        }

        _isSaving.value = true

        viewModelScope.launch {
            try {
                val student = Student(
                    name = name.trim(),
                    email = email.trim(),
                    course = course,
                    age = age.trim().toInt()
                )

                repository.insertStudent(student)
                _message.value = "Student added successfully"
                onSuccess()

            } catch (e: Exception) {
                _message.value = "Failed to add student"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateStudent(
        student: Student,
        name: String,
        email: String,
        course: String,
        age: String,
        onSuccess: () -> Unit
    ) {
        if (_isSaving.value) return

        if (!validateStudent(name, email, course, age)) {
            return
        }

        _isSaving.value = true

        viewModelScope.launch {
            try {
                val updatedStudent = student.copy(
                    name = name.trim(),
                    email = email.trim(),
                    course = course,
                    age = age.trim().toInt()
                )

                repository.updateStudent(updatedStudent)
                _message.value = "Student updated successfully"
                onSuccess()

            } catch (e: Exception) {
                _message.value = "Failed to update student"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                _message.value = "Student deleted successfully"
            } catch (e: Exception) {
                _message.value = "Failed to delete student"
            }
        }
    }
}
