package com.whocares.musicalapi.config;

import com.whocares.musicalapi.entity.Message;
import com.whocares.musicalapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有消息，如果没有则添加测试数据
        if (messageRepository.count() == 0) {
            // 创建测试消息
            Message message1 = new Message();
            message1.setTitle("欢迎使用消息中心");
            message1.setContent("这是第一条测试消息，用于验证消息中心功能是否正常工作。");
            message1.setCreatedAt(LocalDateTime.now());
            message1.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message1);
            
            Message message2 = new Message();
            message2.setTitle("系统维护通知");
            message2.setContent("系统将于今晚 23:00-24:00 进行维护，届时可能会出现短暂服务中断，敬请谅解。");
            message2.setCreatedAt(LocalDateTime.now().minusDays(1));
            message2.setUpdatedAt(LocalDateTime.now().minusDays(1));
            messageRepository.save(message2);
            
            System.out.println("已添加测试消息数据");
        }
    }
}