package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Grade;
import uz.mirmaxsudov.lmsbackend.service.base.lms.GradeService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "grade")
public class GradeController extends BaseCRUDController<Grade, GradeService> {
    public GradeController(GradeService service) {
        super(service);
    }
}