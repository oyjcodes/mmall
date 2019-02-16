package com.mmall.test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * @ClassName:BaseTest
 * @Description TODO
 * @Author oyj
 * @Date 2018/11/24 10:40
 * @Version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration("classpath:applicationContext.xml")
public class BaseTest {

}
