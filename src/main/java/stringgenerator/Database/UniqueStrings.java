package stringgenerator.Database;


import javax.persistence.*;
import java.sql.Blob;

@Entity
public class UniqueStrings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String params;
    private Blob file;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }
}
