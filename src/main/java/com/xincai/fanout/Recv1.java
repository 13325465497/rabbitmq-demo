package com.xincai.fanout;

import com.rabbitmq.client.*;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 * 订阅模式-Fanout : 广播模式
 * 消费者 1
 */
public class Recv1 {
    /**
     * 交换机名称, 订阅-广播式
     */
    private final static String EXCHANGE_NAME = "fanout_exchange";
    /**
     * 消息名称 , 订阅-广播式
     */
    private final static String QUEUE_NAME = "fanout_exchange_queue_1";


    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取到连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //***绑定队列到交换机***
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        //定义队列的消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息，并且处理，这个方法类似事件监听，如果有消息的时候，会被自动调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // body 即消息体
                String msg = new String(body);
                System.out.println(" [消费者1] 消费 : " + msg + "!");
            }
        };
        //监听队列 , 自动返回完成
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
