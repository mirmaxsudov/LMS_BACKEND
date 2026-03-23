package uz.mirmaxsudov.lmsbackend.service.impl.lms;

import org.springframework.stereotype.Service;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.repository.lms.GroupRepository;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;
import uz.mirmaxsudov.lmsbackend.service.impl.BaseCRUDServiceImpl;

@Service
public class GroupServiceImpl extends BaseCRUDServiceImpl<Group, GroupRepository> implements GroupService {
    public GroupServiceImpl(GroupRepository repository) {
        super(repository);
    }
}
