package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Assignment;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AssignmentService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "assignment")
public class AssignmentController extends BaseCRUDController<Assignment, AssignmentService> {
    public AssignmentController(AssignmentService service) {
        super(service);
    }
}