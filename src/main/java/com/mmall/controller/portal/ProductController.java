package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname ProductController
 * @Description TODO
 * @Date 2019/3/21 22:54
 * @Created by oyj
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required = false)String keyword,
                                         @RequestParam(value="categoryId",required = false)Integer categoryId,
                                         @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value="orderBy",defaultValue = "")String orderBy
                                         ){
        return iProductService.getProductByKeyWordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
