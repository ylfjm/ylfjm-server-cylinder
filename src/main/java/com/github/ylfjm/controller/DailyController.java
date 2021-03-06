package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.dto.DailyDTO;
import com.github.ylfjm.pojo.po.Daily;
import com.github.ylfjm.pojo.vo.DailyDateVO;
import com.github.ylfjm.service.DailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：日报
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class DailyController {

    private final DailyService dailyService;

    /**
     * 创建日报
     *
     * @param dailyDTO 日报数据
     */
    @PostMapping(value = "/daily")
    public void add(@RequestBody DailyDTO dailyDTO) {
        dailyService.add(dailyDTO);
    }

    /**
     * 删除日报
     *
     * @param id 日报ID
     */
    @DeleteMapping(value = "/daily/{id}")
    public void remove(@PathVariable("id") Integer id) {
        dailyService.delete(id);
    }

    /**
     * 查询日报列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/daily/show/{pageNum}/{pageSize}")
    public PageVO<DailyDateVO> pageShow(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return dailyService.pageShow(pageNum, pageSize);
    }

    /**
     * 查询日报列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/daily/{pageNum}/{pageSize}")
    public PageVO<Daily> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                              @RequestParam(required = false) String searchType) {
        return dailyService.page(pageNum, pageSize, searchType);
    }

}
