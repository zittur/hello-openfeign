package com.zittur.consumer.controller;

import com.zittur.consumer.client.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/file",method = RequestMethod.GET)
    public String getTansat(){
        return fileService.getFileName();
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public String getTansatById(@PathVariable(value = "id") String id){
        return fileService.getFileById(id);
    }
}
