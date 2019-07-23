package com.xincai.simple;

import com.rabbitmq.client.*;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 * simple 简单队列 一对一
 * recv 消费者获取消息
 */
public class Recv {
    /**
     * 消息名称
     */
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //定义队列消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息 , 并且处理 , 这个方法类似于事件监听 , 如果有消息 , 会被自动调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //body 消息体
                String message = new String(body);
                //自定义获取消息时异常,如果是手动回滚则会怎么样
                int i = 1 / 0;
                System.out.println("获取消息 : " + message);
                //手动进行ACK回滚 , 如果监听设置手动进行ACK ,这里如果不手动提交的话, 消息队列中消息不会被真正的消费掉
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        //监听队列(一直监听),第二个参数:是否自动进行消息确定
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}
