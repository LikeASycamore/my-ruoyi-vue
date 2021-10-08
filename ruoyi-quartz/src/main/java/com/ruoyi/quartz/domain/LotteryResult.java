package com.ruoyi.quartz.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzhuo
 * @version 1.0.0
 * @description: 抽奖结果
 * @create: 2021-10-08 15:01
 */
public class LotteryResult {

    private Map<String, Integer> lottery = new HashMap<>();
    private StringBuilder sb = new StringBuilder();
    public void add (String lotteryName) {
        lottery.put(lotteryName, lottery.getOrDefault(lotteryName, 0) + 1);
    }

    public StringBuilder printLottery() {
        lottery.forEach( (k,v) -> sb.append(k).append(":").append(v).append("  "));
        return sb;
    }

}
