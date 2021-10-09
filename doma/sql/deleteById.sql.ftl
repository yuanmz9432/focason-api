select
<#list entityDesc.entityPropertyDescs as property>
    ${property.columnName}<#if property_has_next>,</#if>
</#list>
from
    ${entityDesc.tableName}