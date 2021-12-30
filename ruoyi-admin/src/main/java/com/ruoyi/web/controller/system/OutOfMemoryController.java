package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.entity.SysUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenzhuo
 * @date 2021-12-23
 */
@RequestMapping("/test")
@RestController
public class OutOfMemoryController {


    private List<SysUser> users = new ArrayList<>();


    /**
     * -Xmx32M -Xms32M
     */
    @GetMapping("out/memory")
    public void out(){

        while(true) {
            SysUser user = new SysUser();
            users.add(user);
        }
    }

}
