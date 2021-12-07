package nl.tudelft.sem.User.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "ta", schema = "taschema")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "username")
    @JsonProperty(value = "username")
    private String username;

    @Column(name = "password")
    @JsonProperty(value = "password")
    private String password;

    public User() {
        this.id = UUID.randomUUID();
    }

    public User(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
    }
}
