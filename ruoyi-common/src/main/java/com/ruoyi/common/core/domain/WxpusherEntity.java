package com.ruoyi.common.core.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenzhuo
 * @version 1.0.0
 * @description: 微信消息推送实体类
 * @create: 2021-09-30 16:14
 */
@Configuration
public class WxpusherEntity {

    private String appToken;

    /**
     * 发送消息主题
     */
    private String content;

    /**
     * 消息摘要,限制长度100，可以不传
     */
    private String summary;

    /**
     * 内容类型 1表示文字  2表示html
     */
    private Long contentType;

    /**
     * 发送目标的topicId，是一个数组
     */
    private String[] topicIds;

    /**
     * 发送目标的UID
     * 注意uids和topicIds可以同时填写，也可以只填写一个
     */
    private String uid;

    private String url;

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getContentType() {
        return contentType;
    }

    public void setContentType(Long contentType) {
        this.contentType = contentType;
    }

    public String[] getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(String[] topicIds) {
        this.topicIds = topicIds;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WxpusherEntity() {
    }
}
