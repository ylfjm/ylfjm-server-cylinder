package com.github.ylfjm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2020/11/19
 */
@SpringBootApplication
@MapperScan("com.github.ylfjm.mapper")
public class CylinderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CylinderApplication.class, args);
    }
}
