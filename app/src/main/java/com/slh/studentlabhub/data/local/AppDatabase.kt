package com.slh.studentlabhub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.slh.studentlabhub.data.local.dao.ClassroomDao
import com.slh.studentlabhub.data.local.dao.LabDao
import com.slh.studentlabhub.data.local.dao.ReferenceDao
import com.slh.studentlabhub.data.local.dao.SubjectDao
import com.slh.studentlabhub.data.local.entity.ClassroomCourseEntity
import com.slh.studentlabhub.data.local.entity.ClassroomTaskEntity
import com.slh.studentlabhub.data.local.entity.LabEntity
import com.slh.studentlabhub.data.local.entity.RefCategoryEntity
import com.slh.studentlabhub.data.local.entity.RefEntryEntity
import com.slh.studentlabhub.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        LabEntity::class,
        ClassroomCourseEntity::class,
        ClassroomTaskEntity::class,
        RefCategoryEntity::class,
        RefEntryEntity::class,
    ],
    version = 7,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao

    abstract fun labDao(): LabDao

    abstract fun classroomDao(): ClassroomDao

    abstract fun referenceDao(): ReferenceDao

    companion object {
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ref_categories` (" +
                        "`id` TEXT NOT NULL, " +
                        "`tech` TEXT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`position` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ref_entries` (" +
                        "`id` TEXT NOT NULL, " +
                        "`categoryId` TEXT NOT NULL, " +
                        "`tech` TEXT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`example` TEXT NOT NULL, " +
                        "`position` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ref_entries_categoryId` " +
                        "ON `ref_entries` (`categoryId`)",
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `subjects` ADD COLUMN `classroomCourseId` TEXT")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `ref_entries` ADD COLUMN `source` TEXT")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `subjects` ADD COLUMN `archived` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "studentlabhub.db")
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                .fallbackToDestructiveMigrationFrom(true, 1, 2)
                .build()
    }
}
