package edu.lespri.app.data.mapper

import edu.lespri.app.data.local.entity.LessonSessionEntity
import edu.lespri.app.data.local.entity.ScheduleRuleEntity
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.ScheduleRule
import edu.lespri.app.domain.model.SessionStatus

/**
 * Mapper dua arah antara entitas jadwal Room ([LessonSessionEntity], [ScheduleRuleEntity])
 * dan model domain ([LessonSession], [ScheduleRule]).
 */
fun LessonSessionEntity.toDomain(): LessonSession {
    return LessonSession(
        id = id,
        classId = classId,
        tutorId = tutorId,
        sessionDate = sessionDate,
        startTime = startTime,
        durationMinutes = durationMinutes,
        status = runCatching { SessionStatus.valueOf(status) }.getOrDefault(SessionStatus.SCHEDULED),
        sourceRuleId = sourceRuleId
    )
}

fun LessonSession.toEntity(): LessonSessionEntity {
    return LessonSessionEntity(
        id = id,
        classId = classId,
        tutorId = tutorId,
        sessionDate = sessionDate,
        startTime = startTime,
        durationMinutes = durationMinutes,
        status = status.name,
        sourceRuleId = sourceRuleId
    )
}

fun ScheduleRuleEntity.toDomain(): ScheduleRule {
    return ScheduleRule(
        id = id,
        classId = classId,
        tutorId = tutorId,
        weekday = weekday,
        startTime = startTime,
        durationMinutes = durationMinutes,
        effectiveFrom = effectiveFrom,
        effectiveUntil = effectiveUntil,
        status = status
    )
}

fun ScheduleRule.toEntity(): ScheduleRuleEntity {
    return ScheduleRuleEntity(
        id = id,
        classId = classId,
        tutorId = tutorId,
        weekday = weekday,
        startTime = startTime,
        durationMinutes = durationMinutes,
        effectiveFrom = effectiveFrom,
        effectiveUntil = effectiveUntil,
        status = status
    )
}
