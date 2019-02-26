package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import java.util.List;

/**
 * @Classname ICategoryService
 * @Description TODO
 * @Date 2019/2/16 16:07
 * @Created by oyj
 */
public interface ICategoryService {

    //添加品类
    public ServerResponse addCategory(String categoryName, Integer parentId);
    //修改品类名
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    //根据类型id查询第一层子类商品类型
    public ServerResponse<List<Category>> getChildrenCategoryByParentId(Integer categoryId);

    //根据父节点的categoryId，递归查询本节点以及所有子节点的id信息
    public ServerResponse selectCategoryAndChildById(Integer categoryId);
}
