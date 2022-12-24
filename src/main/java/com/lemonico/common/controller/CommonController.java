package com.lemonico.common.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.MailBean;
import com.lemonico.common.bean.Ms005_address;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.common.service.CommonService;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 共通機能コントロール
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "共通機能")
public class CommonController
{
    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    private final CommonService commonService;

    private final LoginService loginService;
    private final LoginDao loginDao;

    @Value("${domain}")
    private String domain;

    /**
     * AWSのヘルスチェック
     *
     * @since 1.0.0
     */
    @ApiOperation(value = "AWSのヘルスチェック", notes = "AWSのヘルスチェック")
    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public ResponseEntity<Void> heartbeat() {
        logger.debug("Application is running.");
        return ResponseEntity.ok().build();
    }

    /**
     * メール送信
     *
     * @param email メールアドレス
     * @param status ステータス
     * @return 処理結果
     */
    @RequestMapping(value = "/sendEmail", method = RequestMethod.GET)
    @ApiOperation(value = "メールを送信する")
    public JSONObject sendEmail(String email, Integer status) {
        return commonService.sendEmail(email, status);
    }

    /**
     * メール送信をテストする
     *
     * @param email メールアドレス
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "邮件测试接口", notes = "邮件测试接口")
    @GetMapping("/client/mailTest")
    public JSONObject mailTest(String email) {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(new String[] {
            email
        });
        mailBean.setSubject("這個是測試郵件");
        final String sb = "<h1>■入庫補充</h1>" + " " + "<h4 style='font-weight:normal'>サンロジカスタマーサポートです。<h4/>" +
            " " + "<h4 style='font-weight:normal'>現在、下記の商品の在庫をご確認頂き、早めに入庫をお願い致します。<h4/>" +
            "<h4 style='font-weight:normal'>配送可在庫が補充設定の数値以下になっております。<h4/>" +
            "------------------------------------------" +
            "<h4 style='font-weight:normal'>入庫について" +
            "<h4 style='font-weight:normal'>サンロジ提携倉庫には日々多数のユーザー様からの入庫をいただきます。<h4/>" +
            "<h4 style='font-weight:normal'>それらの荷物は、まず着荷登録を行い、ユーザー様および入庫依頼を特定、その後順次入庫作業を行います。<h4/>" +
            "<h4 style='font-weight:normal'>その際、荷主の特定ができないもの、入庫依頼が無いものについては別途にご連絡いたします。<h4/>" + " " +
            "<h4 style='font-weight:normal'>入庫作業ではまず皆様に作成頂いた入庫依頼を元に、受け取った商品の内容・個数を検めます。<h4/>" +
            "<h4 style='font-weight:normal'>依頼にない商品があった場合、このときにサポート担当者からご連絡いたします。<h4/>" + " " +
            " " + "<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>" +
            "<h4 style='font-weight:normal'>サンロジ カスタマーサポート<h4/>" +
            "<h4 style='font-weight:normal'>お問い合わせ<h4/>" +
            "<h3><a href='" + domain + "/support'>" + domain + "/support<a/><h3/>" + " " +
            "<h4 style='font-weight:normal'>サポート対応時間：平日10:00～18:00<h4/>" +
            "<h4 style='font-weight:normal'>※こちらのメールはシステムから自動で送信されております。<h4/>";
        mailBean.setContent(sb);
        commonService.sendMail(mailBean);
        return CommonUtils.success("发送成功");
    }

    /**
     * 郵便番号による住所を検索する
     *
     * @param zipValue 郵便番号
     * @return {@link Ms005_address}
     * @since 1.0.0
     */
    @RequestMapping(value = "/zip/{zipValue}", method = RequestMethod.GET)
    @ApiOperation(value = "获取邮编番号信息")
    public JSONObject getZipInfo(@PathVariable("zipValue") String zipValue) {
        return loginService.getZipInfo(CommonUtils.getZip(zipValue));
    }

    /**
     * 郵便番号による住所を検索する（認証不要）
     *
     * @param jsonObject 郵便情報
     * @return {@link Ms005_address}
     * @since 1.0.0
     */
    @RequestMapping(value = "/zip/info/token", method = RequestMethod.POST)
    @ApiOperation(value = "获取邮编番号信息不包含token")
    public JSONObject getZipInfoWithoutToken(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "zip");
        String zipValue = jsonObject.getString("zip");
        return loginService.getZipInfo(CommonUtils.getZip(zipValue));
    }

    /**
     * 都道府県を取得する
     *
     * @return 都道府県リスト
     * @since 1.0.0
     */
    @RequestMapping(value = "/prefecture", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有都道府県")
    public JSONObject getPrefectureList() {
        return loginService.getPrefectureList();
    }

    /**
     * 都道府県を取得する（認証不要）
     *
     * @return 都道府県リスト
     * @since 1.0.0
     */
    @RequestMapping(value = "/prefecture/token", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有都道府県")
    public JSONObject getPrefectureListWithoutToken() {
        return loginService.getPrefectureList();
    }

    /**
     * @description: 獲取首頁通知信息
     * @return: Ms015_news
     * @date: 2021/02/02
     */
    @RequestMapping(value = "/login/getNewsInfo", method = RequestMethod.GET)
    @ApiOperation(value = "獲取首頁通知信息")
    public JSONObject getNewsInfo() {
        return CommonUtils.success(loginDao.getNewsInfo());
    }

}
