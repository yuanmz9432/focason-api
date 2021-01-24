package api.lemonico.service;

import api.lemonico.dao.AccountCustomDao;
import api.lemonico.dao.AccountDao;
import api.lemonico.entity.Account;
import api.lemonico.enums.ResponseCode;
import api.lemonico.exception.LemonicoAPIException;
import api.lemonico.request.AccountRegisterReq;
import api.lemonico.util.BCryptEncoder;
import api.lemonico.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {

    Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    AccountDao accountDao;

    @Autowired
    AccountCustomDao accountCustomDao;

    @Override
    @Transactional
    public void createAccount(AccountRegisterReq accountRegisterReq) {

        // メールアドレスの重複チェック
        Account existedUser = accountCustomDao.selectAccountByEmail(accountRegisterReq.getEmail());
        if(Objects.nonNull(existedUser)) {
            throw new LemonicoAPIException(ResponseCode.ACCOUNT_IS_EXISTED);
        }

        // データベースに保存する
        Account account = new Account();
        account.setFirstName(accountRegisterReq.getFirstName());
        account.setLastName(accountRegisterReq.getLastName());
        account.setEmail(accountRegisterReq.getEmail());
        account.setPassword(BCryptEncoder.getInstance().encode(accountRegisterReq.getPassword()));
        accountDao.insert(account);

//        EmailUtil.send(accountRegisterReq.getEmail(), createMailContent(account));
    }

//    @Override
//    public Enum<ResponseCode> processActivate(String email, String validateCode) {
//        co.jp.geekfun.horizon.entity.User user = userCustomDao.selectUserByEmail(email);
//        if(Objects.isNull(user)){
//            return ResponseCode.USER_NOT_EXIST;
//        }
//        if(user.getValidateCode().equals(validateCode) && LocalDateTime.now().isBefore(user.getRegisterTime())){
//            user.setStatus(AuthenticationStatus.AUTHENTICATED.getValue());
//            userDao.update(user);
//        }else{
//            return ResponseCode.WRONG_VALIDATE_CODE;
//        }
//        logger.info("验证成功！");
//        return null;
//    }

    /**
     * バリデーションコードを作る
     * @return int
     */
    private static int createValidateCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    /**
     * 编辑所要发送的邮件内容 TODO
     */
    private static String createMailContent(Account account){
        StringBuffer emailContent=new StringBuffer("请点击下方连接：</br>");
        emailContent.append("http://localhost:8080/activate_servlet?email=");
        emailContent.append(account.getEmail());
        emailContent.append("&validate_code=");
        emailContent.append(createValidateCode());
        return emailContent.toString();
    }
}
