package cat.udl.eps.engsoftarch.workingwithjparest.repositories;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TagHierarchyRepository extends CrudRepository<TagHierarchy, Integer> {
}
