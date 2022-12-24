package com.lemonico.common.bean;

/**
 * @className: LocationVO
 * @description: LocationVO
 * @date: 2020/06/25
 **/

public class LocationVO
{

    private String client_id;
    private String product_id;
    private String from_location;
    private String to_location;
    private Integer count;

    public String getClient_id() {
        return client_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getFrom_location() {
        return from_location;
    }

    public String getTo_location() {
        return to_location;
    }

    public Integer getCount() {
        return count;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setFrom_location(String from_location) {
        this.from_location = from_location;
    }

    public void setTo_location(String to_location) {
        this.to_location = to_location;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
