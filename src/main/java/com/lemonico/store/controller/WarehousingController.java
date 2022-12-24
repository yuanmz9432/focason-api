package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.core.annotation.RequestLimit;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.store.service.WarehousingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 入荷依頼管理コントローラー
 *
 * @since 1.0.0
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "入荷依頼管理")
public class WarehousingController
{
    private final WarehousingService warehousingService;

    /**
     * 入荷依頼検索・一覧
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫コード
     * @param status 入庫ステータス
     * @param search 搜索内容
     * @param startTime 依頼開始日
     * @param endTime 依頼終了日
     * @param tags_id タグID
     * @return 入荷依頼一覧
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehousings/{client_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "入庫依頼一覧", notes = "必ずJSON形式を入力してください")
    public JSONObject getWarehousingList(@PathVariable("client_id") String client_id, String warehouse_cd,
        String status, String search,
        String startTime, String endTime, String tags_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        jsonObject.put("warehouse_cd", warehouse_cd);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return warehousingService.getWarehousingList(jsonObject, status, search, startTime, endTime, tags_id);
    }

    /**
     * 入荷依頼を新規作成する
     *
     * @param jsonObject 入荷依頼情報
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehousings", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "入庫依頼作成", notes = "必ずJSON形式を入力してください")
    @Transactional
    public JSONObject createWarehousing(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "inspection_type,items");
        return warehousingService.createWarehousing(jsonObject, request);
    }

    /**
     * 入荷依頼を取り消す
     *
     * @param jsonObject 入荷依頼取消情報
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehousings", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "入庫依頼取消", notes = "必ずJSON形式を入力してください")
    @Transactional
    public JSONObject deleteWarehousing(
        @ApiParam(name = "jsonObject", value = "client_id,id") @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id");
        return warehousingService.deleteWarehousingsList(jsonObject, request);
    }

    /**
     * 入荷依頼を更新する
     *
     * @param jsonObject 入荷依頼情報
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehousings", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "入庫依頼更新", notes = "必ずJSON形式を入力してください")
    @Transactional
    public JSONObject updateWarehousing(
        @ApiParam(
            name = "jsonObject",
            value = "client_id,id,warehouse_cd,inspection_type") @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id,warehouse_cd,inspection_type");
        return warehousingService.updateWarehousings(jsonObject, request);
    }

    /**
     * 入荷依頼詳細情報を取得する
     *
     * @param jsonObject 入荷依頼情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehousings/getInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "入荷依頼詳細", notes = "必ずJSON形式を入力してください")
    @RequestLimit
    public JSONObject getInfoById(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id");
        return warehousingService.getInfoById(jsonObject);
    }

    /**
     * 入荷依頼CSVファイルをアップロードする
     *
     * @param httpServletRequest {@link HttpServletRequest}
     * @param client_id 店鋪ID
     * @param file CSVファイル
     * @param arrival_date 入荷予定日
     * @param inspection_type 検品タイプ
     * @return 処理結果情報
     * @全局異常
     */
    @RequestMapping(value = "/warehousings/warehousingCsvUpload", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "入庫依赖CSV登录", notes = "入庫依赖CSV登录")
    public JSONObject uploadWarehousingCsv(HttpServletRequest httpServletRequest, String client_id,
        @RequestParam("file") MultipartFile file, String arrival_date, String inspection_type) {
        return warehousingService.warehousingCsvUpload(httpServletRequest, client_id, file, arrival_date,
            inspection_type);
    }

    @RequestMapping(value = "/warehousings/getWarehousingPDF", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "入庫依赖PDF生成", notes = "必ずJSON形式を入力してください")
    public JSONObject createWarehousingPdf(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id");
        return warehousingService.getWarehousingPDF(jsonObject);
    }
}
