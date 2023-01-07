SELECT
    /*%expand*/*
FROM
    user_authority
WHERE
    is_deleted = 0
    /*%if condition.getUuid() != null */
    AND uuid = /* condition.getUuid() */'b925b039-8275-45c4-a91e-748f5c94b8ee'
    /*%end */
ORDER BY
    /*# sort.toSql() */