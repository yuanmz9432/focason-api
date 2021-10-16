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
    /*%if @isNotEmpty(condition.getFirstName()) */
    AND first_name LIKE /* @infix(condition.getFirstName()) */'%Yua%' ESCAPE '$'
    /*%end */
    /*%if @isNotEmpty(condition.getLastName()) */
    AND last_name LIKE /* @infix(condition.getLastName()) */'%Min%' ESCAPE '$'
    /*%end */
    /*%if @isNotEmpty(condition.getEmail()) */
    AND email LIKE /* @infix(condition.getEmail()) */'%admin@lemonico%' ESCAPE '$'
    /*%end */
    /*%if condition.getStatus() != null */
    AND status = /* condition.getStatus() */1
    /*%end */
ORDER BY
    /*# sort.toSql() */