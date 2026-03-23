package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Schedule;
import uz.mirmaxsudov.lmsbackend.repository.lms.ScheduleRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.ScheduleService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class ScheduleServiceImpl extends BaseCRUDServiceImpl<Schedule, ScheduleRepository> implements ScheduleService {
    public ScheduleServiceImpl(ScheduleRepository repository) {
        super(repository);
    }
}
