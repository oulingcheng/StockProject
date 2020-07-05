package com.olc.ejdemo;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class EjdemoApplicationTests {

    @Test
    public void test(){
        boolean b = BigDecimal.valueOf(100000000).compareTo(BigDecimal.valueOf(9000)) > 0;
        System.out.println("result:"+b);
    }

}
