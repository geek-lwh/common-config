package com.aha.tech.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

@Component
public class JacksonDecoder implements Decoder {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.status() == 404) return Util.emptyValueOf(type);
        if (response.body() == null) return null;

        Collection<String> contentEncoding = response.headers().getOrDefault("Content-Encoding", Collections.EMPTY_LIST);
        if (contentEncoding.contains("gzip")) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().asInputStream());
            return objectMapper.readValue(gzipInputStream, objectMapper.constructType(type));
        }

        Reader reader = response.body().asReader();
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }
        try {
            reader.mark(1);
            if (reader.read() == -1) {
                return null;
            }
            reader.reset();


            return objectMapper.readValue(reader, objectMapper.constructType(type));
        } catch (RuntimeJsonMappingException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        }
    }
}
