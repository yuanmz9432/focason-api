SELECT
    /*%expand*/*
FROM
    user
WHERE
    is_deleted = 0
    /*%if condition.getIds() != null */
    AND id IN /* condition.getIds() */(1,2,3)
    /*%end */
    /*%if condition.getUsername() != null */
    AND username = /* condition.getUsername() */'username'
    /*%end */
    /*%if condition.getEmail() != null */
    AND email = /* condition.getEmail() */'admin@focason.com'
    /*%end */
    /*%if condition.getUuid() != null */
    AND uuid = /* condition.getUuid() */'uuid'
    /*%end */
ORDER BY
    /*# sort.toSql() */