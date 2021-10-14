/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.customer.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.customer.entity.Customer;
import api.lemonico.customer.repository.CustomerRepository;
import api.lemonico.customer.resource.CustomerResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 会員サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerService
{

    /**
     * 会員リポジトリ
     */
    private final CustomerRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、会員リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 会員リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<CustomerResource> getResourceList(
        CustomerRepository.Condition condition,
        LcPagination pagination,
        CustomerRepository.Sort sort) {
        // 会員の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // 会員エンティティのリストを会員リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 会員IDを指定して、会員を取得します。
     *
     * @param id 会員ID
     * @return 会員リソース
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResource> getResource(ID<Customer> id) {
        // 会員を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 会員を作成します。
     *
     * @param resource 会員リソース
     * @return 作成された会員リソース
     */
    @Transactional
    public CustomerResource createResource(CustomerResource resource) {
        // 会員を作成します。
        var id = repository.create(resource.toEntity());

        // 会員を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 会員IDを指定して、会員を更新します。
     *
     * @param id 会員ID
     * @param resource 会員リソース
     * @return 更新後の会員リソース
     */
    @Transactional
    public CustomerResource updateResource(ID<Customer> id, CustomerResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 会員IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(Customer.class, id);
        }

        // 会員を更新します。
        repository.update(id, resource.toEntity());

        // 会員を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 会員IDを指定して、会員を削除します。
     *
     * @param id 会員ID
     */
    @Transactional
    public void deleteResource(ID<Customer> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 会員IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(Customer.class, id);
        }

        // 会員を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 会員エンティティを会員リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public CustomerResource convertEntityToResource(Customer entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 会員エンティティのリストを会員リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<CustomerResource> convertEntitiesToResources(List<Customer> entities) {
        return entities.stream()
            .map(CustomerResource::new)
            .collect(toList());
    }

}
