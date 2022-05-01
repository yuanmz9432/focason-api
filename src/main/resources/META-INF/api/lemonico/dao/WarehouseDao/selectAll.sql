SELECT
    /*%expand*/*
FROM
    warehouse
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
    /*%if condition.getWarehouseCodes() != null */
    AND warehouse_code IN /* condition.getWarehouseCodes() */("WHC001", "WHC002")
    /*%end */
ORDER BY
    /*# sort.toSql() */