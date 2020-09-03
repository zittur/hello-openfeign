package com.zittur.producer.controller;

import com.zittur.common.exception.FileNotFoundException;
import com.zittur.producer.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    //正常返回结果
    @GetMapping(value = "/file")
    public String getFileName() {
        return fileService.getFileName();
    }

    //测试故意抛出异常
    @GetMapping(value = "/{id}")
    public String getFileById(@PathVariable ("id") String id) throws FileNotFoundException {
        throw new FileNotFoundException("file not found");
    }
}
