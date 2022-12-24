INSERT INTO "ms201_client"(client_id, client_nm, shop_nm, logo, validity_str_date, validity_end_date, corporation_flg,
                           corporation_number, department, main_office_flg, birthday, country_region, tel, fax, zip,
                           tdfk, add1, add2, tnnm, tnbs, mail, contact_time, url, permonth, delivery_method, color,
                           biko, ins_usr, ins_date, upd_usr, upd_date, label_note, bill_customer_cd, fare_manage_cd,
                           customer_code, delivery_code, yamato_manage_code, sagawa_nisugata_code)
values ('DM001', '【デモ】楽天ストア', '株式会社TFGソリューション', '', now(), now(), '1', '', '', 1, now(), '日本', '03-6280-7380',
        '03-6280-7390', '125-0051', '東京都', '台東区東上野6丁目2番1号', 'MPR東上野ビル10F', '田中', '', 'admin@prologi.com', '', '', 100,
        '1', '#174A84', '', 'ADMIN', now(), 'ADMIN', now(), '商品在中', '', '', '', '', '', '');