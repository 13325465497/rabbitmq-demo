import com.RabbitMQApplication;
import com.xincai.bean.Item;
import com.xincai.spring.MsgProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试代码
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RabbitMQApplication.class)
public class SendTest {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息队列广播模式(订阅/发布)
     */
    @Test
    public void sendFanout() throws InterruptedException {
        MsgProducer msgProducer = new MsgProducer(rabbitTemplate);
        Item item = new Item();
        item.setId(100L);
        item.setItemName("这是一个商品");
        item.setPrice(23.6);
        //发现消息广播
        msgProducer.sendFanoutExchange(item);
        Thread.sleep(10000);
        //rabbitTemplate.convertAndSend("simple_queue", "我发送了spring_simple_queue 一对一消息队列");
        //发送路由定向
        //msgProducer.sendTopicExchange(item, "item.insert");

    }

    @Test
    public void testTemplate() {
        String body = "hello,test rabbitTemplage!";
        MessageProperties properties = new MessageProperties();
        properties.setContentEncoding("utf-8");
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setPriority(1);
        properties.setHeader("nihao:", "yes!");
        Message message = new Message(body.getBytes(), properties);
        //        MessagePostProcessor参数是在消息发送过程中动态修改消息属性的类
        rabbitTemplate.convertAndSend("test.direct01", "mq.direct", message, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //                修改属性
                message.getMessageProperties().setHeader("nihao:", "no");
                //                添加属性
                message.getMessageProperties().setHeader("新添加属性：", "添加属性1");
                return message;
            }
        });


        //        发送objcet类型
        rabbitTemplate.convertAndSend("test.topic01", "mq.topic", "send object type message!!!");
        System.out.println("发送完毕！！！");
    }
}
