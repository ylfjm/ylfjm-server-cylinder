package com.github.ylfjm.controller;

import com.github.ylfjm.pojo.po.Position;
import com.github.ylfjm.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：职位
 *
 * @Author Zhang Bo
 * @Date 2020/12/6
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * 查询职位列表
     */
    @GetMapping(value = "/position")
    public List<Position> page() {
        return positionService.list();
    }

}
