package com.lemonico.api.utils.Rakuten;



import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RakutenPostReturn
{

    /** 密钥Base64加密 */
    String serviceSecret = "SP394788_WChhql0dWNuxzSFy";
    String licenseKey = "SL394788_pt5J2KBmfGgmVlwN";
    // String authorization = "ESA U1AzOTQ3ODhfV0NoaHFsMGRXTnV4elNGeTpTTDM5NDc4OF9wdDVKMktCbWZHZ21WbHdO";
    String authorization = "ESA " + Base64.getEncoder().encodeToString((serviceSecret + ":" + licenseKey).getBytes());

    private final static Logger logger = LoggerFactory.getLogger(RakutenPostReturn.class);

    //// @Scheduled(fixedDelay = 1000 * 180)
    public void axis() {
        try {
            // 指出service所在完整的URL
            String endpoint = "https://api.rms.rakuten.co.jp/es/1.0/inventory/ws";
            // 调用接口的targetNamespace
            String targetNamespace = "https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi";
            // 所调用接口的方法method
            String method = "getInventoryExternal";

            Service service = new Service();
            Call call = (Call) service.createCall();
            // 命名空间
            call.setTargetEndpointAddress(new URL(endpoint));
            // 需要请求的方法
            call.setOperationName(new QName(targetNamespace, method));
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(targetNamespace + "/" + method);
            // 参数
            call.addParameter("param1", XMLType.SOAP_STRING, ParameterMode.IN);
            // String param1 = makeXml();
            // 设置返回值
            call.setReturnType(XMLType.SOAP_DOCUMENT);
            // String result = "";
            // 调用获取返回值
            Object invoke = call.invoke(new Object[] {});
            System.err.println("请求结果:{}" + invoke);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接xml生成请求数据提（暂时废弃）
     * 生成请求xml数据
     * 
     * @param :methodName 方法名 "getInventoryExternal"
     * @param :todoInfo 数据 (key为wsdl文件中参数的name值注意大小写和顺序都要保持一致,value为实际值)
     * @return
     */
    // private String makeXml() {
    // logger.info("=======生成xml======");
    // StringBuffer sb = new StringBuffer();
    // sb.append("\n"+
    //// "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
    // "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\n"+
    // " xmlns:ws=\"http://orderapi.rms.rakuten.co.jp/rms/mall/order/api/ws\">\n" +
    // "<soapenv:Header/>\n" +
    // "<soapenv:Body>\n" +
    // "<ws:getInventoryExternal>\n" +
    // "<externalUserAuthModel>\n" +
    // "<authKey>ESA U1AzOTQ3ODhfV0NoaHFsMGRXTnV4elNGeTpTTDM5NDc4OF9wdDVKMktCbWZHZ21WbHdO</authKey>\n"+
    // "<shopUrl>sunseer</shopUrl>\n"+
    // "<userName></userName>\n"+
    // "</externalUserAuthModel>\n"+
    // "<getRequestExternalModel>\n"+
    // "<inventorySearchRange>0-50</inventorySearchRange>\n"+
    // "<itemUrl>[10000023,10000018,10000015]</itemUrl>\n"+
    // "</getRequestExternalModel>\n"+
    // "</ws:getInventoryExternal>\n"+
    // "</soapenv:Body>\n"+
    // "</soapenv:Envelope>\n");
    // logger.info(String.valueOf(sb));
    // logger.info("=======生成xml结束======");
    // return sb.toString();
    // }



    // 获取库存信息
    //// @Scheduled(fixedDelay = 1000 * 180)
    public GetResponseExternalItem[] getInventoryTest() throws ServiceException {
        InventoryapiLocator inventoryapiLocator = new InventoryapiLocator();
        InventoryapiPort_PortType inventoryapiPort_portType = null;
        try {
            inventoryapiPort_portType = inventoryapiLocator.getinventoryapiPort();
        } catch (ServiceException se) {
            logger.warn("错误信息" + se.getLocalizedMessage());
        }



        // Auth认证
        ExternalUserAuthModel auth = new ExternalUserAuthModel();
        auth.setShopUrl("sunseer");
        auth.setAuthKey(authorization);
        auth.setUserName("sunseer");

        // 库存数量检索条件
        GetRequestExternalModel model = new GetRequestExternalModel();
        // List<String> collect = list.stream().map(Mc100_product::getProduct_id).collect(Collectors.toList());
        ArrayList<String> items = new ArrayList<String>();
        items.add("10000022");
        items.add("10000021");
        items.add("10000019");
        model.setItemUrl(items.toArray(new String[0]));
        // 写入查询库存数量范围
        // model.setInventorySearchRange("500");


        GetResponseExternalModel result = null;

        try {
            // 提交
            result = inventoryapiPort_portType.getInventoryExternal(auth, model);
            // 获取Respone
            if ("N00-000".equals(result.getErrCode())) {
                logger.info("success");
                GetResponseExternalItem[] getResponseExternalItem = result.getGetResponseExternalItem();
                System.out.println("ResponseExternalItem———length———" + getResponseExternalItem.length);
                for (int i = 0; i < getResponseExternalItem.length; i++) {
                    System.out.println("ItemUrl———" + getResponseExternalItem[i].getItemUrl());
                    System.out.println("InventoryType———" + getResponseExternalItem[i].getInventoryType());
                    System.out.println("RestTypeFlag———" + getResponseExternalItem[i].getRestTypeFlag());
                    System.out.println("NokoriThreshold———" + getResponseExternalItem[i].getNokoriThreshold());
                    System.out.println("ItemNumber———" + getResponseExternalItem[i].getItemNumber());
                    GetResponseExternalItemDetail[] itemDetail =
                        getResponseExternalItem[i].getGetResponseExternalItemDetail();
                    System.out.println("itemDetail———length———" + itemDetail.length);
                    for (int j = 0; j < itemDetail.length; j++) {
                        System.out.println("getInventoryCount———" + itemDetail[j].getInventoryCount());
                        System.out.println("getHChoiceName———" + itemDetail[j].getHChoiceName());
                        System.out.println("getVChoiceName———" + itemDetail[j].getVChoiceName());
                        System.out.println("getOrderSalesFlag———" + itemDetail[j].getOrderSalesFlag());
                        System.out.println("getLackDeliveryId———" + itemDetail[j].getLackDeliveryId());
                        System.out.println("getInventoryBackFlag———" + itemDetail[j].getInventoryBackFlag());
                        System.out.println("getNormalDeliveryId———" + itemDetail[j].getNormalDeliveryId());
                        System.out.println("getOrderFlag———" + itemDetail[j].getOrderFlag());

                    }
                }
                return getResponseExternalItem;
            } else {
                logger.error("获取库存发生错误！！！！");
            }
        } catch (RemoteException re) {
            logger.warn("错误信息" + re.getMessage());
        }
        return null;
    }

    // 修改库存
    //// @Scheduled(fixedDelay = 1000 * 180)
    public void updateInventoryTest() {
        InventoryapiLocator inventoryapiLocator = new InventoryapiLocator();
        InventoryapiPort_PortType inventoryapiPort_portType = null;
        try {
            inventoryapiPort_portType = inventoryapiLocator.getinventoryapiPort();
        } catch (ServiceException se) {
            logger.warn("错误信息" + se.getLocalizedMessage());
        }

        // Auth认证
        ExternalUserAuthModel auth = new ExternalUserAuthModel();
        auth.setShopUrl("sunseer");
        auth.setAuthKey(authorization);
        auth.setUserName("sunseer");

        // 库存信息更新请求模型
        UpdateRequestExternalModel model = new UpdateRequestExternalModel();
        // ArrayList<String> items = new ArrayList<String>();
        // items.add("10000019");
        // items.add("10000023");
        // items.add("10000018");
        UpdateRequestExternalItem[] updateItem = model.getUpdateRequestExternalItem();
        /**
         * 库存信息更新请求项目模型[多个]
         * 初始版本为读取对应的商品只改变库存数量 其他数据原封不动返回
         */
        ArrayList<UpdateRequestExternalItem> items = new ArrayList<>();
        UpdateRequestExternalItem updateRequestExternalItem = new UpdateRequestExternalItem();
        // 产品管理编号（产品URL）
        updateRequestExternalItem.setItemUrl("10000019");
        // 库存类型
        updateRequestExternalItem.setInventoryType(2);
        /**
         * 库存数量显示
         * 当库存类型为“ 2：常规库存设置”时可以设置。
         */
        // updateRequestExternalItem.setRestTypeFlag(1);
        // 按项目的库存水平轴选项
        // updateRequestExternalItem.setHChoiceName(null);
        // 垂直轴选项（按项目）库存
        // updateRequestExternalItem.setVChoiceName(null);
        // 按物料选择库存可显示订单
        // updateRequestExternalItem.setOrderFlag(0);
        // 按项目列出库存的剩余显示阈值
        // updateRequestExternalItem.setNokoriThreshold(0);
        /**
         * 库存更新模式
         * 0: 没做什么
         * 1: 初始值设定 (设置何时要使用绝对值更新库存数量)
         * 2： 加
         * 3： 减去
         */
        updateRequestExternalItem.setInventoryUpdateMode(1);
        // 库存数量
        updateRequestExternalItem.setInventory(7);
        // 退货标志
        // updateRequestExternalItem.setInventoryBackFlag(1);
        // 正常交货日期信息删除标志
        // updateRequestExternalItem.setNormalDeliveryDeleteFlag(false);
        // 普通送货ID
        // updateRequestExternalItem.setNormalDeliveryId(1000);
        // 缺货时交货日期信息删除标志
        // updateRequestExternalItem.setLackDeliveryDeleteFlag(false);
        // 缺货时的交货编号
        // updateRequestExternalItem.setLackDeliveryId(0);
        // 缺货销售标志
        // updateRequestExternalItem.setOrderSalesFlag(1);

        items.add(updateRequestExternalItem);

        model.setUpdateRequestExternalItem(items.toArray(new UpdateRequestExternalItem[0]));

        UpdateResponseExternalModel result = null;

        try {
            // 提交
            result = inventoryapiPort_portType.updateInventoryExternal(auth, model);
            // 获取Respone
            System.err.println(result.getErrCode());
            // System.err.println(result.getErrMessage());
            if ("N00-000".equals(result.getErrCode())) {
                logger.info("success");
                // 乐天的修改库存返回值
                // 如果成功修改 则返回的数组为null
                UpdateResponseExternalItem[] externalItem = result.getUpdateResponseExternalItem();
                System.err.println(externalItem);
                // for (int i = 0; i<externalItem.length;i++) {
                // System.out.println("ItemUrl---"+externalItem[i].getItemUrl());
                // System.out.println("VChoiceName---"+externalItem[i].getVChoiceName());
                // System.out.println("HChoiceName---"+externalItem[i].getHChoiceName());
                // System.out.println("ItemErrCode---"+externalItem[i].getItemErrCode());
                // System.out.println("ItemErrMessage---"+externalItem[i].getItemErrMessage());
                // }
            }

        } catch (RemoteException re) {
            logger.warn("错误信息" + re.getMessage());
        }
    }
}
