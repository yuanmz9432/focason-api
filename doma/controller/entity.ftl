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


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import java.net.URI;
import javax.validation.Valid;
import javax.validation.groups.Default;
import annotation.core.com.lemonico.PlConditionParam;
import annotation.core.com.lemonico.PlPaginationParam;
import annotation.core.com.lemonico.PlSortParam;
import attribute.core.com.lemonico.ID;
import attribute.core.com.lemonico.PlPagination;
import attribute.core.com.lemonico.PlResultSet;
import attribute.core.com.lemonico.PlSort;
import exception.core.com.lemonico.PlResourceNotFoundException;
import entity.com.lemonico.Mg003StoreEntity;
import repository.temporary.com.lemonico.Mg003StoreRepository;
import resource.temporary.com.lemonico.Mg003StoreResource;
import service.temporary.com.lemonico.Mg003StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ${comment}コントローラー
 *
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if>
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ${comment}サービス
     */
    private final ${simpleName}Service service;

    /**
     * ${comment}リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return ${comment}リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<PlResultSet<${simpleName}Resource>> get${simpleName}List(
        @PlConditionParam ${simpleName}Repository.Condition condition,
        @PlPaginationParam PlPagination pagination,
        @PlSortParam(allowedValues = {}) PlSort lcSort) {
        if (condition == null) {
            condition = ${simpleName}Repository.Condition.DEFAULT;
        }
        ${simpleName}Repository.Sort sort = ${simpleName}Repository.Sort.fromPlSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ${comment}IDを指定して、${comment}リソース取得API
     *
     * @param id ${comment}ID
     * @return ${comment}リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<${simpleName}Resource> get${simpleName}(
        @PathVariable("id") ID<${simpleName}Entity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PlResourceNotFoundException(${simpleName}Resource.class, id));
    }

    /**
     * ${comment}リソース作成API
     *
     * @param resource ${comment}リソース
     * @return ${comment}リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> create${simpleName}(
        @Valid @RequestBody ${simpleName}Resource resource,
        UriComponentsBuilder uriBuilder) {
        ID<${simpleName}Entity> id = service.createResource(resource).getId();
        URI uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).get${simpleName}(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ${comment}IDを指定して、${comment}リソース更新API
     *
     * @param id ${comment}ID
     * @param resource ${comment}リソース更新APIレスポンス
     * @return ${comment}リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<${simpleName}Resource> update${simpleName}(
        @PathVariable("id") ID<${simpleName}Entity> id,
        @Valid @RequestBody ${simpleName}Resource resource) {
        ${simpleName}Resource updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ${comment}IDを指定して、${comment}リソース削除API
     *
     * @param id ${comment}ID
     * @return ${comment}リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> delete${simpleName}(
        @PathVariable("id") ID<${simpleName}Entity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
