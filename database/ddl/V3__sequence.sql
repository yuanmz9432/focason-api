--
-- Name: mc102_product_img_img_sub_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.mc102_product_img_img_sub_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.mc102_product_img_img_sub_id_seq OWNER TO prologi;
--
-- Name: mc102_product_img_img_sub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.mc102_product_img_img_sub_id_seq OWNED BY public.mc102_product_img.img_sub_id;
--
-- Name: mc102_product_img_img_sub_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.mc102_product_img_img_sub_id_seq', 4091, true);

--
-- Name: mc110_product_options_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.mc110_product_options_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.mc110_product_options_id_seq OWNER TO prologi;
--
-- Name: mc110_product_options_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.mc110_product_options_id_seq OWNED BY public.mc110_product_options.id;
--
-- Name: mc110_product_options_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.mc110_product_options_id_seq', 3996, true);

--
-- Name: ms006_delivery_time_delivery_time_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms006_delivery_time_delivery_time_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms006_delivery_time_delivery_time_id_seq OWNER TO prologi;
--
-- Name: ms006_delivery_time_delivery_time_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms006_delivery_time_delivery_time_id_seq OWNED BY public.ms006_delivery_time.delivery_time_id;
--
-- Name: ms006_delivery_time_delivery_time_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms006_delivery_time_delivery_time_id_seq', 209, true);

--
-- Name: ms013_api_template_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms013_api_template_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms013_api_template_id_seq OWNER TO prologi;
--
-- Name: ms013_api_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms013_api_template_id_seq OWNED BY public.ms013_api_template.id;
--
-- Name: ms013_api_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms013_api_template_id_seq', 4, true);

--
-- Name: ms014_payment_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms014_payment_payment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms014_payment_payment_id_seq OWNER TO prologi;
--
-- Name: ms014_payment_payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms014_payment_payment_id_seq OWNED BY public.ms014_payment.payment_id;
--
-- Name: ms014_payment_payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms014_payment_payment_id_seq', 114, true);

--
-- Name: ms015_news_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms015_news_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms015_news_id_seq OWNER TO prologi;
--
-- Name: ms015_news_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms015_news_id_seq OWNED BY public.ms015_news.id;
--
-- Name: ms015_news_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms015_news_id_seq', 3, true);

--
-- Name: ms205_customer_history_operation_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms205_customer_history_operation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms205_customer_history_operation_id_seq OWNER TO prologi;
--
-- Name: ms205_customer_history_operation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms205_customer_history_operation_id_seq OWNED BY public.ms205_customer_history.operation_id;
--
-- Name: ms205_customer_history_operation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms205_customer_history_operation_id_seq', 1024057, true);

--
-- Name: ms207_all_func_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms207_all_func_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms207_all_func_id_seq OWNER TO prologi;
--
-- Name: ms207_all_func_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms207_all_func_id_seq OWNED BY public.ms207_all_func.id;
--
-- Name: ms207_all_func_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms207_all_func_id_seq', 1, false);

--
-- Name: mw407_smart_file_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.mw407_smart_file_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.mw407_smart_file_id_seq OWNER TO prologi;
--
-- Name: mw407_smart_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.mw407_smart_file_id_seq OWNED BY public.mw407_smart_file.id;
--
-- Name: mw407_smart_file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.mw407_smart_file_id_seq', 178693, true);

--
-- Name: tc204_order_template_template_cd_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc204_order_template_template_cd_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc204_order_template_template_cd_seq OWNER TO prologi;
--
-- Name: tc204_order_template_template_cd_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc204_order_template_template_cd_seq OWNED BY public.tc204_order_template.template_cd;
--
-- Name: tc204_order_template_template_cd_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc204_order_template_template_cd_seq', 329, true);

--
-- Name: tc206_order_ftp_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc206_order_ftp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc206_order_ftp_id_seq OWNER TO prologi;
--
-- Name: tc206_order_ftp_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc206_order_ftp_id_seq', 2, true);

--
-- Name: tc207_order_error_order_error_no_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc207_order_error_order_error_no_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc207_order_error_order_error_no_seq OWNER TO prologi;
--
-- Name: tc207_order_error_order_error_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc207_order_error_order_error_no_seq OWNED BY public.tc207_order_error.order_error_no;
--
-- Name: tc207_order_error_order_error_no_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc207_order_error_order_error_no_seq', 2634, true);

--
-- Name: tc207_order_s3_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc207_order_s3_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc207_order_s3_id_seq OWNER TO prologi;
--
-- Name: tc207_order_s3_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc207_order_s3_id_seq OWNED BY public.tc207_order_s3.id;
--
-- Name: tc207_order_s3_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc207_order_s3_id_seq', 1, false);

--
-- Name: tc208_order_cancel_order_cancel_no_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc208_order_cancel_order_cancel_no_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc208_order_cancel_order_cancel_no_seq OWNER TO prologi;
--
-- Name: tc208_order_cancel_order_cancel_no_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc208_order_cancel_order_cancel_no_seq OWNED BY public.tc208_order_cancel.order_cancel_no;
--
-- Name: tc208_order_cancel_order_cancel_no_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc208_order_cancel_order_cancel_no_seq', 4625, true);

--
-- Name: tc209_csv_template_template_cd_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc209_csv_template_template_cd_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc209_csv_template_template_cd_seq OWNER TO prologi;
--
-- Name: tc209_csv_template_template_cd_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc209_csv_template_template_cd_seq OWNED BY public.tc209_csv_template.template_cd;
--
-- Name: tc209_csv_template_template_cd_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc209_csv_template_template_cd_seq', 1, false);

