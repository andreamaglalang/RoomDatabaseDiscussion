package com.example.roomdatabasediscussion.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDAO: StudentDao
) {

    val students: Flow<List<Student>> =
        studentDAO.getAllStudents()

    val studentCount: Flow<Int> =
        studentDAO.getStudentCount()

    fun searchStudents(search: String): Flow<List<Student>> {
        return studentDAO.searchStudents(search)
    }

    fun filterStudents(course: String): Flow<List<Student>> {
        return studentDAO.filterStudents(course)
    }

    fun searchAndFilterStudents(
        search: String,
        course: String
    ): Flow<List<Student>> {
        return studentDAO.searchAndFilterStudents(search, course)
    }

    suspend fun insertStudent(student: Student) {
        studentDAO.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        studentDAO.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        studentDAO.deleteStudent(student)
    }
}
