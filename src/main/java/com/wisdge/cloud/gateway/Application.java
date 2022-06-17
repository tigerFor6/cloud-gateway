package com.wisdge.cloud.gateway;

import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.ResultCode;
import com.wisdge.cloud.gateway.internal.AuthConstant;
import com.wisdge.cloud.gateway.mapper.SysMapper;
import com.wisdge.commons.redis.RedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.wisdge.cloud"})
public class Application implements CommandLineRunner, DisposableBean {
    private final static CountDownLatch latch = new CountDownLatch(1);

    @Value("${cloud.app-name:cloud.gateway}")
    private String appName;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private SysMapper sysMapper;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        List<Map<String, String>> records = sysMapper.resRoles();
        for (Map<String, String> record : records) {
            redisTemplate.opsForHash().put(AuthConstant.RESOURCE_ROLES_KEY, record.get("RES_CONTENT"), record.get("ROLE_IDS"));
        }
        log.info("{} ------>> 启动成功", appName);
    }

    @Override
    public void destroy() {
        latch.countDown();
        log.info("{} ------>> 关闭成功", appName);
    }

    @GetMapping("fallback")
    @ResponseBody
    public ApiResult fallback() {
        return new ApiResult(ResultCode.SERVICE_UNAVAILABLE);
    }

}
