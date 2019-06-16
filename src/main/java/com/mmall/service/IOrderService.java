package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by oyj
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
    public ServerResponse aliCallback(Map<String,String> params);
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    public  ServerResponse createOrder(Integer userId,Integer shippingId);
    public ServerResponse<String> cancel(Integer userId,Long orderNo);
    public ServerResponse getOrderCartProduct(Integer userId);
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize);



    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    public ServerResponse<OrderVo> manageDetail(Long orderNo);
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    public ServerResponse<String> manageSendGoods(Long orderNo);

}
