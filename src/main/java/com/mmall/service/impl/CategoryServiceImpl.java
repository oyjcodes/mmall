package com.mmall.service.impl;

/**
 * @Classname CategoryServiceImpl
 * @Description TODO
 * @Date 2019/2/16 16:25
 * @Created by oyj
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Classname CategoryService
 * @Description TODO
 * @Date 2019/2/16 16:07
 * @Created by oyj
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId==null || StringUtils.isBlank(categoryName)){
            ServerResponse.createByErrorMessage("商品类型参数错误");
        }
        //添加商品类型
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount>0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    //修改品类名
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null || StringUtils.isBlank(categoryName)){
            ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        //更新品类参数
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("品类名更新成功");
        }
        return ServerResponse.createBySuccessMessage("品类名更新失败");
    }
    //根据父节点的categoryId，非递归查询所有子节点的信息
    public ServerResponse<List<Category>> getChildrenCategoryByParentId(Integer categoryId){
        List<Category> categoryList =  categoryMapper.getChildrenCategoryByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }
    //根据父节点的categoryId，递归查询本节点以及所有子节点的id信息
    public ServerResponse selectCategoryAndChildById(Integer categoryId){
       Set<Category> categorySet = Sets.newHashSet();
       findChidrenCategory(categorySet,categoryId);
       List<Integer> categoryIdList = Lists.newArrayList();
       if(categoryId != null){
           for(Category category : categorySet){
               categoryIdList.add(category.getId());
           }
       }
       return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归查询孩子分类节点
    private Set<Category> findChidrenCategory(Set<Category> categorySet,Integer categoryId){
      // 0->10000->1233
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        //如果当前节点不为空，这添加到Set集合
        if(category != null){
            categorySet.add(category);
        }
        List<Category> childrenList = categoryMapper.getChildrenCategoryByParentId(categoryId);
        for(Category childCategory : childrenList){
            //获取孩子节点，递归调用findChidrenCategory方法
            findChidrenCategory(categorySet,childCategory.getId());
        }
        return categorySet;
    }
}

