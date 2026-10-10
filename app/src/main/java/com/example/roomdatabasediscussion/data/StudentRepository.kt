package com.example.roomdatabasediscussion.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDAO: StudentDao) {

    val students: Flow<List<Student>>
        get() = studentDAO.getAllStudents()

    suspend fun insertStudent(student: Student){
        studentDAO.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student){
        studentDAO.deleteStudent(student)
    }
}