package com.aha.tech.component;


import com.aha.tech.util.IpUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 将服务注册到consul,
 * @Author: luweihong
 * @Date: 2020/8/4
 */
@Component
public class ConsulRegister implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(ConsulRegister.class);

    private final static String PROTOCOL_PREFIX = "http://";

    private final static String API_SUFFIX = "/actuator/prometheus";

    @Value("${common.consul.address:10.10.129.240}")
    private String consulAddress;

    @Value("${common.consul.server.name:${spring.application.name}}")
    private String serverName;

    @Value("${common.consul.server.port:${common.server.tomcat.port}}")
    private int port;

    @Value("${common.consul.report.interval:10s}")
    private String reportInterval;

    @Value("${common.server.tomcat.contextPath:${common.server.tomcat.contextPath}}")
    private String contextPath;


    private void registerForPrometheus() {
        try {
            ConsulClient client = new ConsulClient(consulAddress);
            String ip = IpUtil.getLocalHostAddress();
            String http = PROTOCOL_PREFIX + ip + ":" + port;

            NewService newService = new NewService();
            newService.setId(serverName + "_" + ip);
            newService.setName(serverName);
            newService.setPort(port);
            newService.setAddress(ip);

            String sub = contextPath;
            if (sub.equals("/")) {
                sub = API_SUFFIX;
            } else {
                sub += API_SUFFIX;
            }

            http += sub;
            newService.setTags(Lists.newArrayList(sub));
            NewService.Check serviceCheck = new NewService.Check();
            serviceCheck.setHttp(http);
            serviceCheck.setInterval(reportInterval);
            newService.setCheck(serviceCheck);

            client.agentServiceRegister(newService);
        } catch (Exception e) {
            logger.error("注册consul时,计算ip异常");
        }
    }

    @Override
    public void run(String... args) {
        registerForPrometheus();
    }

}
