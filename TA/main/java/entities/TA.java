package entities;
import javax.persistence.*;

@Entity
@Table(name = "TA")
public class TA {
    @Id
    @GeneratedValue(generator = "UUID")
    private Long id;
}
