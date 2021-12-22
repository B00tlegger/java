package main.model.entityes;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@NoArgsConstructor
@Table(name = "field")
public class Field {
    @Id
    private int id;
    private String name;
    private String selector;
    private float weight;

    public Field(String name, String selector, float weight) {
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }
}
