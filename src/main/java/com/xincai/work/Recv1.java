package com.xincai.work;

import com.rabbitmq.client.*;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 *  工作队列 , 消费者2 , 模拟1秒执行一个消息任务
 */
public class Recv1 {
    /**
     * 消息名称
     */
    private final static String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //设置每个消费者同时只能处理一条消息 , 处理快的多处理:默认平分(如消费1处理1秒),消费2处理3秒, 如发生20条, 还是被平均分,
        //这样就影响性能了 , 消息一1秒处理完需要等消息二2秒处理完,才能继续处理,以此类推
        channel.basicQos(1);
        //定义队列消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息 , 并且处理 , 这个方法类似于事件监听 , 如果有消息 , 会被自动调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //body 消息体
                String message = new String(body);
                //自定义获取消息时异常,如果是手动回滚则会怎么样
                ///int i = 1 / 0;
                System.out.println("[消费者1]消费消息 : " + message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //手动进行ACK回滚 , 如果监听设置手动进行ACK ,这里如果不手动提交的话, 消息队列中消息不会被真正的消费掉
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        //监听队列(一直监听),第二个参数:是否自动进行消息确定
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}
