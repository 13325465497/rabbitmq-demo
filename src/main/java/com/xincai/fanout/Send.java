package com.xincai.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 * 订阅模式-Fanout : 广播模式
 * 在广播模式下，消息发送流程是这样的：
 * - 1）  可以有多个消费者
 * - 2）  每个**消费者有自己的queue**（队列）
 * - 3）  每个**队列都要绑定到Exchange**（交换机）
 * - 4）  **生产者发送的消息，只能发送到交换机**，交换机来决定要发给哪个队列，生产者无法决定。
 * - 5）  交换机把消息发送给绑定过的所有队列
 * - 6）  队列的消费者都能拿到消息。实现一条消息被多个消费者消费
 * 两个变化：
 * - 1）  声明Exchange，不再声明Queue
 * - 2）  发送消息到Exchange，不再发送到Queue
 */
public class Send {
    /**
     * 消息名称, 订阅-广播式
     */
    private final static String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取到连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        //声明Exchange交换机 , 指定类型为fanout 广播式 , 注:这里运用了工厂设计模式 ,根据类型去匹配对应的模式
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 消息内容
        String message = "Hello everyone, 这里是发布订阅, 经过转换机, 广播模式";
        // 发布消息到Exchange
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        System.out.println(" [生产者] 发送消息任务 '" + message + "'");
        //释放资源
        channel.close();
        connection.close();
    }
}
