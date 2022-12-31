<#-- このテンプレートに対応するデータモデルのクラスは org.seasar.doma.extension.gen.EntityDesc です -->
<#import "lib.ftl" as lib>
/*
<#if lib.copyright??>
 * ${lib.copyright}
</#if>
 */
<#if packageName??>
package ${packageName};
</#if>

import static java.util.stream.Collectors.toList;

import attribute.core.com.blazeash.api.ID;
import attribute.core.com.blazeash.api.LcPagination;
import attribute.core.com.blazeash.api.LcResultSet;
import exception.core.com.blazeash.api.LcResourceNotFoundException;
import exception.core.com.blazeash.api.LcUnexpectedPhantomReadException;
import api.lemonico.entity.${simpleName}Entity;
import api.lemonico.repository.${simpleName}Repository;
import api.lemonico.resource.${simpleName}Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ${comment}サービス
 *
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${simpleName}${entitySuffix}
{

    /**
     * ${comment}リポジトリ
     */
    private final ${simpleName}Repository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、${comment}リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ${comment}リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<${simpleName}Resource> getResourceList(
        ${simpleName}Repository.Condition condition,
        LcPagination pagination,
        ${simpleName}Repository.Sort sort) {
        // ${comment}の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ${comment}エンティティのリストを${comment}リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ${comment}IDを指定して、${comment}を取得します。
     *
     * @param id ${comment}ID
     * @return ${comment}リソース
     */
    @Transactional(readOnly = true)
    public Optional<${simpleName}Resource> getResource(ID<${simpleName}Entity> id) {
        // ${comment}を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ${comment}を作成します。
     *
     * @param resource ${comment}リソース
     * @return 作成された${comment}リソース
     */
    @Transactional
    public ${simpleName}Resource createResource(${simpleName}Resource resource) {
        // ${comment}を作成します。
        var id = repository.create(resource.toEntity());

        // ${comment}を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ${comment}IDを指定して、${comment}を更新します。
     *
     * @param id ${comment}ID
     * @param resource ${comment}リソース
     * @return 更新後の${comment}リソース
     */
    @Transactional
    public ${simpleName}Resource updateResource(ID<${simpleName}Entity> id, ${simpleName}Resource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ${comment}IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(${simpleName}Entity.class, id);
        }

        // ${comment}を更新します。
        repository.update(id, resource.toEntity());

        // ${comment}を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ${comment}IDを指定して、${comment}を削除します。
     *
     * @param id ${comment}ID
     */
    @Transactional
    public void deleteResource(ID<${simpleName}Entity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ${comment}IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(${simpleName}Entity.class, id);
        }

        // ${comment}を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ${comment}エンティティを${comment}リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public ${simpleName}Resource convertEntityToResource(${simpleName}Entity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ${comment}エンティティのリストを${comment}リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<${simpleName}Resource> convertEntitiesToResources(List<${simpleName}Entity> entities) {
        return entities.stream()
            .map(${simpleName}Resource::new)
            .collect(toList());
    }

}
