package com.mod.loan.config.init;

/**
 * 缓存初始化数据
 * @Author: whw
 * @Date: 2019/6/4/004 15:26
 */
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.OrderUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * 初始化数据
 * @author: whw
 */
@Component
@Slf4j
public class InitData implements CommandLineRunner {

    @Resource
    private RedisMapper redisMapper;

    @Resource
    private OrderUserMapper orderUserMapper;

    @Override
    public void run(String... strings) throws Exception {
        List<OrderUser> list= orderUserMapper.getList();
        list.forEach(action->{
            redisMapper.set(action.getOrderNo()+action.getSource(),action.getUid());
        });
    }
}
