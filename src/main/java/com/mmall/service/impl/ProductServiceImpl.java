package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname ProductServiceImpl
 * @Description TODO
 * @Date 2019/2/26 14:35
 * @Created by oyj
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product != null){
            String[] subImageArray = product.getSubImages().split(",");
            if(subImageArray.length > 0){
                product.setMainImage(subImageArray[0]);
            }
            if(product.getId() != null){
                //对商品进行更新操作
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0){
                    ServerResponse.createBySuccessMessage("商品更新成功");
                }
                return  ServerResponse.createBySuccessMessage("商品更新失败");
            }else{
                //id为空，则对商品进行插入保存操作
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    ServerResponse.createBySuccessMessage("商品插入成功");
                }
                ServerResponse.createBySuccessMessage("商品插入失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新的产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId ==null || status ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("商品销售状态修改成功");
            }
            return ServerResponse.createByErrorMessage("商品销售状态修改失败");
        }
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product==null){
                return ServerResponse.createByErrorMessage("产品已经下架或删除");
            }
            ProductDetailVo productDetailVo = assembleProductDetail(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
    }
    public ProductDetailVo assembleProductDetail(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setDetail(productDetailVo.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());

        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.jkshop.com/"));
        //createTime
        //updateTime
        productDetailVo.setCreateTime(DataTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DataTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startPage
        //填充自己的额sql查询逻辑
        //pageHelper首尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.jkshop.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    //根据产品名或产品id查询分页的商品信息
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }


}
