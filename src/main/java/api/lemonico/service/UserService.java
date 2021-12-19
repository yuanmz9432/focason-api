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
import api.lemonico.core.utils.BCryptEncoder;
import api.lemonico.domain.UserType;
import api.lemonico.entity.User;
import api.lemonico.repository.UserRepository;
import api.lemonico.resource.UserResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ユーザーサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService
{
    /**
     * ユーザーリポジトリ
     */
    private final UserRepository userRepository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、クライアントリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return クライアントリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<UserResource> getResourceList(
        UserRepository.Condition condition,
        LcPagination pagination,
        UserRepository.Sort sort) {
        // クライアントの一覧と全体件数を取得します。
        var resultSet = userRepository.findAll(condition, pagination, sort);

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
    public Optional<UserResource> getResource(ID<User> id) {
        // クライアントを取得します。
        var client = userRepository.findById(id);
        return client.map(this::convertEntityToResource);
    }

    /**
     * クライアントを作成します。
     *
     * @param resource クライアントリソース
     * @return 作成されたクライアントリソース
     */
    @Transactional
    public UserResource createResource(UserResource resource) {
        // メールアドレスにおいて重複したデータが存在していることを示す。
        var client = getResourceByEmail(resource.getEmail());
        if (client.isPresent()) {
            throw new LcResourceAlreadyExistsException(User.class, client.get().getEmail());
        }

        // クライアントを作成します。
        var id = userRepository.create(
            resource.withPassword(BCryptEncoder.getInstance().encode(resource.getPassword()))
                .withType(UserType.PREMIUM.getValue())
                .toEntity());

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
    public UserResource updateResource(ID<User> id, UserResource resource) {
        // クライアントIDにおいて重複したデータが存在していることを示す。
        if (!userRepository.exists(id)) {
            throw new LcResourceNotFoundException(User.class, id);
        }

        // クライアントを更新します。
        userRepository.update(id, resource.toEntity());

        // クライアントを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザーIDを指定して、ユーザーを削除します。
     *
     * @param id ユーザーID
     */
    @Transactional
    public void deleteResource(ID<User> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // クライアントIDにおいて重複したデータが存在していることを示す。
        if (!userRepository.exists(id)) {
            throw new LcResourceNotFoundException(User.class, id);
        }

        // クライアントを削除します。
        userRepository.deleteLogicById(id);
    }

    /**
     * メールアドレスを指定して、クライアントエンティティを取得する。
     *
     * @param email メールアドレス
     * @return クライアントエンティティ
     */
    @Transactional(readOnly = true)
    public Optional<UserResource> getResourceByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertEntityToResource);
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
     * ユーザーエンティティをユーザーリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public UserResource convertEntityToResource(User entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ユーザーエンティティのリストをユーザーリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<UserResource> convertEntitiesToResources(List<User> entities) {
        return entities.stream()
            .map(UserResource::new)
            .collect(toList());
    }

}
