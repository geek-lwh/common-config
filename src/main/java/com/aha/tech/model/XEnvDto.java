package com.aha.tech.model;


import com.aha.tech.annotation.XEnv;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * 说明:http head 参数 </br>
 * AHA系统自定义evn数据字段,注解名大小写无关,spring底层取值忽略大小写
 *
 * @author luweihong
 * @date 2019年11月28日
 */
public class XEnvDto {

    @XEnv("x-trace-id")
    private String traceId;// 用于追踪同一个请求链，由顶层调用者传入

    @XEnv("x-token")
    private String token;// 安全key

    @XEnv("x-env-utm-source")
    private String utmSource;// 广告来源

    @XEnv("x-env-utm-medium")
    private String utmMedium;// 广告媒介

    @XEnv("x-env-utm-campaign")
    private String utmCampaign;// 广告名称

    @XEnv("x-env-utm-term")
    private String utmTerm;

    @XEnv("x-env-utm-content")
    private String utmContent;

    @XEnv("x-env-user-id")
    private Long userId;// 来源用户

    @XEnv("x-env-pk")
    private String pk;// 来源用户

    @XEnv("x-env-ps")
    private String ps;

    @XEnv("x-env-pd")
    private String pd;// 主从关系标示

    @XEnv("x-env-pp")
    private String pp;// 上级用户信息

    @XEnv("x-env-app-type")
    private Integer appType;// 1公众号,2ahaschool拼团小程序,3app,4小恐龙,5严选

    @XEnv("x-env-guniqid")
    private String guniqid;// 唯一用户表示

    @XEnv("x-env-channel")
    private String channel;// 应用市场，值定义：wandoujia,baidu,c360,xiaomi,yingyongbao,huawei,googleplay,oppo,vivo,meizu,guanwang,toutiao,baiduSEM,_test,applestore

    // ============其他http层标准属性==============
    @XEnv("x-forwarded-for")
    private String httpForwardedFor;

    @XEnv("user-agent")
    private String httpUserAgent;

    @XEnv("os")
    private String os;

    @XEnv("version")
    private String version;

    @XEnv("x-equipment")
    private String equipment;//设备类型 值定义:ipad iphone

    public XEnvDto() {
        super();
    }

    public XEnvDto(HttpServletRequest request) {
        this.traceId = request.getHeader("x-trace-root-id");
        this.token = request.getHeader("x-token");
        this.utmSource = request.getHeader("x-env-utm-source");
        this.utmMedium = request.getHeader("x-env-utm-medium");
        this.utmCampaign = request.getHeader("x-env-utm-campaign");
        this.utmTerm = request.getHeader("x-env-utm-term");
        this.utmContent = request.getHeader("x-env-utm-content");
        this.userId = StringUtils.isBlank(request.getHeader("x-env-user-id")) ? null : Long.parseLong(request.getHeader("x-env-user-id"));
        this.pk = request.getHeader("x-env-pk");
        this.ps = request.getHeader("x-env-ps");
        this.pd = request.getHeader("x-env-pd");
        this.pp = request.getHeader("x-env-pp");
        this.appType = StringUtils.isBlank(request.getHeader("x-env-app-type")) ? null : Integer.parseInt(request.getHeader("x-env-app-type"));
        this.guniqid = request.getHeader("x-env-guniqid");
        this.channel = request.getHeader("x-env-channel");
        this.httpForwardedFor = request.getHeader("x-forwarded-for");
        this.httpUserAgent = request.getHeader("user-agent");
        this.os = request.getHeader("os");
        this.version = request.getHeader("version");
        this.equipment = request.getHeader("x-equipment");
    }


    public String remoteIp() {
        return this.httpForwardedFor;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public void setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getGuniqid() {
        return guniqid;
    }

    public void setGuniqid(String guniqid) {
        this.guniqid = guniqid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getHttpForwardedFor() {
        return httpForwardedFor;
    }

    public void setHttpForwardedFor(String httpForwardedFor) {
        this.httpForwardedFor = httpForwardedFor;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
