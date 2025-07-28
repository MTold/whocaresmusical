package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.entity.Message;
import com.whocares.musicalapi.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {
    
    private static final Logger logger = Logger.getLogger(MessageController.class.getName());
    
    @Autowired
    private MessageService messageService;
    
    // 获取所有消息（按创建时间倒序排列）- 允许所有用户访问
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        logger.info("Received request to get all messages");
        List<Message> messages = messageService.getAllMessages();
        logger.info("Returning " + messages.size() + " messages");
        return ResponseEntity.ok(messages);
    }
    
    // 根据ID获取消息 - 允许所有用户访问
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        logger.info("Received request to get message by id: " + id);
        return messageService.getMessageById(id)
                .map(message -> {
                    logger.info("Found message with id: " + id);
                    return ResponseEntity.ok(message);
                })
                .orElseGet(() -> {
                    logger.info("Message not found with id: " + id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    // 创建新消息（仅管理员）
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        logger.info("Received request to create message: " + message.getTitle());
        Message savedMessage = messageService.createMessage(message);
        logger.info("Created message with id: " + savedMessage.getMessageId());
        return ResponseEntity.ok(savedMessage);
    }
    
    // 更新消息（仅管理员）
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message message) {
        logger.info("Received request to update message with id: " + id);
        message.setMessageId(id);
        Message updatedMessage = messageService.updateMessage(message);
        logger.info("Updated message with id: " + updatedMessage.getMessageId());
        return ResponseEntity.ok(updatedMessage);
    }
    
    // 删除消息（仅管理员）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        logger.info("Received request to delete message with id: " + id);
        messageService.deleteMessage(id);
        logger.info("Deleted message with id: " + id);
        return ResponseEntity.ok().build();
    }
}