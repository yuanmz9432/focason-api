select
    id,
    first_name,
    last_name,
    email,
    status,
    is_deleted
from
    user
where
    id = /* id */1
and
    is_deleted = 0
