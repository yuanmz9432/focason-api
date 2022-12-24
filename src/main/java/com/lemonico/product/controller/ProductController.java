package com.lemonico.product.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc100_product;
import com.lemonico.common.bean.Mc101_product_tag;
import com.lemonico.common.bean.Ms008_items;
import com.lemonico.common.bean.Ms010_product_size;
import com.lemonico.common.service.CommonService;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.bean.ProductRecord;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
 * 商品マスタコントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController
{
    /**
     * コレクションリソースURI
     */
    private static final String PRODUCT_COLLECTION_RESOURCE_URI = "/products";

    /**
     * メンバーリソースURI
     */
    private static final String PRODUCT_RESOURCE_URI = PRODUCT_COLLECTION_RESOURCE_URI + "/{id}";

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final CommonService commonService;
    private final PathProps pathProps;

    /**
     * 商品ラベルを印刷する
     *
     * @param jsonObject 商品情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品ラベル印刷", notes = "商品ラベル印刷")
    @PostMapping(value = "/wms/items/label_pdf")
    public JSONObject itemsLabelPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "item");
        return productService.itemsLabelPDF(jsonObject);
    }

    /**
     * 箱ラベルを印刷する
     *
     * @param jsonObject 商品情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "箱ラベル印刷", notes = "箱ラベル印刷")
    @PostMapping(value = "/wms/items/boxlabel_pdf")
    public JSONObject itemsBoxLabelPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "item");
        return productService.itemsBoxLabelPDF(jsonObject);
    }

    /**
     * 商品サイズを設定する
     *
     * @param warehouse_cd 倉庫コード
     * @param client_id 店鋪ID
     * @param product_id 商品ID
     * @param weight 重量
     * @param size サイズ
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品size设定", notes = "商品size设定")
    @PostMapping(value = "/wms/items/size/{warehouse_cd}")
    public Integer setProductSize(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String product_id, Double weight, String size) {
        return productService.setProductSize(warehouse_cd, client_id, product_id, weight, size);
    }

    /**
     * 商品のロケーション情報を取得する
     *
     * @param warehouse_cd 倉庫コード
     * @param client_id 店鋪ID
     * @param product_id 商品ID
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "获取商品货架信息", notes = "获取商品货架信息")
    @GetMapping(value = "/wms/items/location/{warehouse_cd}")
    public JSONObject getProductLocation(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String product_id) {
        return CommonUtils.success(productService.getProductLocation(warehouse_cd, client_id,
            product_id));
    }

    /**
     * 商品ロケーションを移動する
     *
     * @param jsonObject 商品移動情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品货架移动", notes = "商品货架移动")
    @PostMapping(value = "/wms/items/location/move")
    @Transactional
    public JSONObject moveProductLocation(@RequestBody JSONObject jsonObject) {
        productService.moveProductLocation(jsonObject);
        return CommonUtils.success();
    }

    /**
     * 店鋪ごとの商品IDと商品コードを取得する
     *
     * @param client_id 店鋪ID
     * @param searchName 検索キーワード
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "根据店铺获取商品的code和商品Id")
    @GetMapping(value = "/wms/items/productInfo/{client_id}")
    public JSONObject getProductInfo(@PathVariable("client_id") String client_id, String searchName) {
        return productService.getProductCodeList(client_id, searchName);
    }

    /**
     * 通常商品検索
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫コード
     * @param product_id 商品ID
     * @param search キーワード
     * @param tags_id タグID
     * @param bundled_flg 同梱フラグ
     * @param stock_flg 在庫フラグ
     * @param set_flg セット商品フラグ
     * @param show_flg 商品表示フラグ
     * @param stockShow 在庫表示フラグ
     * @param currentPage ページ
     * @param pageSize サイズ
     * @param productDistinguish 確認要
     * @return {@link ProductRecord}のリスト
     * @since 1.0.0
     */
    @ApiOperation(value = "普通商品一覧", notes = "商品一覧")
    @GetMapping(value = "/store/items/list/{client_id}")
    public JSONObject getProducts(@PathVariable("client_id") String client_id, String warehouse_cd,
        String[] product_id, String search, String tags_id, Integer bundled_flg, String stock_flg, Integer set_flg,
        Integer show_flg, String stockShow, Integer currentPage, Integer pageSize, Integer[] productDistinguish) {
        List<ProductRecord> list =
            productService.getSingleProductRecordList(client_id, warehouse_cd, product_id, search, tags_id,
                bundled_flg, stock_flg, set_flg, show_flg, "1", stockShow, currentPage, pageSize, productDistinguish);
        return CommonUtils.success(list);
    }

    /**
     * セット商品検索
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫コード
     * @param product_id 商品ID
     * @param search キーワード
     * @param tags_id タグID
     * @param bundled_flg 同梱フラグ
     * @param stock_flg 在庫フラグ
     * @param set_flg セット商品フラグ
     * @param show_flg 商品表示フラグ
     * @param stockShow 在庫表示フラグ
     * @param currentPage ページ
     * @param pageSize サイズ
     * @return セット商品リスト
     * @since 1.0.0
     */
    @ApiOperation(value = "set商品一覧", notes = "非表示商品一覧")
    @GetMapping(value = "/itemSet/list/{client_id}")
    public JSONObject getSetProducts(@PathVariable("client_id") String client_id, String warehouse_cd,
        String[] product_id, String search, String tags_id, Integer bundled_flg, String stock_flg, Integer set_flg,
        Integer show_flg, String stockShow, Integer currentPage, Integer pageSize) {
        return productService.getSetProductRecordList(client_id, warehouse_cd, product_id, search, tags_id,
            bundled_flg, stock_flg, set_flg, show_flg, "1", stockShow, currentPage, pageSize);
    }

    /**
     * 商品サイズ情報を取得
     *
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品サイズ情報を取得", notes = "商品サイズ情報を取得")
    @GetMapping(value = "/size/list")
    public JSONObject getProductSize() {
        List<Ms010_product_size> list = commonService.getProductSize();
        return CommonUtils.success(list);
    }

    /**
     * 商品検索
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫コード
     * @param product_id 商品ID
     * @param search キーワード
     * @param tags_id タグID
     * @param bundled_flg 同梱フラグ
     * @param stock_flg 在庫フラグ
     * @param set_flg セット商品フラグ
     * @param show_flg 商品表示フラグ
     * @param stockShow 在庫表示フラグ
     * @param kubuns 区分
     * @return 商品リスト
     * @since 1.0.0
     */
    @ApiOperation(value = "商品一覧", notes = "商品一覧")
    @GetMapping(value = "/items/{client_id}")
    public JSONObject getProductList(@PathVariable("client_id") String client_id, String warehouse_cd,
        String[] product_id, String search, String tags_id, Integer bundled_flg, String stock_flg, Integer set_flg,
        Integer show_flg, String stockShow, int[] kubuns) {

        List<Mc100_product> list = productService.getProductList(client_id, warehouse_cd, product_id, search, tags_id,
            bundled_flg, stock_flg, set_flg, show_flg, "1", stockShow, kubuns);
        return CommonUtils.success(list);
    }

    /**
     * 商品一览/出库/入库依赖一览
     *
     * @param client_id 店铺Id
     * @param search 搜索条件
     * @param page 页数
     * @param size 每页显示件数
     * @param showFlg 非商品显示与否 0：不显示 1：显示
     * @param column 需要排序的字段
     * @param sort 排序的方式
     * @param stockFlg 在库数判断 0：0個以下 1：1個以上
     * @return 商品リスト
     * @since 1.0.0
     */
    @ApiOperation(value = "商品一览/出库/入库依赖一览", notes = "出库/入库依赖一览")
    @GetMapping(value = "/store/operating/list/{client_id}")
    public JSONObject getOperatingList(@PathVariable("client_id") String client_id, String search, Integer page,
        Integer size,
        Integer showFlg, Integer stockFlg, String column, Integer[] productDistinguish,
        String sort, String tagsId, String fakeLoginFlg) {
        return productService.getOperatingList(client_id, search, page, size, productDistinguish, showFlg, stockFlg,
            column, sort, tagsId, fakeLoginFlg);
    }

    /**
     * 商品一覧（倉庫）
     *
     * @param warehouse_cd
     * @param client_id
     * @param search
     * @param stock_flg
     * @param show_flg
     * @param currentPage
     * @param pageSize
     * @param productDistinguish
     * @param column
     * @param sort
     * @return
     */
    @ApiOperation(value = "仓库商品一覧", notes = "商品一覧")
    @GetMapping(value = "/wms/item/list/{warehouse_cd}")
    public JSONObject getItemList(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String search, String stock_flg, Integer show_flg, Integer currentPage, Integer pageSize,
        Integer[] productDistinguish, String column, String sort) {
        return productService.getItemList(client_id, warehouse_cd, search, stock_flg, show_flg, currentPage, pageSize,
            productDistinguish, column, sort);
    }

    /**
     * @param: client_id : 店铺Id
     * @param: warehouse_cd ： 仓库Id
     * @param: search ： 搜索内容
     * @param: stock_flg ： 在库状态
     * @param: set_flg
     * @description: 仓库商品一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/25
     */
    @ApiOperation(value = "商品マスタ一覧", notes = "商品マスタ一覧")
    @GetMapping(value = "/wms/item/itemlist/{warehouse_cd}")
    public JSONObject getItemLists(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String search, String stock_flg, Integer show_flg,
        Integer[] productDistinguish, String column, String sort, String[] productidLists) {
        return productService.getAllItemList(client_id, warehouse_cd, search, stock_flg, show_flg, productDistinguish,
            column, sort, productidLists);
    }

    /**
     * 通常商品一覧CSVダウンロード
     *
     * @param client_id 店舗ID
     * @param search キーワード
     * @param tags_id タグID
     * @param bundled_flg 同梱フラグ
     * @param show_flg 表示フラグ
     * @param stock_flg 在庫フラグ
     * @param stockShow 在庫表示
     * @return {@link ProductRecord}
     * @since 1.0.0
     */
    @ApiOperation(value = "普通商品CSV下载", notes = "普通商品CSV下载")
    @GetMapping(value = "/store/items/csv/{client_id}")
    public JSONObject getProductCsvList(@PathVariable("client_id") String client_id, String search, String tags_id,
        Integer bundled_flg,
        Integer show_flg, String stock_flg, String stockShow) {
        List<ProductRecord> list =
            productService.getProductCsvList(client_id, search, tags_id, bundled_flg, show_flg, stock_flg, stockShow);
        return CommonUtils.success(list);
    }

    /**
     * 通常商品新規登録
     *
     * @param jsonObject 商品新規情報
     * @param httpServletRequest {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "登録商品", notes = "登録商品")
    @PostMapping(value = "/items")
    @Transactional
    public JSONObject insertProduct(@RequestBody JSONObject jsonObject, HttpServletRequest httpServletRequest) {
        return productService.insertProductMain(jsonObject, httpServletRequest);
    }

    /**
     * 通常商品更新
     *
     * @param client_id 店舗ID
     * @param edit_flg 編集フラグ
     * @param old_code 元商品コード
     * @param product_id 商品ID
     * @param jsonObject 更新情報
     * @param httpServletRequest {@link HttpServletRequest}
     * @return 処理結果情報
     * @throws ParseException フォーマット異常
     * @since 1.0.0
     */
    @ApiOperation(value = "商品更新", notes = "商品更新")
    @PutMapping(value = "/items/{client_id}")
    @Transactional
    public JSONObject updateProduct(@PathVariable("client_id") String client_id, String edit_flg, String old_code,
        String product_id,
        @RequestBody JSONObject jsonObject, HttpServletRequest httpServletRequest) throws ParseException {
        return productService.updateProductMain(client_id, edit_flg, old_code, product_id, jsonObject,
            httpServletRequest);
    }

    /**
     * 商品削除
     * <p>
     * セット商品の子商品が削除できません。
     * </p>
     *
     * @param client_id 店舗ID
     * @param product_id 商品ID
     * @param httpServletRequest {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品削除", notes = "商品削除")
    @DeleteMapping(value = "/items/{client_id}")
    @Transactional
    public JSONObject deleteProduct(@PathVariable("client_id") String client_id, String[] product_id,
        HttpServletRequest httpServletRequest) {
        return productService.deleteProduct(client_id, product_id, httpServletRequest);
    }

    /**
     * 商品マッピング情報を新規登録
     *
     * @param client_id 店鋪ID
     * @param jsonObject 商品マッピング情報
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/correspond/insert/{client_id}", method = RequestMethod.POST)
    @ApiOperation(value = "商品マッピング情報を新規登録")
    public JSONObject setCorrespondingData(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonObject, HttpServletRequest request) {
        Integer result = productService.setCorrespondingData(client_id, jsonObject, request);
        if (result > 0) {
            return CommonUtils.success();
        }
        return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 仮登録一括処理
     *
     * @param client_id 店舗ID
     * @param jsonArray 商品情報
     * @param httpServletRequest {@link HttpServletRequest}
     * @return 処理結果情報
     * @throws ParseException フォーマット異常
     * @since 1.0.0
     */
    @ApiOperation(value = "假登录一括処理", notes = "假登录一括処理")
    @PutMapping(value = "/items/process/list/{client_id}")
    public JSONObject allInclusiveHandling(@PathVariable("client_id") String client_id,
        @RequestBody JSONArray jsonArray,
        HttpServletRequest httpServletRequest) throws ParseException {
        return productService.allInclusiveHandling(client_id, jsonArray, httpServletRequest);
    }

    /**
     * 商品コードの対応する商品個数を取得する
     *
     * @param client_id 店舗ID
     * @param code 商品コード
     * @return 商品個数
     * @since 1.0.0
     */
    @ApiOperation(value = "查询商品对应表存在个数", notes = "查询商品对应表存在个数")
    @GetMapping(value = "/items/get_correspondence/{client_id}")
    @Transactional
    public JSONObject getCorrespondence(@PathVariable("client_id") String client_id, String code) {
        return productService.getCorrespondence(client_id, code);
    }

    /**
     * 商品を非表示にする
     *
     * @param client_id 店舗ID
     * @param product_id 商品ID
     * @param show_flg 商品表示フラグ
     * @param httpServletRequest {@link HttpServletRequest}
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "非表示商品設定", notes = "非表示商品設定")
    @PutMapping(value = "/items/show_list/{client_id}")
    @Transactional
    public JSONObject showProduct(@PathVariable("client_id") String client_id, String[] product_id, Integer show_flg,
        HttpServletRequest httpServletRequest) {
        return productService.showProduct(client_id, product_id, show_flg, httpServletRequest);
    }

    /**
     * タグ情報を取得する
     *
     * @param client_id 店舗ID
     * @param product_id 商品ID
     * @return {@link Mc101_product_tag}のリスト
     * @since 1.0.0
     */
    @ApiOperation(value = "タグを取得する", notes = "タグを取得する")
    @GetMapping(value = "/items/tags/{client_id}")
    public JSONObject getTags(@PathVariable("client_id") String client_id, String[] product_id) {
        List<Mc101_product_tag> getClientTags = productService.getClientTags(client_id, product_id);
        return CommonUtils.success(getClientTags);
    }

    /**
     * 商品オプションのタグを取得する
     *
     * @param client_id 店舗ID
     * @return {@link Mc101_product_tag}のリスト
     * @since 1.0.0
     */
    @ApiOperation(value = "tags options", notes = "tags options")
    @GetMapping(value = "/items/tag/{client_id}")
    public JSONObject getTagsOptionsByClientId(@PathVariable("client_id") String client_id) {
        List<Mc101_product_tag> list = productService.getTagsOptionsByClientId(client_id);
        return CommonUtils.success(list);
    }

    /**
     * 商品CSVアップロード
     *
     * @param httpServletRequest {@link HttpServletRequest}
     * @param client_id 店舗ID
     * @param file 商品CSVファイル
     * @return 処理結果情報
     * @since 1.0.0
     */
    @PostMapping("/items/csv/{client_id}")
    @Transactional
    public JSONObject csvUploads(HttpServletRequest httpServletRequest, @PathVariable("client_id") String client_id,
        @RequestParam("file") MultipartFile file) {
        productService.uploadProductCsv(client_id, httpServletRequest, file);
        return CommonUtils.success();
    }

    /**
     * 商品マッピングCSVアップロード
     *
     * @param httpServletRequest {@link HttpServletRequest}
     * @param client_id 店舗ID
     * @param file 商品マッピングCSVファイル
     * @return 処理結果情報
     * @since 1.0.0
     */
    @PostMapping("/items/correspond/csv/{client_id}")
    @Transactional
    public JSONObject correspondCsvUpload(HttpServletRequest httpServletRequest,
        @PathVariable("client_id") String client_id,
        @RequestParam("file") MultipartFile file) {
        productService.correspondCsvUploads(client_id, httpServletRequest, file);
        return CommonUtils.success();
    }

    /**
     * 品名を取得する
     *
     * @param category_cd カテゴリコード
     * @return {@link Ms008_items}
     * @since 1.0.0
     */
    @ApiOperation(value = "获取品名", notes = "获取品名")
    @GetMapping(value = "/items")
    public JSONObject getItemsList(String category_cd) {
        return CommonUtils.success(productService.getItemsList(category_cd));
    }

    /**
     * 商品名称重複チェック
     *
     * @param client_id 店舗ID
     * @param name 商品名称
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品名重复验证", notes = "商品名重复验证")
    @GetMapping(value = "/items/checkname/{client_id}")
    public Boolean checkNameExist(@PathVariable("client_id") String client_id, String name) {
        return productService.checkNameExist(client_id, name);
    }

    /**
     * 商品コード重複チェック
     *
     * @param client_id 店舗ID
     * @param code 商品コード
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品code重复验证", notes = "商品code重复验证")
    @GetMapping(value = "/items/checkcode/{client_id}")
    public Boolean checkCodeExist(@PathVariable("client_id") String client_id, String code) {
        return productService.checkCodeExist(client_id, code);
    }

    /**
     * 商品バーコード重複チェック
     *
     * @param client_id 店舗ID
     * @param barcode 商品バーコード
     * @return 処理結果情報
     * @since 1.0.0
     */
    @ApiOperation(value = "商品barcode重复验证", notes = "商品barcode重复验证")
    @GetMapping(value = "/items/checkbarcode/{client_id}")
    public Boolean checkBarcodeExist(@PathVariable("client_id") String client_id, String barcode) {
        return productService.checkBarcodeExist(client_id, barcode);
    }

    /**
     * 商品イメージCSVアップロード
     *
     * @param client_id 店鋪ID
     * @param imgFile イメージ
     * @param csvFile CSVファイル
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     * @異常
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/import/img/{client_id}", method = RequestMethod.POST)
    @ApiOperation(value = "商品画像CSV上传")
    public JSONObject importImgCSV(@PathVariable("client_id") String client_id,
        @RequestParam("imgFile") MultipartFile imgFile, @RequestParam("csvFile") MultipartFile csvFile,
        HttpServletRequest request) {

        logger.info("商品画像CSV取込の開始：" + client_id);
        return productService.importImgCSV(client_id, imgFile, csvFile, request);
    }

    /**
     * 対象商品がセット商品の子商品かどうかをチェックする
     *
     * @param client_id 店鋪ID
     * @param product_id 商品ID
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/confirmSetProduct", method = RequestMethod.GET)
    @ApiOperation(value = "查询该商品是否为セット商品的子商品")
    public JSONObject confirmSetProduct(String client_id, String product_id) {
        return productService.confirmSetProduct(client_id, product_id);
    }

    /**
     * 出荷依頼中のセット商品が存在するのかをチェックする
     *
     * @param client_id 店鋪ID
     * @param set_sub_id 子商品ID
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/selectShipmentSetProduct", method = RequestMethod.GET)
    @ApiOperation(value = "查询正在出库状态的セット商品")
    public Boolean selectShipmentSetProduct(String client_id, String set_sub_id) {
        return productService.selectShipmentSetProduct(client_id, set_sub_id);
    }

    /**
     * 商品マッピング表CSVをダウンロードする
     *
     * @param client_id 店鋪ID
     * @param search キーワード
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/correspond/csv/{client_id}", method = RequestMethod.GET)
    @ApiOperation(value = "商品对应表CSV下载")
    public JSONObject getCorrespondingCsv(@PathVariable("client_id") String client_id, String search) {
        return productService.getCorrespondingCsv(client_id, search);
    }

    /**
     * 商品マッピング情報を検索する
     *
     * @param client_id 店鋪ID
     * @param search キーワード
     * @param currentPage ページ
     * @param pageSize サイズ
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/correspond/pagination/list/{client_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取商品对应数据")
    public JSONObject getCorrespondingPaginationList(@PathVariable("client_id") String client_id, String search,
        Integer currentPage, Integer pageSize) {
        JSONObject json = productService.getCorrespondingPaginationList(client_id, search, currentPage, pageSize);
        return CommonUtils.success(json);
    }

    /**
     * 商品マッピング情報削除
     *
     * @param clientId 店鋪ID
     * @param id 商品マッピングID
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/correspond/delete/{client_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除商品对应数据")
    public JSONObject delCorresponding(@PathVariable("client_id") String clientId, Integer id) {
        Integer result = productService.delCorresponding(clientId, id);
        if (result > 0) {
            return CommonUtils.success();
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 入荷CSVテンプレートを取得する
     *
     * @param client_id 店鋪ID
     * @param kubuns 区分
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/items/warehouse/csvData/{client_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取入库csv模板数据")
    public JSONObject getWarehouseCsvData(@PathVariable("client_id") String client_id, int[] kubuns) {
        return productService.getWarehouseCsvData(client_id, kubuns);
    }

    /**
     * 商品イメージをアップロードする
     *
     * @param files 商品イメージ
     * @param client_id 店鋪ID
     * @throws IOException IO異常
     */
    @RequestMapping(value = "/items/images/new", method = RequestMethod.POST)
    public void uploadProductImage(@RequestParam("file") MultipartFile[] files, String client_id) throws IOException {
        if (files.length < 1) {
            logger.warn("商品イメージが存在していない。");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowTime = format.format(new Date());
        // 图片路径
        String uploadPath = pathProps.getRoot() + pathProps.getImage() + client_id + "/" + nowTime + "/";
        File uploadDirectory = new File(uploadPath);
        if (uploadDirectory.exists()) {
            if (!uploadDirectory.isDirectory()) {
                if (!uploadDirectory.delete()) {
                    logger.warn("アップロードフォルダーが削除できません。");
                }
            }
        } else {
            if (!uploadDirectory.mkdirs()) {
                logger.warn("対象フォルダーが作成できません。");
            }
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File outFile = new File(uploadPath + fileName);
            file.transferTo(outFile);
        }

        logger.debug("商品イメージがアップロードされました。");
    }

}