--
-- Name: tc209_setting_template_template_cd_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tc209_setting_template_template_cd_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tc209_setting_template_template_cd_seq OWNER TO prologi;
--
-- Name: tc209_setting_template_template_cd_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tc209_setting_template_template_cd_seq OWNED BY public.tc209_setting_template.template_cd;
--
-- Name: tc209_setting_template_template_cd_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tc209_setting_template_template_cd_seq', 77, true);

--
-- Name: tw201_shipment_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw201_shipment_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw201_shipment_detail_id_seq OWNER TO prologi;
--
-- Name: tw201_shipment_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw201_shipment_detail_id_seq OWNED BY public.tw201_shipment_detail.id;
--
-- Name: tw201_shipment_detail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw201_shipment_detail_id_seq', 586650, true);

--
-- Name: tw212_shipment_location_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw212_shipment_location_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw212_shipment_location_detail_id_seq OWNER TO prologi;
--
-- Name: tw212_shipment_location_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw212_shipment_location_detail_id_seq OWNED BY public.tw212_shipment_location_detail.id;
--
-- Name: tw212_shipment_location_detail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw212_shipment_location_detail_id_seq', 518038, true);

--
-- Name: tw213_shipment_location_detail_history_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw213_shipment_location_detail_history_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw213_shipment_location_detail_history_id_seq OWNER TO prologi;
--
-- Name: tw213_shipment_location_detail_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw213_shipment_location_detail_history_id_seq OWNED BY public.tw213_shipment_location_detail_history.id;
--
-- Name: tw213_shipment_location_detail_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw213_shipment_location_detail_history_id_seq', 74799, true);

--
-- Name: tw214_total_picking_total_picking_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw214_total_picking_total_picking_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw214_total_picking_total_picking_id_seq OWNER TO prologi;
--
-- Name: tw214_total_picking_total_picking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw214_total_picking_total_picking_id_seq OWNED BY public.tw214_total_picking.total_picking_id;
--
-- Name: tw214_total_picking_total_picking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw214_total_picking_total_picking_id_seq', 219, true);

--
-- Name: tw215_total_picking_detail_total_picking_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw215_total_picking_detail_total_picking_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw215_total_picking_detail_total_picking_id_seq OWNER TO prologi;
--
-- Name: tw215_total_picking_detail_total_picking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw215_total_picking_detail_total_picking_id_seq OWNED BY public.tw215_total_picking_detail.total_picking_id;
--
-- Name: tw215_total_picking_detail_total_picking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw215_total_picking_detail_total_picking_id_seq', 219, true);

--
-- Name: tw216_delivery_fare_fare_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw216_delivery_fare_fare_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw216_delivery_fare_fare_id_seq OWNER TO prologi;
--
-- Name: tw216_delivery_fare_fare_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw216_delivery_fare_fare_id_seq OWNED BY public.tw216_delivery_fare.fare_id;
--
-- Name: tw216_delivery_fare_fare_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw216_delivery_fare_fare_id_seq', 1, false);

--
-- Name: tw302_stock_management_manage_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw302_stock_management_manage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw302_stock_management_manage_id_seq OWNER TO prologi;
--
-- Name: tw302_stock_management_manage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw302_stock_management_manage_id_seq OWNED BY public.tw302_stock_management.manage_id;
--
-- Name: tw302_stock_management_manage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw302_stock_management_manage_id_seq', 1, false);

--
-- Name: tw303_stock_detail_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.tw303_stock_detail_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.tw303_stock_detail_detail_id_seq OWNER TO prologi;
--
-- Name: tw303_stock_detail_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.tw303_stock_detail_detail_id_seq OWNED BY public.tw303_stock_detail.detail_id;
--
-- Name: tw303_stock_detail_detail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.tw303_stock_detail_detail_id_seq', 1, false);

--
-- Name: mc105_product_setting_set_cd_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.mc105_product_setting_set_cd_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.mc105_product_setting_set_cd_seq OWNER TO prologi;
--
-- Name: mc105_product_setting_set_cd_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.mc105_product_setting_set_cd_seq OWNED BY public.mc105_product_setting.set_cd;
--
-- Name: mc105_product_setting_set_cd_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.mc105_product_setting_set_cd_seq', 128, true);

--
-- Name: mc200_customer_delivery_delivery_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.mc200_customer_delivery_delivery_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.mc200_customer_delivery_delivery_id_seq OWNER TO prologi;
--
-- Name: mc200_customer_delivery_delivery_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.mc200_customer_delivery_delivery_id_seq OWNED BY public.mc200_customer_delivery.delivery_id;
--
-- Name: mc200_customer_delivery_delivery_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.mc200_customer_delivery_delivery_id_seq', 1608, true);

--
-- Name: ms007_setting_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms007_setting_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms007_setting_id_seq OWNER TO prologi;
--
-- Name: ms007_setting_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms007_setting_id_seq OWNED BY public.ms007_setting.id;
--
-- Name: ms007_setting_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms007_setting_id_seq', 15462, true);

--
-- Name: ms016_macro_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms016_macro_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms016_macro_id_seq OWNER TO prologi;
--
-- Name: ms016_macro_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms016_macro_id_seq OWNED BY public.ms016_macro.id;
--
-- Name: ms016_macro_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms016_macro_id_seq', 490, true);

--
-- Name: ms018_api_error_id_seq; Type: SEQUENCE; Schema: public; Owner: prologi
--
CREATE SEQUENCE public.ms018_api_error_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER TABLE public.ms018_api_error_id_seq OWNER TO prologi;
--
-- Name: ms018_api_error_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: prologi
--
ALTER SEQUENCE public.ms018_api_error_id_seq OWNED BY public.ms018_api_error.id;
--
-- Name: ms018_api_error_id_seq; Type: SEQUENCE SET; Schema: public; Owner: prologi
--
SELECT pg_catalog.setval('public.ms018_api_error_id_seq', 31, true);
