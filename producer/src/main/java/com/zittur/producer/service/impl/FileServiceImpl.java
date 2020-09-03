package com.zittur.producer.service.impl;

import com.zittur.producer.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String getFileName() {
        return "FileName is returned，Success! ";
    }
}
