package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.common.bean.Tc209_setting_template;
import com.lemonico.common.bean.Tc210_setting_yoto;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlValidationErrorException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.SettingDao;
import com.lemonico.store.service.MacroSettingService;
import com.lemonico.store.service.SettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 店鋪設定管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/store/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "店鋪設定管理")
public class SettingController
{

    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

    private final SettingService settingService;
    private final MacroSettingService macrosettingService;
    private final SettingDao settingDao;
    private final PathProps pathProps;

    /**
     * @Description: 店舗情報一覧
     * @Param: 顧客CD(Ms200Customer)
     * @return: json
     * @Author: wang
     * @Date: 2020/06/30
     */
    @ApiOperation(value = "店舗情報一覧の取得", notes = "設定")
    @GetMapping("setting/{client_id}")
    public JSONObject getStoreList(@PathVariable("client_id") String client_id, String email, String user_id) {
        JSONObject jsonObject = new JSONObject();
        // ログ出力
        logger.info("店舗情報一覧の取得:" + client_id);

        if (!Strings.isNullOrEmpty(email)) {
            // 処理開始
            jsonObject = settingService.checkStoreList(email);
        } else {
            jsonObject = settingService.getStoreList(user_id);
        }
        return jsonObject;

    }

    /**
     * @Description: 配送依頼先一覧
     * @Param: 依頼主マスタ-Ms012_sponsor_master
     * @return: json
     * @Date: 2020/06/01
     */
    @ApiOperation(value = "配送依頼先一覧の取得", notes = "設定")
    @GetMapping("senders/{client_id}")
    public JSONObject getDeliveryList(@PathVariable("client_id") String client_id, @RequestParam String sponsor_id) {
        JSONObject jsonObject = new JSONObject();
        // ログ出力
        logger.info("配送依頼先一覧の取得:" + client_id + " - " + sponsor_id);
        try {
            if (!Strings.isNullOrEmpty(sponsor_id)) {
                jsonObject = settingService.getDeliveryList(client_id, sponsor_id);
            } else {
                jsonObject = settingService.getDeliveryList(client_id, "#");
            }
            // 結果を返す
            return CommonUtils.success(jsonObject);
        } catch (Exception e) {
            logger.error("配送依頼先一覧の取得エラー", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 配送依頼先更新
     * @Param: Ms012_sponsor_master(依頼先マスタ)
     * @return: json
     * @Date: 2020/06/01
     */
    @RequestMapping(value = "senders/{client_id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "配送依頼先の更新", notes = "JSON")
    public JSONObject updateDelivery(@PathVariable("client_id") String client_id, String sponsor_id,
        @ApiParam(name = "jsonObject", value = "senders") @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {

        logger.info("配送依頼先の更新:" + client_id + " - " + sponsor_id);
        settingService.updateDeliveryList(jsonObject, request);

        return CommonUtils.success();
    }

    /**
     * @Description: 配送依頼先更新
     * @Param: Ms012_sponsor_master(依頼先マスタ)
     * @return: json
     * @Date: 2020/06/01
     */
    @RequestMapping(value = "senders/{client_id}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "配送依頼先の新規登録", notes = "JSON")
    public JSONObject createDelivery(@PathVariable("client_id") String client_id,
        @ApiParam(name = "jsonObject", value = "senders") @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {

        logger.info("配送依頼先の新規登録:" + client_id);
        settingService.createDeliveryList(jsonObject, request);
        return CommonUtils.success();
    }

    /**
     * アカウント情報の取得
     *
     * @param client_id
     * @return JSON
     * @date 2020-06-15
     */
    @ApiOperation(value = "アカウント情報の取得", notes = "JSON")
    @ResponseBody
    @GetMapping("setting/account/{client_id}")
    public JSONObject getClient(@PathVariable("client_id") String client_id) {
        // ログ出力
        logger.info("アカウント情報の取得:" + client_id);
        return settingService.getClient(client_id);

    }

    /**
     * アカウント情報(顧客グループ情報)編集
     *
     * @param jsonObject
     * @return 成否JSON
     * @date 2020-06-15
     */
    @ApiOperation(value = "アカウント情報の編集", notes = "JSON")
    @PutMapping("setting/account/{client_id}")
    public JSONObject updateClient(@PathVariable("client_id") String client_id,
        @ApiParam(
            name = "jsonObject",
            value = "customer_cd,client_id,corporation_flg,country_region,zip,tdfk, add1, tel, birthday, tnnm, url, permonth, contact_time, color") @RequestBody JSONObject jsonObject) {
        logger.info("アカウント情報の編集:" + client_id);
        CommonUtils.hashAllRequired(jsonObject,
            "corporation_flg,country_region,zip,tdfk, add1, tel, birthday, tnnm, color");
        return settingService.updateClient(jsonObject);
    }

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Param jsonObject : client_id,mail,new_mail
     * @description 店舗情報のメール更新
     * @author wang
     * @date 2020/07/01
     */
    @RequestMapping(value = "setting/email/{client_id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "店舗情報のメール更新", notes = "JSON")
    public JSONObject updateMail(@PathVariable("client_id") String client_id,
        @ApiParam(
            name = "jsonObject",
            value = "client_id,email,new_email,pass,new_pass,name,customer_cd,yoto") @RequestBody JSONObject jsonObject) {
        // ログ出力
        logger.info("店舗情報のメール更新:" + client_id);
        return settingService.updateMail(jsonObject);

    }

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Param jsonObject : client_id,mail,new_mail
     * @description 店舗情報のPASS更新
     * @author wang
     * @date 2020/07/01
     */
    @RequestMapping(value = "setting/pass/{userId}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "店舗情報のPASS更新", notes = "JSON")
    public JSONObject updatePass(@PathVariable("userId") String client_id,
        @ApiParam(
            name = "jsonObject", value = "userId,mail,new_mail,pass,new_pass") @RequestBody JSONObject jsonObject) {
        // ログ出力
        logger.info("店舗情報のPASS更新:" + client_id);
        return settingService.updatePass(jsonObject);

    }

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Param jsonObject : user_id,mail,new_mail
     * @description 店舗情報のユーザ更新
     * @author wang
     * @date 2020/07/01
     */
    @RequestMapping(value = "setting/user/{user_id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "店舗情報のユーザ更新", notes = "JSON")
    public JSONObject updateUser(@PathVariable("user_id") String user_id,
        @ApiParam(name = "jsonObject", value = "user_id") @RequestBody JSONObject jsonObject) {
        // ログ出力
        logger.info("店舗情報のユーザ更新:" + user_id);
        return settingService.updateUser(jsonObject);
    }

    /**
     * @Description: 配送先マスタ一覧
     * @Param: 依頼主マスタ-mc200_customer_delivery
     * @return: json
     * @Author: wang
     * @Date: 2020/06/01
     */
    @ApiOperation(value = "配送先マスタ一覧の取得", notes = "設定")
    @GetMapping("recipients/{client_id}")
    public JSONObject getCustomerDeliveryList(@PathVariable("client_id") String client_id, String delivery_id,
        String keyword) {
        JSONObject jsonObject = new JSONObject();
        // ログ出力
        logger.info("配送先マスタ一覧の取得:" + client_id + " - " + delivery_id);
        try {
            jsonObject = settingService.getCustomerDeliveryList(client_id, delivery_id, keyword);

            // 処理開始
            return CommonUtils.success(jsonObject);
        } catch (Exception e) {
            logger.error("配送先マスタ一覧の取得エラー", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 配送先マスタ更新
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Author: wang
     * @Date: 2020/06/01
     */
    @RequestMapping(value = "recipients/{client_id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "配送先マスタの更新", notes = "JSON")
    public JSONObject updateCustomerDelivery(@PathVariable("client_id") String client_id, String delivery_id,
        @ApiParam(name = "jsonObject", value = "recipients") @RequestBody JSONObject jsonObject) {

        logger.info("配送先マスタの更新:" + client_id + " - " + delivery_id);
        jsonObject = settingService.updateCustomerDeliveryList(jsonObject);

        return jsonObject;
    }

    /**
     * @Description: 配送先マスタの新規登録
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Author: wang
     * @Date: 2020/06/01
     */
    @RequestMapping(value = "recipients/{client_id}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "配送先マスタの新規登録", notes = "JSON")
    public JSONObject createCustomerDelivery(@PathVariable("client_id") String client_id,
        @ApiParam(name = "jsonObject", value = "recipients") @RequestBody JSONObject jsonObject) {

        logger.info("配送先マスタの新規登録:" + client_id);
        jsonObject = settingService.createCustomerDeliveryList(jsonObject);

        return jsonObject;
    }

    /**
     * @Description: 配送先マスタ削除
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Date: 2020/8/22
     */
    @RequestMapping(value = "recipients/{client_id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "配送先マスタの削除", notes = "JSON")
    public JSONObject deleteCustomerDelivery(@PathVariable("client_id") String client_id, String[] delivery_id) {
        logger.info("配送先マスタの削除:" + client_id);
        Integer result = settingService.deleteCustomerDelivery(delivery_id);

        return CommonUtils.success("SUCCESS");
    }

    /**
     * 依頼主ロゴ画像アップロード
     *
     * @param file
     * @param client_id
     * @param sponsor_id
     * @return JSON
     * @Date: 2020/06/10
     */
    @RequestMapping(value = "senders/images/upload", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "配送依頼先明細ロゴ追加", notes = "JSON")
    public void imagesUpload(@RequestParam("file") MultipartFile[] file, String client_id, String sponsor_id)
        throws IOException {

        // ログ出力
        logger.info("配送依頼先明細ロゴ:" + client_id);

        if (file.length < 1) {
            System.out.println("アップロードしたファイルは空です");
            return;
        }

        if (StringTools.isNullOrEmpty(sponsor_id)) {
            sponsor_id = settingService.getLastSponsorIdByClientId(client_id);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowTime = format.format(new Date());
        // 图片路径
        String uploadPath =
            pathProps.getRoot() + pathProps.getImage() + sponsor_id + "/" + client_id + "/" + nowTime + "/";

        File uploadDirectory = new File(uploadPath);
        if (uploadDirectory.exists()) {
            if (!uploadDirectory.isDirectory()) {
                uploadDirectory.delete();
            }
        } else {
            uploadDirectory.mkdirs();
        }

        for (MultipartFile files : file) {
            String fileName = files.getOriginalFilename();
            // 图片重命名，根据需要决定要不要
            // String[] tmpArr = fileName.split("\\.");
            // double random = Math.random();
            // int iRan = (int)(random*1000000);
            // long imgName = System.currentTimeMillis()+iRan;
            // String imgPath = uploadPath+imgName+"."+tmpArr[1];
            String imgPath = uploadPath + fileName;
            File outFile = new File(imgPath);
            if (!StringTools.isNullOrEmpty(sponsor_id)) {
                settingService.updateMasterLogo(client_id, sponsor_id, fileName);
            }
            // 拷贝文件到输出文件对象
            files.transferTo(outFile);
        }
        System.out.println("アップロード成功");
    }

    /**
     * @Param: client_id
     * @description: 获取店铺的所有员工
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @RequestMapping(value = "senders/getClientUserList/{client_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取店铺的所有员工", notes = "JSON")
    public JSONObject getClientUserList(@PathVariable("client_id") String client_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return settingService.getClientUserList(client_id);
    }

    /**
     * @Param: user_id
     * @description: 获取店铺员工信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @RequestMapping(value = "senders/getClientUserByUserId/{user_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取店铺员工信息", notes = "JSON")
    public JSONObject getClientUserByUserId(@PathVariable("user_id") String user_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        CommonUtils.hashAllRequired(jsonObject, "user_id");
        return settingService.getClientUserByUserId(user_id);
    }

    /**
     * @Param: jsonObject : client_id,login_id,login_pw,login_nm
     * @description: 新增属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @RequestMapping(value = "senders/insertClientUser", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增属于该店铺的用户", notes = "JSON")
    public JSONObject insertClientUser(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,login_id,login_nm");
        return settingService.insertClientUser(jsonObject, request);
    }

    /**
     * @Param: jsonObject userIdList
     * @description: 删除属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @RequestMapping(value = "senders/deleteClientUser", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除属于该店铺的用户", notes = "JSON")
    public JSONObject deleteClientUser(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "userIdList");
        return settingService.deleteClientUser(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 获取依頼主マスタ模板PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    @RequestMapping(value = "senders/getMasterPdf", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getMasterPdf(@RequestBody JSONObject jsonObject, Integer flag) {
        return settingService.getMasterPdf(jsonObject, flag);
    }

    /**
     * @param jsonObject
     * @description: 生成纳品书-作业指示书
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/8 15:47
     */
    @RequestMapping(value = "senders/getMasterDetailPdf", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getMasterDetailPdf(@RequestBody JSONObject jsonObject) {
        return settingService.getMasterDetailPdf(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 获取当前店铺默认依赖主
     * @return:
     * @date: 2020/9/29
     */
    @RequestMapping(value = "senders/getDefaultSponsor", method = RequestMethod.GET)
    @ResponseBody
    public Ms012_sponsor_master getDefaultSponsor(String client_id) {
        return settingDao.getSponsorDefaultInfo(client_id, 1);
    }

    /**
     * @throws Exception
     * @Description: CSV配送先マスタ登録をアップロードする
     * @Param:
     * @return: String
     * @Date: 2020/12/07
     */
    @PostMapping("/setting/recipients/import/{client_id}")
    @Transactional
    public JSONObject recipientsCsvUpload(HttpServletRequest req, @PathVariable("client_id") String client_id,
        @RequestParam("file") MultipartFile file) throws Exception {
        settingService.recipientsCsvUpload(client_id, req, file);
        return CommonUtils.success();
    }

    /**
     * @Param: client_id
     * @description: `店舗のマクロリストを取得し
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2021/2/26
     */
    @RequestMapping(value = "/setting/macro/list/{client_id}", method = RequestMethod.GET)
    @ApiOperation("店舗マクロリストを取得")
    public JSONObject getMacroList(@PathVariable("client_id") String client_id) {
        return macrosettingService.getMacroList(client_id);
    }

    /**
     * @Param: client_id
     * @description: `マクロIDより、マクロ情報を取得し
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2021/2/26
     */
    @RequestMapping(value = "/setting/macro/info/{id}", method = RequestMethod.GET)
    @ApiOperation("マクロ情報を取得")
    public JSONObject getMacroInfoById(@PathVariable("id") String id) {
        return macrosettingService.getMacroInfoById(id);
    }

    @PostMapping(value = "/setting/macro/save")
    @ApiOperation("マクロ情報を保存")
    public JSONObject saveMacroInfo(@RequestBody JSONObject jsonObject) throws NoSuchFieldException {
        return CommonUtils.success(macrosettingService.saveMacroInfo(jsonObject));
    }

    /**
     * @Description: 批量删除macro设定
     * @Param: id client_id
     * @return: Integer
     * @Date: 2021/03/29
     */
    @ApiOperation(value = "批量删除macro设定", notes = "批量删除macro设定")
    @DeleteMapping(value = "/setting/macro/delete/{client_id}")
    public JSONObject delMacro(@PathVariable String client_id, String ids) {
        return CommonUtils.success(macrosettingService.delMacro(client_id, ids));
    }

    /**
     * @Param:
     * @description: 检验macro优先顺序是否重复
     * @return: boolean
     * @date: 2021/04/06
     */
    @ApiOperation(value = "检验macro优先顺序是否重复", notes = "检验macro优先顺序是否重复")
    @GetMapping(value = "setting/priorityCheck/{client_id}")
    public boolean checkMacroPriorityExists(@PathVariable String client_id, Integer priority) {
        return macrosettingService.checkMacroPriorityExists(priority, client_id);
    }

    /**
     * @Param:
     * @description: macro信息修改
     * @return: JSONObject
     * @date: 2021/04/06
     */
    @ApiOperation(value = "macro信息修改", notes = "macro信息修改")
    @PostMapping(value = "setting/macro/edit/{client_id}")
    public JSONObject updateLocationInfo(@PathVariable("client_id") String client_id,
        Integer priority, Integer macro_status, String id, Integer old_priority, String repeat_id) {
        return macrosettingService.updateMacroInfo(client_id, priority, macro_status, id, old_priority, repeat_id);
    }

    /**
     * @Param:
     * @description: 检验macro名称是否重复
     * @return: boolean
     * @date: 2021/04/06
     */
    @ApiOperation(value = "检验macro名称是否重复", notes = "检验macro名称是否重复")
    @GetMapping(value = "/setting/macro/{client_id}")
    public boolean nameCheck(@PathVariable String client_id, String macro_name) {
        return macrosettingService.nameCheck(macro_name, client_id);
    }

    /**
     * @Param:
     * @description: csv导出模板信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/05/06
     */
    @RequestMapping(value = "csv/getSetTemplate", method = RequestMethod.GET)
    @ApiOperation("获取店铺模板信息")
    public JSONObject getSetTemplate(String warehouse_cd, String client_id, Integer template_cd, String yoto_id) {
        List<Tc209_setting_template> list = settingDao.getSetTemplate(warehouse_cd, client_id, template_cd, yoto_id);
        return CommonUtils.success(list);
    }

    /**
     * @Param:
     * @description: 获取不同csv模板title
     * @return:
     * @date: 2021/05/06
     */
    @RequestMapping(value = "csv/getTemplateTitle", method = RequestMethod.GET)
    @ApiOperation("获取不同csv模板的基本title")
    public JSONObject getTemplateTitle(String yoto_id) {
        List<Tc210_setting_yoto> list = settingDao.getTemplateTitle(yoto_id);
        return CommonUtils.success(list);
    }

    /**
     * @Description: //TODO 添加客户自定义csv数据模板
     *               @Date： 2021/5/8
     * @Param：
     * @return：
     */
    // @RequestMapping(value = "", method = RequestMethod.POST)
    @PostMapping("csv/insertCsvCustom")
    @ApiOperation("添加客户自定义csv数据模板")
    public JSONObject insertCsvCustom(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        return settingService.insertCsvCustom(jsonObject, servletRequest);
    }

    /**
     * @Param:
     * @description: 删除店铺自定义csv模板信息
     * @return:
     * @date: 2021/5/8
     */
    @RequestMapping(value = "csv/deleteCsvTemplate", method = RequestMethod.DELETE)
    @ApiOperation("删除店铺受注csv模板信息")
    public JSONObject deleteCustomTemplate(Integer template_cd) {
        settingDao.deleteCustomTemplate(template_cd);
        return CommonUtils.success();
    }

    /**
     * @Description: // 根据依赖主ID 删除依赖主master
     *               @Date： 2021/6/18
     * @Param：sponsor_id
     * @return：boolean
     */
    @ApiOperation(value = "删除依赖主master", notes = "据依赖主ID 删除依赖主master")
    @GetMapping(value = "setting/deleteSponsor/{client_id}")
    public JSONObject deleteSponsor(@PathVariable String client_id, String sponsor_id) {
        if (StringTools.isNullOrEmpty(sponsor_id)) {
            throw new PlValidationErrorException("依頼マスタIDを指定していません。");
        }
        settingService.deleteSponsor(client_id, sponsor_id);
        return CommonUtils.success();
    }

    /**
     * @description: 获取csv受注类型
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/24 10:39
     */
    @ApiOperation(value = "获取csv受注类型")
    @GetMapping(value = "order/getCsvTmp")
    public JSONObject getCsvTmp() {
        return settingService.getCsvTmp();
    }
}
