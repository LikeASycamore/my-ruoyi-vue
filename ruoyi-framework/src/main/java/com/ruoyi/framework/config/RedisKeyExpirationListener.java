package com.ruoyi.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author chenzhuo
 * @version 1.0.0
 * @description: redis过期监听
 * @create: 2021-10-19 16:55
 */
@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    /**
     * 针对redis数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String key = message.toString();
            //从失效key中筛选代表订单失效的key
            if (key != null && key.startsWith("order_")) {
                String orderNo = key.substring(6);
                // mock 更新订单状态
                log.info("订单号为【" + orderNo + "】超时未支付-自动修改为已取消状态");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【修改支付订单过期状态异常】：" + e.getMessage());
        }
    }
}
