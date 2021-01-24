package api.lemonico.service;


import api.lemonico.enums.ResponseCode;
import api.lemonico.request.AccountRegisterReq;

public interface AccountService {

    /**
     * アカウント登録処理
     * @param accountRegisterReq
     * @return
     */
    void createAccount(AccountRegisterReq accountRegisterReq);

//    /**
//     * 通过用户ID取得用户信息
//     * @param userId
//     * @return
//     */
//    UserInfo getUserInfo(Integer userId);
//
//    /**
//     * 通过用户ID取得用户信息
//     * @param userId
//     * @return
//     */
//    User getUser(Integer userId);

//    /**
//     * 验证注册邮箱
//     * @param email
//     * @param validateCode
//     * @return Enum<ResponseCode>
//     */
//    Enum<ResponseCode> processActivate(String email, String validateCode);
//
//    /**
//     * 用户收货地址保存
//     * @param userAddressReq
//     * @return Enum<ResponseCode>
//     */
//    Enum<ResponseCode> saveUserAddress(UserAddressReq userAddressReq);
//
//    /**
//     * 用户地址取得
//     * @param userId
//     * @return List<Address>
//     */
//    List<Address> getUserAddresses(Integer userId);
//
//    void changeDefaultAddress(Integer userId, Integer addressId);
}
