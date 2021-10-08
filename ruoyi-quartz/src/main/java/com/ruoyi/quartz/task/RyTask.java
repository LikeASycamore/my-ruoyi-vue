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

    final String SIGN_URL = "https://api.juejin.cn/growth_api/v1/check_in";
    final String LOTTERY_URL = "https://api.juejin.cn/growth_api/v1/lottery/draw";
    final String WX_PUSHER = "http://wxpusher.zjiecode.com/api/send/message";
    final String COOKIE = "_ga=GA1.2.1397669210.1621576261; n_mh=Yo4egwfD1gF9yHg-RDVfM0fCtRvD4xOH9aYx9Td2DX4; odin_tt=d3f95992cad82d0bcf5762f8fca942b95b2660a46b5811103f6ab41d77702abb491308ba7dae4b31ae3ca06f17acef255cbaca4bf10ac75406c7add43bed8296; MONITOR_WEB_ID=c1740511-06e7-46bc-ab3a-113648140e23; passport_csrf_token_default=0dfcee686f8ee3901160b0c1e61c9d99; passport_csrf_token=0dfcee686f8ee3901160b0c1e61c9d99; passport_auth_status=996c24fe968351d4c70c62847ab3c37c%2C; passport_auth_status_ss=996c24fe968351d4c70c62847ab3c37c%2C; sid_guard=ff17bf66e0f42b5efe15e753d5050249%7C1631582354%7C5184000%7CSat%2C+13-Nov-2021+01%3A19%3A14+GMT; uid_tt=0acc219fbaf38a391cd4f3ab236656da; uid_tt_ss=0acc219fbaf38a391cd4f3ab236656da; sid_tt=ff17bf66e0f42b5efe15e753d5050249; sessionid=ff17bf66e0f42b5efe15e753d5050249; sessionid_ss=ff17bf66e0f42b5efe15e753d5050249; sid_ucp_v1=1.0.0-KGNkM2I2YzZhZjU2NDVjYmVlOTI4ZjE3ZTdjMWJiNTE5MmU0NGI4NzIKFwiHssD_743sARCS8f-JBhiwFDgCQPEHGgJsZiIgZmYxN2JmNjZlMGY0MmI1ZWZlMTVlNzUzZDUwNTAyNDk; ssid_ucp_v1=1.0.0-KGNkM2I2YzZhZjU2NDVjYmVlOTI4ZjE3ZTdjMWJiNTE5MmU0NGI4NzIKFwiHssD_743sARCS8f-JBhiwFDgCQPEHGgJsZiIgZmYxN2JmNjZlMGY0MmI1ZWZlMTVlNzUzZDUwNTAyNDk; _gid=GA1.2.2076561506.1632617718";

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
        String result = HttpRequest.post(SIGN_URL)
                //头信息，多个头信息多次调用此方法即可
                .header(Header.COOKIE, COOKIE)
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
        String result = HttpRequest.post(LOTTERY_URL)
                //头信息，多个头信息多次调用此方法即可
                .header(Header.COOKIE, COOKIE)
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

        entity.setSummary("掘金抽奖");
        entity.setContentType(1L);

        Map<String, Object> paramMap = BeanUtil.beanToMap(entity);
        String result = HttpUtil.get(WX_PUSHER, paramMap);
        System.out.println(result);
        return result;
    }

}
