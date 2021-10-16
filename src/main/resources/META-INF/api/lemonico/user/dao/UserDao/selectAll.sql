SELECT
    id,
    first_name,
    last_name,
    email,
    status,
    is_deleted
FROM
    user
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
ORDER BY
    /*# sort.toSql() */