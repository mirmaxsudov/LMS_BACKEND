package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Department;
import uz.mirmaxsudov.lmsbackend.service.base.lms.DepartmentService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "department")
public class DepartmentController extends BaseCRUDController<Department, DepartmentService> {
    public DepartmentController(DepartmentService service) {
        super(service);
    }
}