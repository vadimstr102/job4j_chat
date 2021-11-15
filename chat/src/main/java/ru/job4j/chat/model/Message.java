package ru.job4j.chat.model;

import ru.job4j.chat.controller.Operation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Id must be more than 0", groups = {Operation.OnUpdate.class})
    private int id;

    @NotBlank(message = "Text must be not blank", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String text;
    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JoinColumn(name = "person_id")
    @NotNull(message = "Person must be not null", groups = {Operation.OnCreate.class})
    private Person person;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @NotNull(message = "Room must be not null", groups = {Operation.OnCreate.class})
    private Room room;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
