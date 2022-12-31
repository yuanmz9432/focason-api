select
    /*%expand*/*
from
    user
where
    user.email = /* email */'admin@Blazeash.com'
    and
    user.is_deleted = 0