# 基础组件使用说明

请使用最新版本,查看链接:http://testm.ahaschool.com:9200/nexus/content/repositories/huijiame_repo/com/aha/public/aha-commons-config/

特别注意:config包下有许多默认配置,如果不满足业务要求可以通过参数自行配置.

##  com.aha.tech.annotation

### Authentication.java

@Authentication,配合com.aha.tech.aop.AuthenticationHandlerAop.java 使用,达到请求时候判断是否存在userId,不存在则拒绝.

-

###  XEnv.java

@Xenv 公共的头对象,配合XenvDto,XenvThreadLocal,EnvInterceptor使用,达到针对http请求头中的参数绑定到当前线程Threadlocal对象中

-
## com.aha.tech.aop

### AuthenticationHandlerAop.java

在controller方法执行前,判断是否存在@Authentication注解,存在则执行判断,判断该接口参数中是否携带合法的userId.

-
##  com.aha.tech.component

### CatLogbackComponent.java

将cat的日志信息集成到logback中,需要在logback中引用.

-
###  FeignHystrixComponent.java

同时使用feign+hytrix会遇到线程中的变量无法传递的问题,原因是hytrix在遇到feign请求时会开启一个子线程,因为线程可见性问题,导致无法获取父类线程的变量,使用该组件重写了hytrix创建子线程的方法,将父线程参数变量传递给新开启的子线程.

-

###  FeignDecoderComponent.java
使用feign发送和接收http报文时,使用该类作为数据decoder时的方案

-

###  FeignEncoderComponent.java

使用feign发送和接收http报文时,使用该类作为数据encoder时的方案

-


##  com.aha.tech.config.cat

### CatFilterConfiguration.java,CatContextFilter.java,CatContext.java


定义了cat过滤器,通过CatContext初始化上线文维护一个map,使用catContextFilter进行值的初始化,并且setContext.通过@ConditionalOnProperty(name = "use.common.cat") 进行使用或者禁用配置

-

##  com.aha.tech.config.http

### HttpClientConfiguration.java,RestTemplateConfiguration.java,HttpInterceptor.java

HttpClientConfiguration.java定义了http连接池,http长连接的属性,restTemplateConfiguration.java使用了httpClientConfiguration.java,并且httpInterceptor.java针对http的每一个请求增加了http头信息等功能.

通过@ConditionalOnProperty(name = "use.common.http", matchIfMissing = true)进行使用或者禁用配置
-

## com.aha.tech.config.jackson

### JacksonConfiguration.java

全局的jackson配置,定义了针对时间类型date,instant,localDateTime,LocalDate的序列化和反序列化格式.以及字符串驼峰与下划线的转换.被spring webConfiguration引用
通过@ConditionalOnProperty(name = "use.common.jackson",matchIfMissing = true)进行使用或者禁用配置
## com.aha.tech.config.jdbc

### MongodbConfiguration.java

数据源的定义,通过@ConditionalOnProperty(name = "use.common.mongodb",matchIfMissing = true) 进行使用或者禁用配置

### DataSourceConfiguration.java

数据源的定义,通过@ConditionalOnProperty(name = "use.common.jdbc",matchIfMissing = true) 进行使用或者禁用配置

-
## com.aha.tech.config.kafka

### KafkaConfiguration.java

消息系统定义,通过@ConditionalOnProperty(name = "use.common.kafka")进行使用或者禁用配置.

-

## com.aha.tech.config.mybatis

### MybatisConfiguration.java,CatMybatisPlugin.java,PagePlugin.java

mybatis框架定义,客户端需要自行实现scan类,扫描mapper接口的package路径配合使用,增加了cat针对mybatis的监控,以及分页插件.通过@ConditionalOnProperty(name = "use.common.jdbc")
进行使用或者禁用配置

-

## com.aha.tech.config.redis

### RedisConfiguration.java,RedissonConfig.java,FastJsonRedisSerializer.java

redis配置类,使用lettcue框架,多路复用,线程安全,配合redisson框架和fastJson作为数据的序列化方式.

-

## com.aha.tech.config.swagger


### SwaggerConfiguration.java

swagger文档配置类,通过@ConditionalOnProperty(name = "use.common.swagger")
进行使用或者禁用配置

-

##com.aha.tech.config.thread

### TaskThreadPoolConfiguration

定义spring task执行时候使用的线程池,通过@ConditionalOnProperty(name = "use.common.task.thread", matchIfMissing = true)
进行使用或者禁用配置

-

### ThreadPoolConfiguration.java

定义了系统的核心线程池coreThreadPool和非核心线程池nonCoreThreadPool 通过@ConditionalOnProperty(name = "use.common.core.thread", matchIfMissing = true)
进行使用或者禁用配置

-

## com.aha.tech.config.web

### TomcatConfiguration.java

tomcat容器启动时的参数配置,配置都有默认值,可以参考,通过@ConditionalOnProperty(name = "use.common.tomcat.server")
进行使用或者禁用配置

-

