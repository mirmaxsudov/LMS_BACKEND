package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.repository.lms.EnrollmentRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.EnrollmentService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class EnrollmentServiceImpl extends BaseCRUDServiceImpl<Enrollment, EnrollmentRepository> implements EnrollmentService {
    public EnrollmentServiceImpl(EnrollmentRepository repository) {
        super(repository);
    }
}
