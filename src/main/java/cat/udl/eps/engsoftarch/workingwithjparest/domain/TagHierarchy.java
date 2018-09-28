package cat.udl.eps.engsoftarch.workingwithjparest.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;

@Entity
@Data
public class TagHierarchy {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "definedIn")
    private Collection<Tag> defines;
}
