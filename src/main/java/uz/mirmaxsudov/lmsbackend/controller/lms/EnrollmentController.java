package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Enrollment;
import uz.mirmaxsudov.lmsbackend.service.base.lms.EnrollmentService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "enrollment")
public class EnrollmentController extends BaseCRUDController<Enrollment, EnrollmentService> {
    public EnrollmentController(EnrollmentService service) {
        super(service);
    }
}