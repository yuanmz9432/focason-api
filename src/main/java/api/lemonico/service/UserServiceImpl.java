package api.lemonico.service;

import api.lemonico.dao.UserCustomDao;
import api.lemonico.dao.UserDao;
import api.lemonico.entity.User;
import api.lemonico.enums.ResponseCode;
import api.lemonico.exception.LemonicoAPIException;
import api.lemonico.model.UserInfo;
import api.lemonico.request.AccountReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserDao userDao;

    @Autowired
    UserCustomDao userCustomDao;

    @Override
    @Transactional
    public void createAccount(AccountReq accountReq) {

        // メールアドレスの重複チェック
//        Account existedUser = userCustomDao.selectAccountByEmail(accountReq.getEmail());
//        if(Objects.nonNull(existedUser)) {
//            throw new LemonicoAPIException(ResponseCode.ACCOUNT_IS_EXISTED);
//        }

        // データベースに保存する
        User account = new User();
        account.setUid(accountReq.getUid());
        account.setFirstName(accountReq.getFirstName());
        account.setLastName(accountReq.getLastName());
        account.setSex(accountReq.getSex());
        account.setYear(accountReq.getYear());
        account.setMonth(accountReq.getMonth());
        account.setDay(accountReq.getDay());
        account.setEmail(accountReq.getEmail());
        account.setEmailVerified(accountReq.getEmailVerified());
        userDao.insert(account);
    }

    @Override
    public UserInfo getAccountInfo(String uid) {
        User user  = userDao.selectById(uid);
        if(Objects.isNull(user)) {
            throw new LemonicoAPIException(ResponseCode.ACCOUNT_IS_NOT_EXIST);
        }
        return new UserInfo(user);
    }

    @Override
    public void updateAccount(AccountReq accountReq) {

    }

}
