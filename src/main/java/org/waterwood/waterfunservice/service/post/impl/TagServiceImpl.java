package org.waterwood.waterfunservice.service.post.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.entity.post.Tag;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.infrastructure.exception.business.BusinessException;
import org.waterwood.waterfunservice.infrastructure.persistence.TagRepository;
import org.waterwood.waterfunservice.service.post.TagService;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.waterfunservice.infrastructure.utils.generator.SlugGenerator;
import org.waterwood.waterfunservice.infrastructure.utils.security.JwtHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final SlugGenerator slugGenerator;
    private final UserService userService;

    @Override
    public void createTag(Tag tag) {
        // Generate slug && check
        String slug = slugGenerator.generateSlug(tag.getName(), tagRepository);
        tag.setSlug(slug);
        User u = userService.getUserById(JwtHelper.getCurrentUserId());
        tag.setCreator(u);

        if(tag.getUsageCount() == null) tag.setUsageCount(0L);
        tagRepository.save(tag);
    }

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllByCreatorId(JwtHelper.getCurrentUserId());
    }

    @Override
    public Tag getTag(Long id) {
        return tagRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_FOUND, "Tag ID: " + id)
        );
    }

    @Override
    public void updateTag(Tag tag) {
        Tag t = tagRepository.findById(tag.getId()).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_FOUND, "Tag ID: " + tag.getId())
        );
        if(tag.getName() !=  null){
            t.setName(tag.getName());
            t.setSlug(slugGenerator.generateSlug(tag.getName(), tagRepository));
        }
        if(tag.getDescription() != null) t.setDescription(tag.getDescription());
        tagRepository.save(t);
    }

    @Override
    public void deleteTag(Long id) {
        Tag t = tagRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.HTTP_NOT_FOUND)
        );

        if(! t.getCreator().getId().equals(JwtHelper.getCurrentUserId())){
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }

        tagRepository.delete(t);
    }
}
