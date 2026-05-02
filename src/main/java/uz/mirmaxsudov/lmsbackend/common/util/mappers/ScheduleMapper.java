package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.model.response.lms.ScheduleResponse;

public final class ScheduleMapper {

    private ScheduleMapper() {
    }

    public static ScheduleResponse toResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .groupId(schedule.getGroup() == null ? null : schedule.getGroup().getId())
                .groupName(schedule.getGroup() == null ? null : schedule.getGroup().getGroupName())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }
}
