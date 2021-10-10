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

import api.lemonico.attribute.ID;
import api.lemonico.entity.${simpleName};
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
<#if tableName??>
 * ${tableName}のエンティティ
 *
</#if>
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if> {
<#list ownEntityPropertyDescs as property>

    <#if showDbComment && property.comment??>
    /** ${property.comment} */
    <#else>
    /** */
    </#if>
    <#if property.id>
    private final ID<${simpleName}> ${property.name};
    <#else>
    private final ${property.propertyClassSimpleName} ${property.name};
    </#if>
</#list>

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity ${simpleName}エンティティ
     */
    public ${simpleName}${entitySuffix}(${simpleName} entity) {
        <#list ownEntityPropertyDescs as property>
        this.${property.name} = entity.get${property.name?cap_first}();
        </#list>
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ${simpleName}エンティティ
     */
    public ${simpleName} toEntity() {
        return ${simpleName}.builder()
            <#list ownEntityPropertyDescs as property>
            .${property.name}(${property.name})
            </#list>
            .build();
    }


<#--    /**-->
<#--    * Returns the ${property.name}.-->
<#--    *-->
<#--    * @return the ${property.name}-->
<#--    */-->
<#--    public ${property.propertyClassSimpleName} get${property.name?cap_first}() {-->
<#--    return ${property.name};-->
<#--    }-->

<#--    /**-->
<#--    * Sets the ${property.name}.-->
<#--    *-->
<#--    * @param ${property.name} the ${property.name}-->
<#--    */-->
<#--    public void set${property.name?cap_first}(${property.propertyClassSimpleName} ${property.name}) {-->
<#--    this.${property.name} = ${property.name};-->
<#--    }-->
}