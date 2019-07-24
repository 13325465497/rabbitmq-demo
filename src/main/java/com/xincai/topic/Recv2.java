package com.xincai.topic;

import com.rabbitmq.client.*;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xincai
 * 消费者2 只接收两种类型的消息：新增
 */
public class Recv2 {
    private final static String QUEUE_NAME = "topic_exchange_queue_2";
    private final static String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取到连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 绑定队列到交换机 , 同时指定需要订阅的routing key , 使用通配符
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "item.*");
        // 定义队列的消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息 ,  并且处理 , 这个方法类似事件监听 ,如果有消息的时候就会被调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //body 消息体
                String message = new String(body);
                System.out.println("[消费者 2] 消费" + message);
            }
        };
        // 监听队列 , 字段ACK
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }
}
