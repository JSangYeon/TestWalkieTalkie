package jsy.sample.testwalkietalkie.data

import java.util.*

data class MediaTimeLine (
    val cal: Calendar,
    val hourMinute: String,
    val startPoint: Boolean, //시작 시각
    val endPoint: Boolean, //끝나는 시각
    val timeCheck: Boolean // 시간표시 필요한지 체크
)

