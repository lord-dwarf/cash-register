package com.polinakulyk.cashregister.db.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cashbox {
    @Id
    private String id;

    @Column(unique = true)
    private String name;

    private String shiftStatus;
    private LocalDateTime shiftStatusTime;

    @OneToMany(mappedBy = "cashbox")
    private List<User> users = new ArrayList<>();

    public String getId() {
        return id;
    }

    public Cashbox setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Cashbox setName(String name) {
        this.name = name;
        return this;
    }

    public String getShiftStatus() {
        return shiftStatus;
    }

    public Cashbox setShiftStatus(String status) {
        this.shiftStatus = status;
        return this;
    }

    public LocalDateTime getShiftStatusTime() {
        return shiftStatusTime;
    }

    public Cashbox setShiftStatusTime(LocalDateTime shiftStatusTime) {
        this.shiftStatusTime = shiftStatusTime;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Cashbox setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cashbox cashbox = (Cashbox) o;

        return id.equals(cashbox.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", Cashbox.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("shiftStatus='" + shiftStatus + "'")
                .add("shiftStatusTime='" + shiftStatusTime + "'")
                .add("users=" + users)
                .toString();
    }
}
