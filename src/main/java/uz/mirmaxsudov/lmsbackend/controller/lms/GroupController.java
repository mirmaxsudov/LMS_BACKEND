package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Group;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GroupService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "group")
public class GroupController extends BaseCRUDController<Group, GroupService> {
    public GroupController(GroupService service) {
        super(service);
    }
}