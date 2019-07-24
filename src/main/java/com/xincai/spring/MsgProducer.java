package com.xincai.spring;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author xincai
 * 消息生产者封装类 , + 回滚
 */
@Component
public class MsgProducer implements RabbitTemplate.ConfirmCallback {
    /**
     * 由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入 , 多例
     */
    private RabbitTemplate rabbitTemplate;
    /**
     * 回调查看消息是否被成功消费
     */
    private Boolean ack;

    public Boolean getAck() {
        return ack;
    }

    public void setAck(Boolean ack) {
        this.ack = ack;
    }

    /**
     * 构造方法注入rabbitTemplate
     */
    @Autowired
    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 发布订阅 - 通配符模式消息发送 ,指定消息与 routingKey
     *
     * @param msgObject
     * @param routingKey
     */
    public void sendTopicExchange(Object msgObject, String routingKey) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitmqConfig.topic_exchange, routingKey, msgObject, correlationId);
    }

    /**
     * 发布订阅-广播模式消息发送，这里不设置routing_key,因为设置了也无效，发送端的routing_key写任何字符都会被忽略。
     *
     * @param msgObject
     */
    public void sendFanoutExchange(Object msgObject) {
        //绑定交换机A , 对应Key A ,然后把交换机队列绑定起来 ,
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitmqConfig.fanout_exchange, "", msgObject, correlationId);
    }

    /**
     * @param msgObject
     */
    public void sendSimple(Object msgObject) {
        // CorrelationData  该数据的作用是给每条消息一个唯一的标识
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.simple_queue, msgObject, correlationId);
    }

    /**
     * 回调
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("回调了 !!");
        setAck(ack);
        if (ack) {
            System.out.println("消息成功消费");
        } else {
            System.out.println("消息消费失败 : " + cause);
        }
    }
}