### WebConfiguration.java
spring web配置,定义了webMvc使用什么协议交互,如何序列化,以及拦截器的使用和拦截器的过滤条件.

-

## com.aha.tech.exception

### AuthenticationFailedException.java
定义了认证失败的错误类

-

### BaseException.java
定义了错误的自维护异常基类.

-

## com.aha.tech.filter.cat

### CatContextFilter.java,CatContext.java

参考CatConfiguration.java

-

### CrossDomainRequestFilter.java

跨域请求过滤器

-

### RequestResponseLogFilter.java

打印request body和response body 信息.并且过滤了一些非业务请求信息

-

## com.aha.tech.interceptor

### FeignRequestInterceptor.java

使用feign请求时的拦截器,针对业务上header头透传的需求,进行了公有设计.

-


### EnvInterceptor.java,XEnvThreadLocal.java,XenvDto.java

env拦截器,针对http request请求携带的header信息,在controller层获取不便,增加此拦截器进行拦截,并且放置到当前线程的threadlocal中进行保存.


# DEMO

<code>

    #server.port = 9894
    spring.application.name = accountserver
    #server.servlet.context-path = /aha-account
    
    #jackson
    #spring.jackson.property-naming-strategy = com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
    #spring.jackson.date-format = yyyy-MM-dd HH:mm:ss
    #spring.jackson.time-zone = GMT+8
    
    # use common config
    use.common.swagger = true
    use.common.jdbc = true
    use.common.redis = true
    use.common.kafka = false
    use.common.http = true
    use.common.web = true
    user.common.core.thread = true
    user.common.task.thread = true
    use.common.tomcat.server = true
    use.common.cat = true
    use.common.mongodb = true

    #mongodb
    common.mongodb.host = localhost:27017
    common.mongodb.database = game
    common.mongodb.username = 
    common.mongodb.pwd = 
    
    # tomcat
    common.server.tomcat.port = 9894
    common.server.tomcat.contextPath = /aha-account
    
    
    #swagger
    common.swagger.namespace = accountserver
    
    # mysql
    common.jdbc.driverClassName = com.mysql.jdbc.Driver
    common.jdbc.jdbcUrl = jdbc:mysql://test6-basics:3306/account?useUnicode=true&characterEncoding=utf8
    common.jdbc.username = hjm_dev
    common.jdbc.password = hjm_dev
    common.jdbc.connectionTimeout = 5000
    common.jdbc.idleTimeout = 1000
    common.jdbc.maximumPoolSize = 50
    common.jdbc.minimumIdle = 5
    
    
    
    ##redis
    common.redis.host = test6-basics
    common.redis.port = 6379
    common.redis.password = 
    # 连接超时时间（毫秒）
    common.redis.timeout = 10000
    # 区分database
    common.redis.database = 2
    # 连接池最大连接数（使用负值表示没有限制） 默认 8
    common.redis.max-active = 10
    # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
    common.redis.max-wait = 3000
    # 连接池中的最大空闲连接 默认 8
    common.redis.max-idle = 5
    # 连接池中的最小空闲连接 默认 0
    common.redis.min-idle = 0
    
    # kafka
    common.kafka.enable = on
    common.kafka.producer.bootstrap-servers = test6-basics:9092
    common.kafka.producer.retries = 3
    common.kafka.producer.batch-size = 8096
    common.kafka.producer.linger-ms = 5
    common.kafka.producer.buffer-memory = 33554432
    common.kafka.poll.timeout = 2000
    
    common.kafka.consumer.bootstrap-servers = test6-basics:9092
    common.kafka.consumer.group-id = ${spring.application.name}
    common.kafka.consumer.enable-auto-commit = false
    common.kafka.consumer.auto-commit-interval-ms = 1000
    common.kafka.consumer.session-timeout-ms = 30000
    common.kafka.consumer.max-poll-records = 500
    common.kafka.consumer.max.poll.interval.ms = 15000
    #earliest,latest
    common.kafka.consumer.auto-offset-reset = latest
    
    # http pool
    common.http.connect.timeout = 5000
    common.http.request.timeout = 5000
    common.http.socket.timeout = 10000
    common.http.max.total.connections = 100
    common.http.keep.alive.timeout = 15000
    common.http.close.idle.connection.wait.timeout = 30
    
    # core thread
    common.core.thread.pool.size = 100
    common.core.thread.max.pool.size = 200
    common.core.thread.queue.capacity.size = 1000
    common.core.thread.name.prefix = core-thread
    
    # task thread
    common.task.pool.size = 10
</code>

## 接入cat需要的本地文件

<code>
    mkdir /data/appdatas/cat
    mkdir /data/applogs/cat
    vi /data/appdatas/cat/client.xml
    
    将以下内容复制到client.xml中,其中ip是cat服务器地址
    <?xml version="1.0" encoding="utf-8"?>
    <config xmlns:xsi="http://www.w3.org/2001/XMLSchema" xsi:noNamespaceSchemaLocation="config.xsd">
        <servers>
            <server ip="10.10.163.249" port="2280" http-port="8080" />
        </servers>
    </config>
    
</code>
