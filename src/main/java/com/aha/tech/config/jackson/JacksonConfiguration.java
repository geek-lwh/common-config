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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "use.common.jackson", matchIfMissing = true)
public class JacksonConfiguration {

    public final Logger logger = LoggerFactory.getLogger(JacksonConfiguration.class);


    public static TimeZone defaultTimezone = TimeZone.getTimeZone("GMT+8");

    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";


    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setTimeZone(defaultTimezone);

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT_FULL));

        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantCustomSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL, Locale.SIMPLIFIED_CHINESE)));
        javaTimeModule.addDeserializer(Instant.class, new InstantCustomDeserializer());

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL, Locale.SIMPLIFIED_CHINESE)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL, Locale.SIMPLIFIED_CHINESE)));

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT, Locale.SIMPLIFIED_CHINESE)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT, Locale.SIMPLIFIED_CHINESE)));

        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DATE_FORMAT_FULL)));
        javaTimeModule.addDeserializer(Date.class, new DateCustomDeserializer());

        objectMapper.registerModule(javaTimeModule);

        logger.info("json序列化初始化完成 [jackson] dateFormat : {} , namingStrategy : {} ", DATE_FORMAT_FULL, objectMapper.getPropertyNamingStrategy());
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
