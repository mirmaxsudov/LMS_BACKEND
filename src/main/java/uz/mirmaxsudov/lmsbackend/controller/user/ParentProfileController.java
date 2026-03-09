package uz.mirmaxsudov.lmsbackend.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.mirmaxsudov.lmsbackend.controller.BaseCRUDController;
import uz.mirmaxsudov.lmsbackend.model.entity.user.ParentProfile;
import uz.mirmaxsudov.lmsbackend.service.base.user.ParentProfileService;
import uz.mirmaxsudov.lmsbackend.util.APIUtil;

@RestController
@RequestMapping(APIUtil.API_BASE_URL + "parent")
public class ParentProfileController extends BaseCRUDController<ParentProfile, ParentProfileService> {
    public ParentProfileController(ParentProfileService service) {
        super(service);
    }
}