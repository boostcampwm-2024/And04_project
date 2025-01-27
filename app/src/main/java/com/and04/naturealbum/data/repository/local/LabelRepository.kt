package com.and04.naturealbum.data.repository.local

import com.and04.naturealbum.data.localdata.room.Label

interface LabelRepository {
    suspend fun getLabels(): List<Label>
    suspend fun getLabelById(id: Int): Label
    suspend fun insertLabel(label: Label): Long
}
