package com.xincai.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author xincai
 * simple 工作队列 [轮询,公平]  一对多, 一个消息任务只能被一个消费者消费
 * send 消息生产发送消息
 */
public class Send {
    /**
     * 消息名称
     */
    private final static String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取rabbitmq连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        //从连接中获取通道
        Channel channel = connection.createChannel();
        // 声明(创建)队列 , 必须声明队列才能够发送消息 , 我们可以把消息发到队列中
        // 声明一个队列是幂等的 , 只有当它不存在时才能被创建 第二个参数为true 表示队列持久化
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //定义消息内容
        for (int i = 0; i < 20; i++) {
            String message = "Hello world ! 这里是work工作队列 : [轮询,公平] 一个消息任务只能被一个消费者消费 ,消息 :" + i + "条";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("Send" + i + "发送 : " + message);

            try {
                //每发一次消息亭一下
                Thread.sleep(i * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 关闭连接
        channel.close();
        connection.close();
    }
}
