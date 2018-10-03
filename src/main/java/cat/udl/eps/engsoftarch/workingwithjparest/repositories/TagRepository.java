package cat.udl.eps.engsoftarch.workingwithjparest.repositories;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.Tag;
import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface TagRepository extends CrudRepository<Tag, Integer> {
    List<Tag> findByTagHierarchy(@Param("tagHierarchy") TagHierarchy tagHierarchy);
}
