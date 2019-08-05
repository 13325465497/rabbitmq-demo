package com.xincai.spring;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xincai
 * 配置类
 * Broker:它提供一种传输服务,它的角色就是维护一条从生产者到消费者的路线，保证数据能按照指定的方式进行传输,
 * Exchange：消息交换机,它指定消息按什么规则,路由到哪个队列。
 * Queue:消息的载体,每个消息都会被投到一个或多个队列。
 * Binding:绑定，它的作用就是把exchange和queue按照路由规则绑定起来.
 * Routing Key:路由关键字,exchange根据这个关键字进行消息投递。
 * vhost:虚拟主机,一个broker里可以有多个vhost，用作不同用户的权限分离。
 * Producer:消息生产者,就是投递消息的程序.
 * Consumer:消息消费者,就是接受消息的程序.
 * Channel:消息通道,在客户端的每个连接里,可建立多个channel.
 */
@Configuration
public class RabbitmqConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //多例 prototype
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    public static final String simple_queue = "simple_queue";
    /**
     * 定义死信队列相关信息
     */
    public final static String deadQueueName = "dead_queue";
    public final static String deadRoutingKey = "dead_routing_key";
    public final static String deadExchangeName = "dead_exchange";
    /**
     * 死信队列 交换机标识符
     */
    public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";
    /**
     * 死信队列交换机绑定键标识符
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    /**
     * 订阅模式-Fanout : 广播模式
     */
    public static final String fanout_exchange = "fanout_exchange";
    public static final String fanout_exchange_queue_1 = "fanout_exchange_queue_1";
    public static final String fanout_exchange_queue_2 = "fanout_exchange_queue_2";
    /**
     * 订阅模式-topic : 通配符模式
     */
    public static final String topic_exchange = "topic_exchange";
    public static final String topic_exchange_queue_1 = "topic_exchange_queue_1";
    public static final String topic_exchange_queue_1_routingKey = "item.*";
    public static final String topic_exchange_queue_2 = "topic_exchange_queue_2";
    public static final String topic_exchange_queue_2_routingKey = "item.insert";

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange DirectExchange() {
        //定义 发布订阅-路由定向交换机
        return new DirectExchange(deadExchangeName);
    }

    @Bean
    public TopicExchange TopicExchange() {
        //定义 发布订阅-通配符交换机
        return new TopicExchange(topic_exchange);
    }

    @Bean
    public FanoutExchange FanoutExchange() {
        //定义 发布订阅-广播交换机
        return new FanoutExchange(fanout_exchange);
    }

    /**
     * simple简单队列
     *
     * @return
     */
    @Bean
    public Queue simple_queue() {
        return new Queue(simple_queue, true);
    }

    /**
     * 定义队列 , topic_exchange_queue_1 通配符
     * 定义队列 , topic_exchange_queue_1 通配符
     *
     * @return
     */
    @Bean
    public Queue topic_exchange_queue_1() {
        return new Queue(topic_exchange_queue_1, true);
    }

    @Bean
    public Queue topic_exchange_queue_2() {
        return new Queue(topic_exchange_queue_2, true);
    }

    /**
     * 绑定发布订阅-通配符 交换机和通配符队列 , 并且指定 routing key
     *
     * @return
     */
    @Bean
    public Binding bindingTopicExchange1() {
        //绑定 topic_exchange_queue_1队列 , 和 TopicExchange 通配符交换机 + key  topic_exchange_queue_1_routingKey
        return BindingBuilder.bind(topic_exchange_queue_1()).to(TopicExchange()).with(topic_exchange_queue_1_routingKey);
    }

    @Bean
    public Binding bindingTopicExchange2() {
        //绑定 topic_exchange_queue_2队列 , 和 TopicExchange 通配符交换机 + key  topic_exchange_queue_2_routingKey
        return BindingBuilder.bind(topic_exchange_queue_2()).to(TopicExchange()).with(topic_exchange_queue_2_routingKey);
    }

    /**
     * 获取队列广播式 获取队列广播室fanout_exchange_queue_1 1
     *
     * @return
     */
    @Bean
    public Queue fanout_exchange_queue_1() {
        // 将普通队列绑定到死信队列交换机上
        Map<String, Object> args = new HashMap<>(2);
        args.put(DEAD_LETTER_QUEUE_KEY, deadExchangeName);
        args.put(DEAD_LETTER_ROUTING_KEY, deadRoutingKey);
        return new Queue(fanout_exchange_queue_1, true, false, false, args); //队列持久
    }

    @Bean
    public Queue fanout_exchange_queue_2() {
        // 将普通队列绑定到死信队列交换机上
        Map<String, Object> args = new HashMap<>(2);
        args.put(DEAD_LETTER_QUEUE_KEY, deadExchangeName);
        args.put(DEAD_LETTER_ROUTING_KEY, deadRoutingKey);
        return new Queue(fanout_exchange_queue_2, true, false, false, args); //队列持久
    }

    /**
     * 绑定队列广播1 和广播交换机
     * 绑定队列广播2 和广播交换机
     *
     * @return
     */
    @Bean
    public Binding bindingFanoutExchange1(Queue fanout_exchange_queue_1, FanoutExchange fanoutExchange) {
        //绑定 fanout_exchange_queue_1队列 , 和 FanoutExchange广播交换机
        return BindingBuilder.bind(fanout_exchange_queue_1).to(fanoutExchange);
    }

    @Bean
    public Binding bindingFanoutExchange2(Queue fanout_exchange_queue_2, FanoutExchange fanoutExchange) {
        //绑定 fanout_exchange_queue_2队列 , 和 FanoutExchange广播交换机
        return BindingBuilder.bind(fanout_exchange_queue_2).to(fanoutExchange);
    }

    /**
     * 定义死信队列 , 绑定死信交换机, 绑定 , 如绑定死信交换机的消息queue , 如符合死信要求则会执行
     */
    @Bean
    public Queue direct_exchange_dead_queue() {
        return new Queue(deadQueueName, true);
    }

    @Bean
    public Binding bindingDirectExchangeDead() {
        return BindingBuilder.bind(direct_exchange_dead_queue()).to(DirectExchange()).with(deadRoutingKey);
    }

}
