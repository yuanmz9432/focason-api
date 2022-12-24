package com.lemonico.warehouse.controller;



import com.lemonico.core.annotation.PlConditionParam;
import com.lemonico.core.annotation.PlPaginationParam;
import com.lemonico.core.annotation.PlSortParam;
import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlResultSet;
import com.lemonico.core.attribute.PlSort;
import com.lemonico.core.exception.PlResourceNotFoundException;
import com.lemonico.entity.Mg002WarehouseEntity;
import com.lemonico.product.controller.ProductController;
import com.lemonico.warehouse.repository.WarehouseRepository;
import com.lemonico.warehouse.resource.WarehouseResource;
import com.lemonico.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 倉庫情報管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseController
{
    /**
     * APIバージョン
     */
    private static final String API_VERSION = "/v2";
    /**
     * コレクションリソースURI
     */
    private static final String WAREHOUSE_COLLECTION_RESOURCE_URI = API_VERSION + "/warehouses";

    /**
     * メンバーリソースURI
     */
    private static final String WAREHOUSE_RESOURCE_URI = WAREHOUSE_COLLECTION_RESOURCE_URI + "/{id}";

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final WarehouseService warehouseService;

    /**
     * 倉庫情報リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param plSort ソートパラメータ
     * @return 倉庫情報リソース一覧取得APIレスポンス
     */
    @RequestMapping(path = WAREHOUSE_COLLECTION_RESOURCE_URI, method = RequestMethod.GET)
    public ResponseEntity<PlResultSet<WarehouseResource>> getWarehouseList(
        @PlConditionParam WarehouseRepository.Condition condition,
        @PlPaginationParam PlPagination pagination,
        @PlSortParam(allowedValues = {}) PlSort plSort) {
        if (condition == null) {
            condition = WarehouseRepository.Condition.DEFAULT;
        }
        WarehouseRepository.Sort sort = WarehouseRepository.Sort.fromPlSort(plSort);
        return ResponseEntity.ok(warehouseService.getWarehouseList(condition, pagination, sort));
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報リソース取得API
     *
     * @param id 倉庫情報ID
     * @return 倉庫情報リソース取得APIレスポンス
     */
    @RequestMapping(path = WAREHOUSE_RESOURCE_URI, method = RequestMethod.GET)
    public ResponseEntity<WarehouseResource> getWarehouse(
        @PathVariable("id") ID<Mg002WarehouseEntity> id) {
        return warehouseService.getWarehouseResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PlResourceNotFoundException(WarehouseResource.class, id));
    }
}
