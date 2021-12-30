package com.ruoyi.quartz.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.core.domain.WxpusherEntity;
import com.ruoyi.quartz.domain.LotteryResult;
import com.ruoyi.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 定时任务调度测试
 *
 * @author ruoyi
 */
@Component("ryTask")
public class RyTask {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ISysDictDataService dictDataService;

    final String SIGN_URL = "https://api.juejin.cn/growth_api/v1/check_in";
    final String LOTTERY_URL = "https://api.juejin.cn/growth_api/v1/lottery/draw";
    final String WX_PUSHER = "http://wxpusher.zjiecode.com/api/send/message";

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams() {
        System.out.println("执行无参方法");
    }

    public void sign() {
        String cookie = dictDataService.selectDictLabel("jujin", "cookie");
        String result = HttpRequest.post(SIGN_URL)
                //头信息，多个头信息多次调用此方法即可
                .header(Header.COOKIE, cookie)
                //超时，毫秒
                .timeout(3000)
                .execute().body();
        System.out.println("签到结果:"  + result);
    }

    /**
     * 抽奖一次
     */
    public void freeLottery() {
        String result = lottery();
        sendWxMessage("【 "+ DateTime.now() +"】" + " 掘金抽奖结果为:" + result);
    }

    /**
     * 抽奖
     * @return 奖品名称
     */
    private String lottery() {
        String cookie = dictDataService.selectDictLabel("jujin", "cookie");
        String result = HttpRequest.post(LOTTERY_URL)
                //头信息，多个头信息多次调用此方法即可
                .header(Header.COOKIE, cookie)
                //超时，毫秒
                .timeout(3000)
                .execute().body();
        JSONObject jsonObject = JSON.parseObject(result);
        String data = jsonObject.getString("data");
        JSONObject lotteryName = JSON.parseObject(data);
        return lotteryName.getString("lottery_name");
    }

    /**
     * 批量抽奖，并将结果发送到微信
     * @param num 抽奖次数
     */
    public String batchLottery(Integer num) {
        LotteryResult lotteryResult = new LotteryResult();
        for (int i = 0; i < num; i++) {
            lotteryResult.add(lottery());
        }
        String result = lotteryResult.printLottery().toString();
        sendWxMessage(result);
        return result;
    }

    public void sendMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        // 发件人
        message.setFrom("865531828@qq.com");
        // 收件人
        message.setTo("759761301@qq.com");
        // 邮件标题
        message.setSubject("资源");
        // 邮件内容
        message.setText("性感耀武，在线发牌");
        mailSender.send(message);
    }

    public void sendMails(Integer num) {
        for (int i = 0; i < num; i++) {
            System.out.println("发送第" + i + "封邮件");
            sendMail();
        }
    }

    /**
     * 通过wxpusher向微信推送消息
     * @param message 消息主体
     * @return 发送结果
     */
    public String sendWxMessage(String message) {
        WxpusherEntity entity = new WxpusherEntity();
        entity.setAppToken("AT_5dgGQFGyIPZr0205yrEjEZKWWw2eQWN3");
        entity.setUid("UID_Oz5YySOPfSbwCAJusIIE39nCOThs");
        entity.setContent(message);
        entity.setUrl("http://wxpusher.zjiecode.com");

        entity.setSummary(message);
        entity.setContentType(1L);

        Map<String, Object> paramMap = BeanUtil.beanToMap(entity);
        String result = HttpUtil.get(WX_PUSHER, paramMap);
        System.out.println(result);
        return result;
    }

}
