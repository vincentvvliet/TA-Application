package nl.tudelft.sem.TAs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "hours" , schema = "taschema")
public class WorkingHour {
    @Id
    @Column(name = "id" , nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "TAid" , nullable = false)
    @JsonProperty(value = "TAId")
    private UUID TAId;

    @Column(name = "date")
    @JsonProperty(value = "date")
    private LocalDate date;

    @Column(name = "hours")
    @JsonProperty(value = "hours")
    private Integer hours;

    @Column(name = "approved")
    @JsonProperty(value = "approved")
    private Boolean approved;

    public WorkingHour(UUID TAId, LocalDate date, int hours){
        this.id = UUID.randomUUID();
        this.TAId = TAId;
        this.date = date;
        this.hours = hours;
        this.approved = false;
    }

    public WorkingHour() {
        this.id = UUID.randomUUID();
    }
}
