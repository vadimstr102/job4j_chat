package ru.job4j.chat.model;

import ru.job4j.chat.controller.Operation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Id must be more than 0", groups = {Operation.OnUpdate.class})
    private int id;

    @NotBlank(message = "Room name must be not blank", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    @Size(min = 3, message = "Room name must be at least 3 characters", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String name;

    @OneToMany
    @JoinColumn(name = "room_id")
    private List<Message> messages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
