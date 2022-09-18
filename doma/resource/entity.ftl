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



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.${simpleName}Entity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ${comment}リソース
 *
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if>
{
<#list ownEntityPropertyDescs as property>

    <#if showDbComment && property.comment??>
    /** ${property.comment} */
    <#else>
    /** */
    </#if>
    <#if property.id>
    private final ID<${simpleName}Entity> ${property.name};
    <#else>
    private final ${property.propertyClassSimpleName} ${property.name};
    </#if>
</#list>

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity ${comment}エンティティ
     */
    public ${simpleName}${entitySuffix}(${simpleName}Entity entity) {
        <#list ownEntityPropertyDescs as property>
        this.${property.name} = entity.get${property.name?cap_first}();
        </#list>
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ${comment}エンティティ
     */
    public ${simpleName}Entity toEntity() {
        return ${simpleName}Entity.builder()
            <#list ownEntityPropertyDescs as property>
            .${property.name}(${property.name})
            </#list>
            .build();
    }
}
