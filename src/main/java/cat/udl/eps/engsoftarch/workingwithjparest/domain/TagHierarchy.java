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
}
