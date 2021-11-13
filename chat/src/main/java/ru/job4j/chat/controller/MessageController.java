package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.repository.MessageRepository;
import ru.job4j.chat.service.ObjectPatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Message>> findAll() {
        return new ResponseEntity<>(
                StreamSupport.stream(messageRepository.findAll().spliterator(), false).collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = messageRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message is not found. Please check the data")
        );
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        try {
            message = messageRepository.save(message);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text, person and room mustn't be empty");
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        if (messageRepository.findById(message.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message with this id is not found");
        }
        try {
            messageRepository.save(message);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text, person and room mustn't be empty");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = new Message();
        message.setId(id);
        messageRepository.delete(message);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Message> patch(@RequestBody Message message) throws InvocationTargetException, IllegalAccessException {
        Message patchableMessage = messageRepository.findById(message.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        ObjectPatcher.patch(patchableMessage, message);
        return new ResponseEntity<>(
                messageRepository.save(patchableMessage),
                HttpStatus.OK
        );
    }
}
