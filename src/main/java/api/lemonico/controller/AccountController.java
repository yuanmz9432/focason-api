package api.lemonico.controller;


import api.lemonico.enums.ResponseCode;
import api.lemonico.model.BaseAPIResponse;
import api.lemonico.request.AccountRegisterReq;
import api.lemonico.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AccountService  accountService;

    /**
     * アカウント登録処理
     * @param accountRegisterReq
     * @return BaseAPIResponse
     */
    @CrossOrigin
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseAPIResponse register(@RequestBody AccountRegisterReq accountRegisterReq) {
        logger.debug("アカウント登録処理開始");
        accountService.createAccount(accountRegisterReq);
        return BaseAPIResponse.success();
    }

}
