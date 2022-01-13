package nl.tudelft.sem.TAs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@Table(name = "ta", schema = "taschema")
public class TADetail {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "rating")
    @JsonProperty(value = "rating")
    @Getter
    @Setter
    private int rating;

    @Column(name = "timespent")
    @JsonProperty(value = "timeSpent")
    @Getter @Setter
    private int timeSpent;


}
