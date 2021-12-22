package main.model.entityes;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')")
    private StatusType status;
    @Column(name = "status_time")
    private long statusTime;
    @Column(name = "last_error_txt")
    private String lastErrorTxt;
    private String url;
    @Column(unique = true)
    private String name;

    public Site (StatusType status, long statusTime, String lastErrorTxt, String url, String name) {
        this.status = status;
        this.statusTime = statusTime;
        this.lastErrorTxt = lastErrorTxt;
        this.url = url;
        this.name = name;
    }

    public enum StatusType {
        INDEXING,
        INDEXED,
        FAILED

    }
}
