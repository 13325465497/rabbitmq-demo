package com.xincai.spring;

import com.rabbitmq.client.Channel;
import com.xincai.bean.Item;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author xincai
 * 消息消费者可配置多个
 * 只需要配置注解指定queue名称即可, 因为其他都已经封装在配置了 , bean , 如队列A 绑0定 交换机 绑定 routingKey
 * 接受消息参数为object , 需要实现序列化
 */
@Component
public class Listener {
    /**
     * 手动确认消息，假如不确认的话，消息一直会存在在队列当中，下次消费的时候，就会出现重复消费
     *
     * @param 消息内容
     * @param channel 队列
     * @param message 消息整体
     */
    /*@RabbitListener(queues = RabbitmqConfig.fanout_exchange_queue_1)
    @RabbitHandler
    public void fanoutQueue1Receiver(Item item, Channel channel, Message message) {
        try {
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            System.out.println("接收处理队列A当中的消息：" + item);
            int a = 1 / 0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            //丢弃这条消息
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            e.printStackTrace();
        }
    }*/

    /**
     * simple - 简单队列 , 多个接受就是work 工作队列 [一个消息只能被消费一次] , 默认实现能者多劳 , 而不是均分
     */
    @RabbitListener(queues = RabbitmqConfig.simple_queue)
    @RabbitHandler//获取消息头内容注解
    public void simpleQueue1Receiver(Long id) throws InterruptedException {
        System.out.println("我是simple_queue 消费者 1 ---- :  " + id);
        Thread.sleep(1000);
    }

    @RabbitListener(queues = RabbitmqConfig.simple_queue)
    @RabbitHandler//获取消息头内容注解
    public void simpleQueue2Receiver(Long id) throws InterruptedException {
        System.out.println("我是simple_queue 消费者 2 ---- :  " + id);
        Thread.sleep(3000);
    }

    /**
     * 发布订阅 - 广播室 模拟两个队列正在监听
     *
     * @param item 接受消息对象
     */
    @RabbitListener(queues = RabbitmqConfig.fanout_exchange_queue_1)
    @RabbitHandler//获取消息头内容注解
    public void fanoutQueue1Receiver(Item item, Channel channel, Message message) throws IOException {
        try {
            System.out.println("fanoutRecv1 接受消息 : " + item + " 我是发布订阅广播消费者 2  ");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            System.out.println("fanoutRecv1 消费消息异常 : 准备放入死信");
        }
    }

    @RabbitListener(queues = RabbitmqConfig.fanout_exchange_queue_2)
    @RabbitHandler//获取消息头内容注解
    public void fanoutQueue2Receiver(Item item, Channel channel, Message message) throws IOException {
        try {
            System.out.println("fanoutRecv2 接受消息 : " + item + " 我是发布订阅广播消费者 2  ");
            int a = 1 / 0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            System.out.println("fanoutRecv2 消费消息异常 : 准备放入死信");
        }
    }

    /**
     * 发布订阅 - 路由定向 模拟两个队列正在监听
     *
     * @param item 接受消息对象
     */
    @RabbitListener(queues = RabbitmqConfig.topic_exchange_queue_1)
    @RabbitHandler//获取消息头内容注解
    public void TopicQueue1Receiver(Item item, Channel channel, Message message) throws IOException {
        try {
            System.out.println("topic_exchange_queue_1 接受消息 : " + item + " 好像是个商品");

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            System.out.println("topic_exchange_queue_1 消费消息异常 : 准备放入死信");
        }
    }

    @RabbitListener(queues = RabbitmqConfig.topic_exchange_queue_2)
    @RabbitHandler//获取消息头内容注解
    public void TopicQueue2Receiver(Item item, Channel channel, Message message) throws IOException {
        try {
            System.out.println("topic_exchange_queue_2 接受消息 : " + item + " 好像是个商品");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            System.out.println("topic_exchange_queue_2 消费消息异常 : 准备放入死信");
        }
    }

    @RabbitListener(queues = RabbitmqConfig.deadQueueName)
    @RabbitHandler
    public void DirectDeadQueue(Object msg, Channel channel, Message message) {
        System.out.println("死信dead queue 接受消息 : " + msg);
        try {
            //手动回滚消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
