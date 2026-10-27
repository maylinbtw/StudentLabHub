package com.slh.studentlabhub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.slh.studentlabhub.domain.model.LabStatus
import com.slh.studentlabhub.domain.model.WorkType

@Entity(
    tableName = "labs",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("subjectId")],
)
data class LabEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val type: WorkType = WorkType.LAB,
    val number: Int,
    val title: String,
    val status: LabStatus = LabStatus.NOT_DONE,
    val score: Int? = null,
    val notes: String = "",
    val deadline: Long? = null,
    val classroomTaskId: String? = null,
    val link: String? = null,
    @ColumnInfo(defaultValue = "0")
    val needsRedo: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val defendQueued: Boolean = false,
)