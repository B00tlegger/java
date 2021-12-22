package main.model.entityes;

import lombok.*;

import javax.persistence.*;
@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "page")
public class Page{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "text")
    private String path;
    private Integer code;
    @Column(columnDefinition = "mediumtext")
    private String content;
    @Column(name = "site_id")
    private int siteId;

    public Page(String path, int code, String content) {
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public Page(String path, int code, String content, int siteId) {
        this.path = path;
        this.code = code;
        this.content = content;
        this.siteId = siteId;
    }


}
