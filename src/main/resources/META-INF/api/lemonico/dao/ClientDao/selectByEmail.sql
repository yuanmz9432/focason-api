select
    /*%expand*/*
from
    client
where
    client.email = /* email */'admin@lemonico.com'
    and
    client.is_deleted = 0