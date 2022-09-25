/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.warehouse.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceAlreadyExistsException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.warehouse.entity.CompanyEntity;
import api.lemonico.warehouse.repository.CompanyRepository;
import api.lemonico.warehouse.resource.CompanyResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 会社サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompanyService
{

    /**
     * 会社リポジトリ
     */
    private final CompanyRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、会社リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 会社リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<CompanyResource> getResourceList(
        CompanyRepository.Condition condition,
        LcPagination pagination,
        CompanyRepository.Sort sort) {
        // 会社の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // 会社エンティティのリストを会社リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 会社IDを指定して、会社を取得します。
     *
     * @param id 会社ID
     * @return 会社リソース
     */
    @Transactional(readOnly = true)
    public Optional<CompanyResource> getResource(ID<CompanyEntity> id) {
        // 会社を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 会社を作成します。
     *
     * @param resource 会社リソース
     * @return 作成された会社リソース
     */
    @Transactional
    public CompanyResource createResource(CompanyResource resource) {
        // 会社重複チェック（会社コード）
        if (isCompanyExisted(resource)) {
            throw new LcResourceAlreadyExistsException(CompanyEntity.class,
                resource.getCompanyCode() + ":" + resource.getCompanyName());
        }

        // 会社を作成します。
        var id = repository.create(resource.toEntity());

        // 会社を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 会社重複性チェック
     *
     * @param resource ユーザリソース
     * @return true：重複した、false：重複しない
     */
    private boolean isCompanyExisted(CompanyResource resource) {
        var client = getResourceList(
            CompanyRepository.Condition.builder()
                .companyCode(resource.getCompanyCode())
                .build(),
            LcPagination.DEFAULT,
            CompanyRepository.Sort.DEFAULT);
        return client.getCount() > 0;
    }

    /**
     * 会社IDを指定して、会社を更新します。
     *
     * @param id 会社ID
     * @param resource 会社リソース
     * @return 更新後の会社リソース
     */
    @Transactional
    public CompanyResource updateResource(ID<CompanyEntity> id, CompanyResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 会社IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(CompanyEntity.class, id);
        }

        // 会社を更新します。
        repository.update(id, resource.toEntity());

        // 会社を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 会社IDを指定して、会社を削除します。
     *
     * @param id 会社ID
     */
    @Transactional
    public void deleteResource(ID<CompanyEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 会社IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(CompanyEntity.class, id);
        }

        // 会社を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 会社エンティティを会社リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public CompanyResource convertEntityToResource(CompanyEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 会社エンティティのリストを会社リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<CompanyResource> convertEntitiesToResources(List<CompanyEntity> entities) {
        return entities.stream()
            .map(CompanyResource::new)
            .collect(toList());
    }

}
