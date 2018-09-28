package cat.udl.eps.engsoftarch.workingwithjparest.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
public class TagHierarchy {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "definedIn")
    // If defines is not initialized at construction time, if returns status 500 when POST to /tags/<id>/defines
    // If it is initialized, it returns status 204 but does not link Tag to TagCollection
    private Collection<Tag> defines = new ArrayList<>();
}
