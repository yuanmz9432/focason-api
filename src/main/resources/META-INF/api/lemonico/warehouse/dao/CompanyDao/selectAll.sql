SELECT
    /*%expand*/*
FROM
    company
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
    /*%if condition.getCompanyCode() != null */
    AND company_code = /* condition.getCompanyCode() */'CPN-001'
    /*%end */
ORDER BY
    /*# sort.toSql() */