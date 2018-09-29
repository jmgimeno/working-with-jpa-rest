package cat.udl.eps.engsoftarch.workingwithjparest.handler;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.Tag;
import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagHierarchyRepository;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RepositoryEventHandler
public class TagHierarchyEventHandler {

    private final Logger logger = LoggerFactory.getLogger(TagHierarchyEventHandler.class);

    private final TagRepository tagRepository;
    private final TagHierarchyRepository tagHierarchyRepository;

    public TagHierarchyEventHandler(TagRepository tagRepository, TagHierarchyRepository tagHierarchyRepository) {
        this.tagRepository = tagRepository;
        this.tagHierarchyRepository = tagHierarchyRepository;
    }

    @HandleAfterLinkSave // org.springframework.data.rest.webmvc.RepositoryPropertyReferenceController
    @Transactional
    public void manintainBidirectionalAssociation(TagHierarchy tagHierarchy, Object o) {
        for (Tag tag : tagHierarchy.getDefines()) {
            TagHierarchy oldTagHierarchy = tag.getDefinedIn();
            if ( oldTagHierarchy != tagHierarchy ) {
                if (oldTagHierarchy != null) {
                    oldTagHierarchy.getDefines().remove(tag);
                    tagHierarchyRepository.save(oldTagHierarchy);
                }
                tag.setDefinedIn(tagHierarchy);
                tagRepository.save(tag);
            }
        }
    }
}
