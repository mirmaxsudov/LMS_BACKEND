package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Submission;
import uz.mirmaxsudov.lmsbackend.service.base.lms.SubmissionService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "submission")
public class SubmissionController extends BaseCRUDController<Submission, SubmissionService> {
    public SubmissionController(SubmissionService service) {
        super(service);
    }
}