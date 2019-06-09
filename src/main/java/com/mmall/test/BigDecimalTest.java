package com.mmall.test;

import org.junit.Test;

/**
 * @Classname BigDecimalTest
 * @Description 浮点型商业运算中丢失精度的问题
 * @Date 2019/3/23 19:10
 * @Created by oyj
 */
public class BigDecimalTest {
    @Test
    public void test1(){
        System.out.println(0.05+0.01);
    }
}
