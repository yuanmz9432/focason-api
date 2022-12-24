package com.lemonico.warehouse.service;

import static java.util.stream.Collectors.toList;

import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlResultSet;
import com.lemonico.entity.Mg002WarehouseEntity;
import com.lemonico.warehouse.repository.WarehouseRepository;
import com.lemonico.warehouse.resource.WarehouseResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 倉庫情報のサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseService
{
    private final WarehouseRepository warehouseRepository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、倉庫情報リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 倉庫情報リソースの結果セットが返されます。
     */
    public PlResultSet<WarehouseResource> getWarehouseList(WarehouseRepository.Condition condition,
        PlPagination pagination, WarehouseRepository.Sort sort) {
        // 倉庫情報の一覧と全体件数を検索します。
        PlResultSet<Mg002WarehouseEntity> resultSet = warehouseRepository.findAll(condition, pagination, sort);

        // 倉庫情報エンティティのリストを倉庫情報リソースのリストに変換します。
        List<WarehouseResource> resources = resultSet.stream().map(WarehouseResource::new).collect(toList());
        return new PlResultSet<>(resources, resultSet.getCount());
    }

    public Optional<WarehouseResource> getWarehouseResource(ID<Mg002WarehouseEntity> id) {
        return warehouseRepository.findById(id).map(this::convertEntityToResource);
    }


    /**
     * ストア情報エンティティをストア情報リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public WarehouseResource convertEntityToResource(Mg002WarehouseEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ストア情報エンティティのリストをストア情報リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<WarehouseResource> convertEntitiesToResources(List<Mg002WarehouseEntity> entities) {
        return entities.stream()
            .map(WarehouseResource::new)
            .collect(toList());
    }
}
