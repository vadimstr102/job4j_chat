package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not found. Please check the data")
        );
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@RequestBody Room room) {
        try {
            room = roomRepository.save(room);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room name mustn't be empty");
        }
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Room room) {
        if (roomRepository.findById(room.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room with this id is not found");
        }
        try {
            roomRepository.save(room);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room name mustn't be empty");
        }
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
    public ResponseEntity<Room> patch(@RequestBody Room room) throws InvocationTargetException, IllegalAccessException {
        Room patchableRoom = roomRepository.findById(room.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        ObjectPatcher.patch(patchableRoom, room);
        return new ResponseEntity<>(
                roomRepository.save(patchableRoom),
                HttpStatus.OK
        );
    }
}
