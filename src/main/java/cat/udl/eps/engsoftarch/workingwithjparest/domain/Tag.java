package cat.udl.eps.engsoftarch.workingwithjparest.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Tag {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @ManyToOne
    private TagHierarchy definedIn;
}
