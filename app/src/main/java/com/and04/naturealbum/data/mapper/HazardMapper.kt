package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.GreenEyeDto
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus

object HazardMapper {
    fun mapToPassOrFail(greenEyeDto: GreenEyeDto): HazardAnalyzeStatus {
        val result = greenEyeDto.images?.firstOrNull() ?: return HazardAnalyzeStatus.FAIL

        val mostHeightScore = result.confidence
        val porn = result.result?.porn?.confidence
        val adult = result.result?.adult?.confidence

        return if (mostHeightScore == porn || mostHeightScore == adult) {
            HazardAnalyzeStatus.FAIL
        } else {
            HazardAnalyzeStatus.PASS
        }
    }
}
