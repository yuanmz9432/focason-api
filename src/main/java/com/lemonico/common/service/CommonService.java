package com.lemonico.common.service;



import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.MailBean;
import com.lemonico.common.bean.Ms010_product_size;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.MailTools;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.apiLimit.GetIp;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonService
{
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    private final CommonFunctionDao commonFunctionDao;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String MAIL_SENDER;

    @Value("${domain}")
    private String domain;

    /**
     * 倉庫と店舗に関する情報を取得する
     *
     * @param warehouse_cd 倉庫コード
     * @param request {@link HttpServletRequest}
     * @return {@link Ms201_client}
     */
    public List<Ms201_client> getClientsByWarehouseCd(String warehouse_cd, HttpServletRequest request) {

        List<Ms201_client> clients = commonFunctionDao.getClientInfomation(warehouse_cd);
        // ip符合的店铺
        String ip = GetIp.getIpAddress(request);
        ArrayList<Ms201_client> accordClients = new ArrayList<>();
        clients.forEach(ms201 -> {
            if (!StringTools.isNullOrEmpty(ms201.getIp_address())) {
                List<String> storeIps =
                    Splitter.on(",").omitEmptyStrings().trimResults().splitToList(ms201.getIp_address());
                if (storeIps.contains(ip)) {
                    accordClients.add(ms201);
                }
            } else {
                accordClients.add(ms201);
            }
        });
        return accordClients;
    }

    /**
     * @Description: 商品サイズ
     * @Param: なし
     * @return: List
     * @Date: 2020/9/14
     */
    public List<Ms010_product_size> getProductSize() {
        return commonFunctionDao.getProductSize();
    }


    // 不确定附件是固定还是客户选择
    // 添付ファイルが固定されているか、顧客が選択しているかは不確定です
    // public boolean sendMail(MailBean mailBean, MultipartFile multipartFile){
    public boolean sendMail(MailBean mailBean) {
        MimeMessage mimeMailMessage = null;
        try {
            logger.info("スタート");
            mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);

            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent(), true);


            String filePath = mailBean.getFilePath();
            if (!StringTools.isNullOrEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    MimeMultipart msgMultipart = new MimeMultipart("mixed");
                    MimeBodyPart filePart = new MimeBodyPart();
                    FileDataSource dataSource = new FileDataSource(file);
                    DataHandler dataHandler = new DataHandler(dataSource);
                    filePart.setDataHandler(dataHandler);
                    filePart.setFileName(file.getName());
                    mimeMailMessage.setContent(msgMultipart);
                    msgMultipart.addBodyPart(filePart);
                }
            }
            javaMailSender.send(mimeMailMessage);
            logger.info(" 終了");
            logger.info("発送成功");
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * @Param * @param: email
     * @description: メールを送る
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/10
     */
    public JSONObject sendEmail(String email, Integer status) {
        MailTools mailTools = new MailTools();
        MailBean mailBean = null;
        // 0 注册 ，1 找回密码
        if (status == 0) {
            mailBean = mailTools.registerTemplate(email, domain);
        }
        if (status == 1) {
            mailBean = mailTools.changePwdTemplate(email, domain);
        }
        assert mailBean != null;
        sendMail(mailBean);
        return CommonUtils.success();
    }

    /**
     * @param: key
     * @description: メールが失効するかどうかを検証する
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/10
     */
    public JSONObject clickEmail(String key, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String startTime = (String) session.getAttribute(key);
        if (startTime == null) {
            return CommonUtils.failure(ErrorCode.EMAIL_INCORRECT);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        int time = 0;
        try {
            long start = simpleDateFormat.parse(startTime).getTime();
            long end = simpleDateFormat.parse(format).getTime();
            time = (int) ((end - start) / (1000));
        } catch (ParseException e) {
            return CommonUtils.failure(ErrorCode.DATA_TRANSFER_FAILED);
        }
        if (time > (5 * 60)) {
            return CommonUtils.failure(ErrorCode.EMAIL_INCORRECT);
        }
        return CommonUtils.success();
    }
}
