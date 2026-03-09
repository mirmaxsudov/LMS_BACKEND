package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Lecture;
import uz.mirmaxsudov.lmsbackend.service.base.lms.LectureService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "lecture")
public class LectureController extends BaseCRUDController<Lecture, LectureService> {
    public LectureController(LectureService service) {
        super(service);
    }
}