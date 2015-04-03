package com.xt.simplemq.bean;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Topic {
    private String topicName;

    public Topic(String topic) {
        topicName = topic;
    }

    public boolean isEqual(String name) {
        return topicName == name;
    }

    public String getTopicName() {
        return topicName;
    }
}
