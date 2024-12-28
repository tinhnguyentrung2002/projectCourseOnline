package com.example.courseonline.Domain;

import java.util.Date;

public class PaymentHistoryClass {
    private String order_id;
    private Date order_date;
    private int order_status;
    private double order_amount;
    private String order_item_id;
    private String order_payer_id;
    private String order_pay_method;

    public PaymentHistoryClass(String order_id, Date order_date, int order_status, double order_amount, String order_item_id, String order_payer_id, String order_pay_method) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_status = order_status;
        this.order_amount = order_amount;
        this.order_item_id = order_item_id;
        this.order_payer_id = order_payer_id;
        this.order_pay_method = order_pay_method;
    }

    public PaymentHistoryClass() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public String getOrder_payer_id() {
        return order_payer_id;
    }

    public void setOrder_payer_id(String order_payer_id) {
        this.order_payer_id = order_payer_id;
    }

    public String getOrder_pay_method() {
        return order_pay_method;
    }

    public void setOrder_pay_method(String order_pay_method) {
        this.order_pay_method = order_pay_method;
    }
}
