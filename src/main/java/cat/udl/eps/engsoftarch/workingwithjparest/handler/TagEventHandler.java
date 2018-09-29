package cat.udl.eps.engsoftarch.workingwithjparest.handler;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.Tag;
import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagHierarchyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RepositoryEventHandler
public class TagEventHandler {

    private final Logger logger = LoggerFactory.getLogger(TagEventHandler.class);

    private final TagHierarchyRepository tagHierarchyRepository;

    public TagEventHandler(TagHierarchyRepository tagHierarchyRepository) {
        this.tagHierarchyRepository = tagHierarchyRepository;
    }

    @HandleAfterCreate
    @HandleAfterSave
    @Transactional
    public void manintainBidirectionalAssociation(Tag tag) {
        TagHierarchy tagHierarchy = tag.getDefinedIn();
        if ( tagHierarchy != null && ! tagHierarchy.getDefines().contains(tag)) {
            tagHierarchy.getDefines().add(tag);
            tagHierarchyRepository.save(tagHierarchy);
        }
    }

    @HandleAfterLinkSave
    @Transactional
    public void manintainBidirectionalAssociation(Tag tag, Object o) {
        TagHierarchy tagHierarchy = tag.getDefinedIn();
        if ( tagHierarchy != null && ! tagHierarchy.getDefines().contains(tag)) {
            tagHierarchy.getDefines().add(tag);
            tagHierarchyRepository.save(tagHierarchy);
        }
    }
}
