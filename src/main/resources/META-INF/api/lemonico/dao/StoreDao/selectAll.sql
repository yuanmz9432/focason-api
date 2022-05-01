SELECT
    /*%expand*/*
FROM
    store
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
    /*%if condition.getStoreCodes() != null */
    AND store_code IN /* condition.getStoreCodes() */("1", "2")
    /*%end */
ORDER BY
    /*# sort.toSql() */