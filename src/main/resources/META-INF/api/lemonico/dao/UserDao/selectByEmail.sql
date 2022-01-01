select
    /*%expand*/*
from
    user
where
    user.email = /* email */'admin@lemonico.com'
    and
    user.is_deleted = 0