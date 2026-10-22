package com.slh.studentlabhub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slh.studentlabhub.data.local.dao.ClassroomDao
import com.slh.studentlabhub.data.local.dao.LabDao
import com.slh.studentlabhub.data.local.dao.SubjectDao
import com.slh.studentlabhub.data.local.entity.ClassroomCourseEntity
import com.slh.studentlabhub.data.local.entity.ClassroomTaskEntity
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        LabEntity::class,
        ClassroomCourseEntity::class,
        ClassroomTaskEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao

    abstract fun labDao(): LabDao

    abstract fun classroomDao(): ClassroomDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "studentlabhub.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}