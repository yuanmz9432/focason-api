SELECT
    /*%expand*/*
FROM
    store_dependent
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
    /*%if condition.getWarehouseCode() != null */
    AND warehouse_code = /* condition.getWarehouseCode() */'WHS001'
    /*%end */
ORDER BY
    /*# sort.toSql() */