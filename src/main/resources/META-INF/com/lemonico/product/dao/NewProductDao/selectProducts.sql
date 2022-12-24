SELECT
    /*%expand*/*
FROM
    pd001_product
WHERE
    is_deleted = 0
    /*%if condition.getProductCode() != null */
    AND product_code IN /* condition.getProductCode() */(1,2,3)
    /*%end */
ORDER BY
    /*# sort.toSql() */