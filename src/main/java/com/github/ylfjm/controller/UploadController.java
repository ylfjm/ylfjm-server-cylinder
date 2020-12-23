package com.github.ylfjm.controller;

import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.constant.Constant;
import com.github.ylfjm.common.utils.UUIDUtil;
import com.github.ylfjm.context.SpringContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 描述：文件上传
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private static final Pattern IMAGE_PATTERN = Pattern.compile(".*(.jpg|.JPG|.png|.PNG|.gif|.GIF|.jpeg|.JPEG)$");
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;
    @Value("${file.resourceLocations}")
    private String resourceLocations;

    /**
     * 单个文件上传接口
     *
     * @param imgFile 文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart(value = "imgFile", required = false) MultipartFile imgFile, HttpServletRequest request) {
        if (imgFile == null) {
            throw new BadRequestException("文件不能为空");
        }
        long fileSize = imgFile.getSize();
        //头像文件合法性检查
        String fileName = imgFile.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new BadRequestException("文件不能为空");
        }
        if (IMAGE_PATTERN.matcher(fileName).matches()) {
            if (fileSize > 10 * 1024 * 1024) {
                throw new BadRequestException("图片文件大小不能超过10M");
            }
        } else {
            throw new BadRequestException("暂不支持该格式文件上传");
        }

        String prefix = new SimpleDateFormat("yyyy_MM_dd_").format(Calendar.getInstance().getTime());
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = prefix + UUIDUtil.uuid() + suffix;
        String path = staticAccessPath.substring(0, staticAccessPath.lastIndexOf("/") + 1);
        String fileServerPath;
        if (Objects.equals(Constant.PROD, SpringContext.getEnvironment())) {
            //生产环境
            fileServerPath = "http://192.168.10.63:8003" + path + newFileName;
        } else {
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            fileServerPath = basePath + path + newFileName;
        }
        File saveFile = new File(resourceLocations, newFileName);
        try {
            //文件保存
            imgFile.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("文件上传出错");
        }
        log.info("文件访问路径：" + fileServerPath);
        log.info("文件保存路径：" + saveFile.getPath());
        return fileServerPath;
    }

}
