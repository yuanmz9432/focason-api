package api.lemonico.controller;


import api.lemonico.info.BaseResponseInfo;
import api.lemonico.request.UserRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    /**
     * 用户注册
     * @param userRegisterReq
     * @return BaseResponseInfo
     */
    @CrossOrigin
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseResponseInfo register(@RequestBody UserRegisterReq userRegisterReq) {
        return null;
    }

}
