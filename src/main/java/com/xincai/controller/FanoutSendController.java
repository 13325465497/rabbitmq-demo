package com.xincai.controller;

import com.xincai.bean.Item;
import com.xincai.spring.MsgProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xincai
 * 订阅模式-Fanout : 广播模式
 */
@RestController
public class FanoutSendController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("fanoutSend")
    public String fanoutSend(){
        System.out.println("进来了 ~~`");
        try {
            MsgProducer msgProducer=new MsgProducer(rabbitTemplate);
            Item item=new Item();
            item.setId(100L);
            item.setItemName("这是一个商品");
            item.setPrice(23.6);
            msgProducer.sendFanoutExchange(item);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return "Success";
    }
}
