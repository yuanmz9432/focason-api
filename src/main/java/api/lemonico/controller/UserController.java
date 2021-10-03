package api.lemonico.controller;


import api.lemonico.model.UserInfo;
import api.lemonico.model.BaseAPIResponse;
import api.lemonico.request.AccountReq;
import api.lemonico.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    /**
     * アカウント登録処理
     * @param accountReq
     * @return BaseAPIResponse
     */
    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseAPIResponse register(@RequestBody AccountReq accountReq) {
        logger.debug("アカウント登録処理開始");
        userService.createAccount(accountReq);
        return BaseAPIResponse.success();
    }

    /**
     * アカウント更新処理
     * @param accountReq
     * @return BaseAPIResponse
     */
    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public BaseAPIResponse update(@RequestBody AccountReq accountReq) {
        logger.debug("アカウント更新処理開始");
        userService.updateAccount(accountReq);
        return BaseAPIResponse.success();
    }

    /**
     * アカウント更新処理
     * @param uid
     * @return BaseAPIResponse
     */
    @CrossOrigin
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public BaseAPIResponse getUserInfo(@RequestParam(name = "uid") String uid) {
        logger.debug("アカウント情報取得処理開始");
        UserInfo userInfo = userService.getAccountInfo(uid);
        return BaseAPIResponse.success(userInfo);
    }

}
