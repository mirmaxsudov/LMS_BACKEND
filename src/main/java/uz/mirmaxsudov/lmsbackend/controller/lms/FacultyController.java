package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Faculty;
import uz.mirmaxsudov.lmsbackend.service.base.lms.FacultyService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "faculty")
public class FacultyController extends BaseCRUDController<Faculty, FacultyService> {
    public FacultyController(FacultyService service) {
        super(service);
    }
}