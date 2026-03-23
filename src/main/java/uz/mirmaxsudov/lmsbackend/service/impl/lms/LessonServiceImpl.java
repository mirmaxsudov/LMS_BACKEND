package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lesson;
import uz.mirmaxsudov.lmsbackend.repository.lms.LessonRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LessonService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class LessonServiceImpl extends BaseCRUDServiceImpl<Lesson, LessonRepository> implements LessonService {
    public LessonServiceImpl(LessonRepository repository) {
        super(repository);
    }
}
