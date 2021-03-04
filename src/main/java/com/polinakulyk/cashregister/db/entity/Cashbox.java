package com.polinakulyk.cashregister.db.entity;

import com.polinakulyk.cashregister.db.dto.ShiftStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Cashbox {
    @Id
    private String id;

    @Column(unique = true)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Shift status cannot be null")
    private ShiftStatus shiftStatus;

    @NotNull(message = "Shift status time cannot be null")
    private LocalDateTime shiftStatusTime;

    @OneToMany(mappedBy = "cashbox", fetch = LAZY)
    @NotNull(message = "Users cannot be null")
    private Set<User> users = new HashSet<>();

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

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public Cashbox setShiftStatus(ShiftStatus status) {
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

    public Set<User> getUsers() {
        return users;
    }

    public Cashbox setUsers(Set<User> users) {
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
