package uz.mirmaxsudov.lmsbackend.controller.lms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.lms.Room;
import uz.mirmaxsudov.lmsbackend.service.base.lms.RoomService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "room")
public class RoomController extends BaseCRUDController<Room, RoomService> {
    public RoomController(RoomService service) {
        super(service);
    }
}