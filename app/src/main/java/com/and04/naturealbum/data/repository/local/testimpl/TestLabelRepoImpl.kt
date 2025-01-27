package com.and04.naturealbum.data.repository.local.testimpl

import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.repository.local.LabelRepository

class TestLabelRepoImpl: LabelRepository {
    override suspend fun getLabels(): List<Label> {
        return emptyList()
    }

    override suspend fun getLabelById(id: Int): Label {
        return Label.emptyLabel()
    }

    override suspend fun insertLabel(label: Label): Long {
        return 0
    }
}
