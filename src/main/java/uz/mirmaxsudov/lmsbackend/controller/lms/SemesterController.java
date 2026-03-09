package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Semester;
import uz.mirmaxsudov.lmsbackend.service.base.lms.SemesterService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "semester")
public class SemesterController extends BaseCRUDController<Semester, SemesterService> {
    public SemesterController(SemesterService service) {
        super(service);
    }
}