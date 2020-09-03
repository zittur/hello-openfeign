package com.zittur.consumer.client;

import com.zittur.common.decoder.FeignErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "${fileservice.name}",
        url = "${fileservice.address}:${fileservice.port}", configuration = FeignErrorDecoder.class)
public interface FileService {
    @RequestMapping(value = "/file",method = RequestMethod.GET)
    String getFileName();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    String getFileById(@PathVariable("id") String id);
}
