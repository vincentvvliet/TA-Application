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
@Table(name = "notification", schema = "userschema")
public class Notification {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "recipientid")
    @JsonProperty(value = "recipientid")
    private UUID recipientId;

    @Column(name = "message")
    @JsonProperty(value = "message")
    private String message;

    /**
     * Instantiates new Notification.
     */
    public Notification() {
        this.id = UUID.randomUUID();
    }

    /**
     * Instantiates new Notification.
     *
     * @param recipientId id of user recieving notifiaction.
     * @param message message for recipient.
     */
    public Notification(UUID recipientId, String message) {
        this.id = UUID.randomUUID();
        this.recipientId = recipientId;
        this.message = message;
    }
}
