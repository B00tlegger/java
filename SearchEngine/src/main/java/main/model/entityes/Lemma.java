package main.model.entityes;

import lombok.*;

import javax.persistence.*;


@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "lemma")
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String lemma;
    private int frequency;
    @Column(name = "site_id")
    private int siteId;

    public Lemma(String lemma, int frequency, int siteId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.siteId = siteId;
    }
}
