select
    /*%expand*/*
from
    user
where
    user.email = /* email */'admin@Focason.com'
    and
    user.is_deleted = 0