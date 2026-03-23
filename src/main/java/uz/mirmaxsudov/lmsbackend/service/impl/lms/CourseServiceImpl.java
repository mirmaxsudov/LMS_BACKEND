package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Course;
import uz.mirmaxsudov.lmsbackend.repository.lms.CourseRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.CourseService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class CourseServiceImpl extends BaseCRUDServiceImpl<Course, CourseRepository> implements CourseService {
    public CourseServiceImpl(CourseRepository repository) {
        super(repository);
    }
}
