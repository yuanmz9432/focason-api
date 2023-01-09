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
    AND username LIKE /* @infix(condition.getUsername()) */'%username%'
    /*%end */
    /*%if condition.getEmail() != null */
    AND email = /* condition.getEmail() */'admin@focason.com'
    /*%end */
    /*%if condition.getUuid() != null */
    AND uuid = /* condition.getUuid() */'uuid'
    /*%end */
    /*%if condition.getGender() != null */
    AND gender = /* condition.getGender() */1
    /*%end */
    /*%if condition.getStatus() != null */
    AND status = /* condition.getStatus() */1
    /*%end */
    /*%if condition.getType() != null */
    AND type = /* condition.getType() */1
    /*%end */
    /*%if condition.getIsDeleted() != null */
    AND is_deleted = /* condition.getIsDeleted() */0
    /*%end */
ORDER BY
    /*# sort.toSql() */
