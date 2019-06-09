package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by oyj
 */
public interface IShippingService {

    //添加地址
    ServerResponse add(Integer userId, Shipping shipping);
    //删除地址
    ServerResponse<String> del(Integer userId, Integer shippingId);
    //更新地址
    ServerResponse update(Integer userId, Shipping shipping);
    //查询用户地址
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    //如果用户有多个地址，查询所有的用户地址列表进行分页输出
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
