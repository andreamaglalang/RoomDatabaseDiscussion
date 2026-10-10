package com.example.roomdatabasediscussion.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY name")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :search || '%' ORDER BY name")
    fun searchStudents(search: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE course = :course ORDER BY name")
    fun filterStudents(course: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :search || '%' AND course = :course ORDER BY name")
    fun searchAndFilterStudents(
        search: String,
        course: String
    ): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}
