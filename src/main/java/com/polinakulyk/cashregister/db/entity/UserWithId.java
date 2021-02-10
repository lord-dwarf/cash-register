package com.polinakulyk.cashregister.db.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static com.polinakulyk.cashregister.db.entity.User.*;

@Entity
@Table(name = TABLE_USERS)
public class UserWithId {

    @Id
    private String id;
    private String username;
    private String password;
    private String role;
    @OneToMany(mappedBy = "user")
    private List<Receipt> receipts = new ArrayList<>();

    public String getId() {
        return id;
    }

    public UserWithId setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserWithId setUsername(String login) {
        this.username = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserWithId setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() {
        return role;
    }

    public UserWithId setRole(String role) {
        this.role = role;
        return this;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public UserWithId setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithId user = (UserWithId) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserWithId.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("role='" + role + "'")
                .add("receipts=" + receipts)
                .toString();
    }
}
