package org.waterwood.waterfunservice.service.post.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.waterfunservicecore.entity.post.Category;
import org.waterwood.waterfunservicecore.entity.post.Tag;
import org.waterwood.common.exceptions.BusinessException;
import org.waterwood.waterfunservicecore.infrastructure.persistence.PostRepository;
import org.waterwood.waterfunservicecore.entity.post.Post;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.infrastructure.persistence.TagRepository;
import org.waterwood.waterfunservicecore.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservice.service.post.CategoryService;
import org.waterwood.waterfunservice.service.post.PostService;
import org.waterwood.waterfunservice.service.post.TagService;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.utils.generator.SlugGenerator;

import java.util.HashSet;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final SlugGenerator slugGenerator;
    private final UserService userService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final CategoryService categoryService;

    public PostServiceImpl(PostRepository postRepository, SlugGenerator slugGenerator, UserRepository userRepository, UserService userService, TagRepository tagRepository, TagService tagService, CategoryService categoryService) {
        this.postRepository = postRepository;
        this.slugGenerator = slugGenerator;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
        this.categoryService = categoryService;
    }

    @Override
    public void add(Post post, Set<Integer> tagIds) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
        User u = userService.getUserById(AuthContextHelper.getCurrentUserId());
        post.setAuthor(u);
        post.setSlug(slugGenerator.generateSlug(post.getTitle(), postRepository));
        post.setTags(tags);
        postRepository.save(post);
    }

    @Override
    public Page<Post> listPosts(Specification<Post> spec, Pageable pageable) {
        return postRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post p = postRepository.getReferenceById(id);
        if(p.getAuthor() == userService.getUserById(AuthContextHelper.getCurrentUserId())){
            postRepository.deleteById(id);
        }else{
            throw new BusinessException(BaseResponseCode.FORBIDDEN);
        }
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()-> new BusinessException(BaseResponseCode.NOT_FOUND));
    }

    @Override
    public void updatePost(Post post, Set<Integer> tagIds, Integer categoryId) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
        Category category = categoryService.getCategory(categoryId);
        User u = userService.getUserById(AuthContextHelper.getCurrentUserId());
        post.setCategory(category);
        post.setAuthor(u);
        post.setSlug(slugGenerator.generateSlug(post.getTitle(), postRepository));
        post.setTags(tags);
        postRepository.save(post);
    }
}
