package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message with this id is not found")
        );
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@Validated(Operation.OnCreate.class) @RequestBody Message message) {
        message = messageRepository.save(message);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Validated(Operation.OnUpdate.class) @RequestBody Message message) {
        if (messageRepository.findById(message.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message with this id is not found");
        }
        messageRepository.save(message);
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
    public ResponseEntity<Message> patch(@Validated(Operation.OnUpdate.class) @RequestBody Message message)
            throws InvocationTargetException, IllegalAccessException {
        Message patchableMessage = messageRepository.findById(message.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message with this id is not found")
        );
        ObjectPatcher.patch(patchableMessage, message);
        return new ResponseEntity<>(
                messageRepository.save(patchableMessage),
                HttpStatus.OK
        );
    }
}
