package com.xincai.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xincai
 * simple 简单队列 一对一
 * send 消息生产者
 */
public class Send {
    /**
     * 消息名称
     */
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取rabbitmq连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        //从连接中获取通道
        Channel channel = connection.createChannel();
        // 声明(创建)队列 , 必须声明队列才能够发送消息 , 我们可以把消息发到队列中
        // 声明一个队列是幂等的 , 只有当它不存在时才能被创建
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //定义消息内容
        String message = "Hello world ! 这里是simple简单队列, 一对一";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("Send发送 : " + message);
        // 关闭连接
        channel.close();
        connection.close();
    }
}
