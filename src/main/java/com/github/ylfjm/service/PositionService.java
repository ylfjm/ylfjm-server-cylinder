package com.github.ylfjm.service;

import com.github.ylfjm.mapper.PositionMapper;
import com.github.ylfjm.pojo.po.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Zhang Bo
 * @Date 2020/12/6
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PositionService {

    private final PositionMapper positionMapper;

    /**
     * 查询职位列表
     */
    public List<Position> list() {
        return positionMapper.selectAll();
    }

}
