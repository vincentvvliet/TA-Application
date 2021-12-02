package nl.tudelft.sem.TAs.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contract" , schema = "taschema")
public class Contract {
    @Id
    @Column(name = "id" , nullable = false)
    private final UUID id;

    public Contract(){
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}
