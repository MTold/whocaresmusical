package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Message;
import com.whocares.musicalapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }
    
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }
    
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }
    
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}