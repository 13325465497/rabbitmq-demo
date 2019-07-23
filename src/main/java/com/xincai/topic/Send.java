package com.xincai.topic;

/**
 * @author 菜心
 *订阅模式-topic : 通配符模式
 * Topic通配符 和 direct 路由定向相比 : 原理一样 , 只不过Topic可以给Routing key 绑定通配符:
 *  一般是一个或者多个单词组成 , 多个单词之间以"."分隔 , 如 : item.insert
 *      规则 :
 *          '#' :匹配一个或多个词
 *          '*' :匹配不多不少恰好一个词
 *      举例 :
 *           'audit.#' :能够匹配 'audit.irs.corporate' 或者 'audit.irs'
 *           'audit.*' :只能匹配 'audit.irs'
 */
public class Send {
}
