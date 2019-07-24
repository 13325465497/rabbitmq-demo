package com.xincai.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.xincai.utils.RabbitMqConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 菜心
 * 订阅模式-topic : 通配符模式
 * Topic通配符 和 direct 路由定向相比 : 原理一样 , 只不过Topic可以给Routing key 绑定通配符:
 * 一般是一个或者多个单词组成 , 多个单词之间以"."分隔 , 如 : item.insert
 * 规则 :
 * '#' :匹配一个或多个词
 * '*' :匹配不多不少恰好一个词
 * 举例 :
 * 'audit.#' :能够匹配 'audit.irs.corporate' 或者 'audit.irs'
 * 'audit.*' :只能匹配 'audit.irs'
 */
public class Send {
    /**
     * 交换机名称
     */
    private final static String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取到连接
        Connection connection = RabbitMqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        //声明exchange交换机,指定类型为topic通配符 , 第三个参数为true , 表示交换机持久化
        channel.exchangeDeclare(EXCHANGE_NAME, "topic",true);
        //定义消息内容
        String message = "更新商品 , id=1001";
        //发送消息 , 并指定routing key 为 :insert ,代表新增商品
        channel.basicPublish(EXCHANGE_NAME, "item.update", null, message.getBytes());
        System.out.println("发送消息 :[发布订阅,通配符]" + message);
        //释放资源
        channel.close();
        connection.close();
    }
}
