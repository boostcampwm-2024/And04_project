package com.and04.naturealbum.data.repository.local.impl

import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.LabelDao
import com.and04.naturealbum.data.repository.local.LabelRepository
import javax.inject.Inject

class LabelRepositoryImpl @Inject constructor(
    private val labelDao: LabelDao
): LabelRepository {
    override suspend fun getLabels(): List<Label> {
        return labelDao.getAllLabel()
    }

    override suspend fun getLabelById(id: Int): Label {
        return labelDao.getLabelById(id)
    }

    override suspend fun insertLabel(label: Label): Long {
        return labelDao.insertLabel(label)
    }
}
