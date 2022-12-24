package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.store.service.WarehousingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 入荷依頼履歴管理コントローラー
 *
 * @since 1.0.0
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehousingHistoryController
{
    private final WarehousingService warehousingService;

    /**
     * 入荷依頼履歴CSVをダウンロードする
     *
     * @param client_id 店鋪ID
     * @param startTime 開始日時
     * @param endTime 終了日時
     * @param tags_id タグID
     * @param search 検索条件
     * @param status ステータス
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/warehusings/history/csvData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "入荷依頼CSVダウンロード", notes = "入荷依頼CSVダウンロード")
    public JSONObject getWarehousingHistories(String client_id, String startTime, String endTime, String tags_id,
        String search, int status) {
        return warehousingService.getHistoryCsvData(client_id, startTime, endTime, tags_id, search, status);
    }

    /**
     * 入荷依頼履歴CSVをダウンロードする
     *
     * @param client_id 店鋪ID
     * @param warehouse_cd 倉庫コード
     * @param status ステータス
     * @param search 検索条件
     * @param startTime 開始日時
     * @param endTime 終了日時
     * @param tags_id タグID
     * @param column カラム
     * @param sort ソート順
     * @return 入荷依頼CSVファイル情報
     * @since 1.0.0
     * @deprecated TODO 廃止予定
     */
    @RequestMapping(value = "/warehousings/csv/{client_id}", method = RequestMethod.GET)
    @ResponseBody
    @Deprecated
    @ApiOperation(value = "入庫履歴CSVダウンロード", notes = "必ずJSON形式を入力してください")
    public JSONObject downloadWarehousingHistoryCsv(@PathVariable("client_id") String client_id, String warehouse_cd,
        String status, String search,
        String startTime, String endTime, String tags_id, String column, String sort) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        jsonObject.put("warehouse_cd", warehouse_cd);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return warehousingService.getWarehousingsCsvList(jsonObject, status, search, startTime, endTime, tags_id,
            column, sort);
    }
}
