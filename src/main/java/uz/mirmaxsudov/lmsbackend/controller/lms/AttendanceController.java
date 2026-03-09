package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Attendance;
import uz.mirmaxsudov.lmsbackend.service.base.lms.AttendanceService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "attendance")
public class AttendanceController extends BaseCRUDController<Attendance, AttendanceService> {
    public AttendanceController(AttendanceService service) {
        super(service);
    }
}