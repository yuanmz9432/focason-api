select
  /*%expand*/*
from
  file_transfer
where
  id = /* id */1
AND
  is_deleted = 0
