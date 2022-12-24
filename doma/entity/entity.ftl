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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
<#list importNames as importName>
import ${importName};
</#list>


/**
 * <#if comment??>${comment}</#if>エンティティ
 *
 <#if lib.since??>
 * @since ${lib.since}
 </#if>
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "${tableName}")
public class <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if>
{
<#list ownEntityPropertyDescs as property>
    <#if property.comment??>
    /** ${property.comment} */
    <#else>
    /** */
    </#if>
    <#if property.id>
    <#if property.version>
    @Version
    </#if>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    <#if !useAccessor>public </#if>ID<${simpleName}<#if entitySuffix??>${entitySuffix}</#if>> ${property.name};
    <#else>
    <#if property.version>
    @Version
    </#if>
    <#if property.showColumnName && property.columnName??>
    @Column(name = "${property.columnName}")
    </#if>
    <#if !useAccessor>public </#if>${property.propertyClassSimpleName} ${property.name};
    </#if>
</#list>
}
