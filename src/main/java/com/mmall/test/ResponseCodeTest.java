package com.mmall.test;

import com.mmall.common.ResponseCode;
import org.junit.Test;

/**
 * @ClassName:ResponseCodeTest
 * @Description 测试枚举类
 * @Author oyj
 * @Date 2018/11/24 10:42
 * @Version 1.0
 **/
public class ResponseCodeTest extends BaseTest {
    @Test
    public void test1() {
        ResponseCode rs1 = ResponseCode.SUCCESS;
        System.out.println("状态码"+rs1.getCode());
        System.out.println("装态描述"+rs1.getDesc());
    }
}
