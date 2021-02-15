package com.polinakulyk.cashregister.db.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import static com.polinakulyk.cashregister.db.entity.User.*;

@Entity
@Table(name = TABLE_USERS)
public class User {

    static final String TABLE_USERS = "users";

    @Id
    private String id;

    @Column(unique = true)
    private String username;

    private String password;
    private String role;
    private String fullName;

    @OneToMany(mappedBy = "user")
    private List<Receipt> receipts = new ArrayList<>();

    @ManyToOne
    private Cashbox cashbox;

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String login) {
        this.username = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public User setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
        return this;
    }

    public Cashbox getCashbox() {
        return cashbox;
    }

    public User setCashbox(Cashbox cashbox) {
        this.cashbox = cashbox;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("role='" + role + "'")
                .add("fullName='" + fullName + "'")
                .add("receipts=" + receipts)
                .add("cashbox.id=" + (cashbox != null ? cashbox.getId() : null))
                .toString();
    }
}
