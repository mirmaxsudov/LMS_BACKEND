package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Assignment;
import uz.mirmaxsudov.lmsbackend.repository.lms.AssignmentRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AssignmentService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class AssignmentServiceImpl extends BaseCRUDServiceImpl<Assignment, AssignmentRepository> implements AssignmentService {
    public AssignmentServiceImpl(AssignmentRepository repository) {
        super(repository);
    }
}