/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.auth.config.LoginUser;
import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceAlreadyExistsException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.domain.ClientStatus;
import api.lemonico.entity.Client;
import api.lemonico.repository.ClientRepository;
import api.lemonico.resource.ClientResource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * クライアントサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientService
{

    /**
     * クライアントリポジトリ
     */
    private final ClientRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、クライアントリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return クライアントリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<ClientResource> getResourceList(
        ClientRepository.Condition condition,
        LcPagination pagination,
        ClientRepository.Sort sort) {
        // クライアントの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // クライアントエンティティのリストをクライアントリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * クライアントIDを指定して、クライアントを取得します。
     *
     * @param id クライアントID
     * @return クライアントリソース
     */
    @Transactional(readOnly = true)
    public Optional<ClientResource> getResource(ID<Client> id) {
        // クライアントを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * クライアントを作成します。
     *
     * @param resource クライアントリソース
     * @return 作成されたクライアントリソース
     */
    @Transactional
    public ClientResource createResource(ClientResource resource) {
        // メールアドレスにおいて重複したデータが存在していることを示す。
        var client = getResourceByEmail(resource.getEmail());
        if (client.isPresent()) {
            throw new LcResourceAlreadyExistsException(Client.class, client.get().getEmail());
        }

        // クライアントを作成します。
        var id = repository.create(
            resource.withCreatedAt(LocalDateTime.now())
                .withCreatedBy(resource.getClientCode())
                .withModifiedAt(LocalDateTime.now())
                .withModifiedBy(resource.getClientCode())
                .withIsDeleted(ClientStatus.NORMAL.getValue()).toEntity());

        // クライアントを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * クライアントIDを指定して、クライアントを更新します。
     *
     * @param id クライアントID
     * @param resource クライアントリソース
     * @return 更新後のクライアントリソース
     */
    @Transactional
    public ClientResource updateResource(ID<Client> id, ClientResource resource) {
        // クライアントIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(Client.class, id);
        }

        // クライアントを更新します。
        repository.update(id, resource.toEntity());

        // クライアントを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * クライアントIDを指定して、クライアントを削除します。
     *
     * @param id クライアントID
     */
    @Transactional
    public void deleteResource(ID<Client> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // クライアントIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(Client.class, id);
        }

        // クライアントを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * メールアドレスを指定して、クライアントエンティティを取得する。
     *
     * @param email メールアドレス
     * @return クライアントエンティティ
     */
    @Transactional(readOnly = true)
    public Optional<ClientResource> getResourceByEmail(String email) {
        return repository.findByEmail(email).map(this::convertEntityToResource);
    }

    /**
     * メールアドレスを指定して、LoginUserを取得する。
     *
     * @param email メールアドレス
     * @return LoginUser
     */
    @Transactional(readOnly = true)
    public LoginUser getLoginUserByEmail(String email) {
        return LoginUser.builder()
            .email(email)
            .userId(1)
            .username("").enabled(true)
            .build();
    }

    /**
     * クライアントエンティティをクライアントリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public ClientResource convertEntityToResource(Client entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * クライアントエンティティのリストをクライアントリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<ClientResource> convertEntitiesToResources(List<Client> entities) {
        return entities.stream()
            .map(ClientResource::new)
            .collect(toList());
    }

}
