SELECT
    /*%expand*/*
FROM
    mg002_warehouse
WHERE
    is_deleted = 0
    /*%if condition.getWarehouseCode() != null */
    AND warehouse_code IN /* condition.getWarehouseCode() */(1,2,3)
    /*%end */
ORDER BY
/*# sort.toSql() */