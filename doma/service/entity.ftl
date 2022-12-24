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


import attribute.core.com.lemonico.ID;
import attribute.core.com.lemonico.PlPagination;
import attribute.core.com.lemonico.PlResultSet;
import exception.core.com.lemonico.PlResourceNotFoundException;
import exception.core.com.lemonico.PlUnexpectedPhantomReadException;
import jp.co.tfg.prologi.entity.${simpleName}Entity;
import jp.co.tfg.prologi.temporary.repository.${simpleName}Repository;
import jp.co.tfg.prologi.temporary.resource.${simpleName}Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;


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
    public PlResultSet<${simpleName}Resource> getResourceList(
        ${simpleName}Repository.Condition condition,
        PlPagination pagination,
        ${simpleName}Repository.Sort sort) {
        // ${comment}の一覧と全体件数を取得します。
        PlResultSet<${simpleName}Entity> resultSet = repository.findAll(condition, pagination, sort);

        // ${comment}エンティティのリストを${comment}リソースのリストに変換します。
        List<${simpleName}Resource> resources = convertEntitiesToResources(resultSet.getData());
        return new PlResultSet<>(resources, resultSet.getCount());
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
        ID<${simpleName}Entity> id = repository.create(resource.toEntity());

        // ${comment}を取得します。
        return getResource(id).orElseThrow(PlUnexpectedPhantomReadException::new);
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
            throw new PlResourceNotFoundException(${simpleName}Entity.class, id);
        }

        // ${comment}を更新します。
        repository.update(id, resource.toEntity());

        // ${comment}を取得します。
        return getResource(id).orElseThrow(PlUnexpectedPhantomReadException::new);
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
            throw new PlResourceNotFoundException(${simpleName}Entity.class, id);
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
