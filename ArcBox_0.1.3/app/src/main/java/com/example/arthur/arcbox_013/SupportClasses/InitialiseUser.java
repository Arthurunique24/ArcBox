package com.example.arthur.arcbox_013.SupportClasses;

/**
 * Created by Arthur on 04.05.17.
 */

public class InitialiseUser {
    private String OrdersContractCouriers;
    private String OrdersFreeCourirs;
    private String CompletedOrders;
    private String Chat;

    InitialiseUser(){

    }

    public InitialiseUser(String OrdersContractCouriers, String OrdersFreeCourirs, String CompletedOrders, String Chat){
        this.OrdersContractCouriers = OrdersContractCouriers;
        this.OrdersFreeCourirs = OrdersFreeCourirs;
        this.CompletedOrders = CompletedOrders;
        this.Chat = Chat;
    }

    public void setOrdersContractCouriers(String OrdersContractCouriers){
        this.OrdersContractCouriers = OrdersContractCouriers;
    }

    public String getOrdersContractCouriers(){
        return OrdersContractCouriers;
    }

    public void setOrdersFreeCourirs(String OrdersFreeCourirs){
        this.OrdersFreeCourirs = OrdersFreeCourirs;
    }

    public String getOrdersFreeCourirs(){
        return OrdersFreeCourirs;
    }

    public void setCompletedOrders(String CompletedOrders){
        this.CompletedOrders = CompletedOrders;
    }

    public String getCompletedOrders(){
        return  CompletedOrders;
    }

    public void setChat(String Chat){
        this.Chat = Chat;
    }

    public String getChat(){
        return Chat;
    }

}
