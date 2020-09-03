package com.zittur.common.decoder;

import com.zittur.common.exception.FileNotFoundException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static feign.FeignException.errorStatus;

@Configuration
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            //TODO: 将 body 转为 JSON 格式
            String body = Util.toString(response.body().asReader());
            System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO：直接考虑具体错误状态码，换成 Swith & Case 形式
        if (response.status() >= 400 && response.status() <= 499) {
            // 客户端报错
            System.out.println(response.status());
            if (response.status() == 404) {
                return new FileNotFoundException("File not Found");
            }
        }
        if (response.status() >= 500 && response.status() <= 599) {
            return new FileNotFoundException(
                    response.reason()
            );
        }
        return errorStatus(methodKey, response);
    }
}

