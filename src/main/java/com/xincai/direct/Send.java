package com.xincai.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 * 订阅模式-direct : 路由定向模式
 * 在Direct模型下，队列与交换机的绑定，不能是任意绑定了，而是要指定一个RoutingKey（路由key）
 * 消息的发送方在向Exchange发送消息时，也必须指定消息的routing key
 * P：生产者，向Exchange发送消息，发送消息时，会指定一个routing key。
 * X：Exchange（交换机），接收生产者的消息，然后把消息递交给 与routing key完全匹配的队列
 * C1：消费者，其所在队列指定了需要routing key 为 error 的消息
 * C2：消费者，其所在队列指定了需要routing key 为 info、error、warning 的消息
 */
public class Send {
    //此处我们模拟商品的增删改，发送消息的RoutingKey分别是：insert、update、delete
    /**
     * 交换机名称
     */
    private final static String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取到连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        // 声明exchange交换机，指定类型为direct路由定向模式 第三个参数为true , 表示交换机持久化
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        // 消息内容
        String message = "商品新增了， id = 1001";
        // 发送消息，并且指定routing key 为：insert ,代表新增商品 , 第三个参数为null , 表示消息不持久话 , 为 MessageProperties.PERSISTENT_TEXT_PLAIN 表示持久化
        channel.basicPublish(EXCHANGE_NAME, "update", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println(" [商品服务：] 发送消息 '" + message + "'");
        //释放资源
        channel.close();
        connection.close();
    }
}
