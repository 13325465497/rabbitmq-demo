package com.xincai.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xincai
 */
public class RabbitMqConnectionUtil {
    public static Connection getConnection(){
        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置服务地址
        factory.setHost("39.105.184.86");
        //端口
        factory.setPort(5672);
        //设置账号信息，用户名、密码、vhost
        factory.setVirtualHost("/demo");
        factory.setUsername("root");
        factory.setPassword("root");
        // 通过工程获取连接
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;

    }
}
