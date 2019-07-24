package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId );

    //根据商品名,类型查询所有的商品信息
    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList") List<Integer> categoryIdList);

    //这里一定要用Integer，因为int无法为NULL，考虑到很多商品已经删除的情况。
    Integer selectStockByProductId(Integer id);

}