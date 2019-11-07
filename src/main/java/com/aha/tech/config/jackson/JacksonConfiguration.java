package com.aha.tech.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @Author: luweihong
 * @Date: 2018/12/14
 */
@Configuration
public class JacksonConfiguration {

    public static TimeZone defaultTimezone = TimeZone.getTimeZone("GMT+8");

    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";



    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        //objectMapper.setTimeZone(defaultTimezone);

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        //objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //LocalDateTime 类型自动转换yyyy-MM-dd HH:mm:ss格式日期字符串
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL, Locale.SIMPLIFIED_CHINESE)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL, Locale.SIMPLIFIED_CHINESE)));

        //LocalDate 类型自动转换yyyy-MM-dd
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT, Locale.SIMPLIFIED_CHINESE)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT, Locale.SIMPLIFIED_CHINESE)));

        //javaTimeModule.addSerializer(Instant.class, new InstantCustomSerializer(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT_FULL)));
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DATE_FORMAT_FULL)));
        //javaTimeModule.addDeserializer(Instant.class, new InstantCustomDeserializer());
        javaTimeModule.addDeserializer(Date.class, new DateCustomDeserializer());

        //objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(javaTimeModule);
//        JacksonUtil.setMapper(objectMapper);
        return objectMapper;
    }

    class InstantCustomSerializer extends JsonSerializer<Instant> {
        private DateTimeFormatter format;

        private InstantCustomSerializer(DateTimeFormatter formatter) {
            this.format = formatter;
        }

        @Override
        public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (instant == null) {
                return;
            }
            String jsonValue = format.format(instant.atZone(ZoneId.systemDefault()));
            jsonGenerator.writeString(jsonValue);
        }
    }

    class InstantCustomDeserializer extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText().trim();
            if (StringUtils.isNotBlank(dateString)) {
                try {
                    Date pareDate = (new SimpleDateFormat(DATE_FORMAT_FULL)).parse(dateString);
                    if (null != pareDate) {
                        return pareDate.toInstant();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    class DateCustomDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText().trim();
            if (StringUtils.isNotBlank(dateString)) {
                try {
                    return (new SimpleDateFormat(DATE_FORMAT_FULL)).parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
