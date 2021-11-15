package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.RoomRepository;
import ru.job4j.chat.service.ObjectPatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Room>> findAll() {
        return new ResponseEntity<>(
                StreamSupport.stream(roomRepository.findAll().spliterator(), false).collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        var room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with this id is not found")
        );
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@Validated(Operation.OnCreate.class) @RequestBody Room room) {
        room = roomRepository.save(room);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Validated(Operation.OnUpdate.class) @RequestBody Room room) {
        if (roomRepository.findById(room.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with this id is not found");
        }
        roomRepository.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Room room = new Room();
        room.setId(id);
        roomRepository.delete(room);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Room> patch(@Validated(Operation.OnUpdate.class) @RequestBody Room room)
            throws InvocationTargetException, IllegalAccessException {
        Room patchableRoom = roomRepository.findById(room.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with this id is not found")
        );
        ObjectPatcher.patch(patchableRoom, room);
        return new ResponseEntity<>(
                roomRepository.save(patchableRoom),
                HttpStatus.OK
        );
    }
}
