package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.DailyProject;
import com.github.ylfjm.service.DailyProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class DailyProjectController {

    private final DailyProjectService dailyProjectService;

    @PostMapping(value = "/dailyProject")
    public void add(@RequestBody DailyProject dailyProject) {
        dailyProjectService.add(dailyProject);
    }

    @DeleteMapping(value = "/dailyProject/{id}")
    public void remove(@PathVariable("id") Integer id) {
        dailyProjectService.delete(id);
    }

    @PutMapping(value = "/dailyProject")
    public void update(@RequestBody DailyProject dailyProject) {
        dailyProjectService.update(dailyProject);
    }

    @GetMapping(value = "/dailyProject/{pageNum}/{pageSize}")
    public PageVO<DailyProject> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return dailyProjectService.page(pageNum, pageSize);
    }

}
