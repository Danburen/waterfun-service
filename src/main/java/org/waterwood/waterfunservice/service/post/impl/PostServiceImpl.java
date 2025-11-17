package org.waterwood.waterfunservice.service.post.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.post.Tag;
import org.waterwood.waterfunservice.infrastructure.persistence.PostRepository;
import org.waterwood.waterfunservice.entity.post.Post;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.infrastructure.persistence.TagRepository;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.infrastructure.utils.context.ThreadLocalUtil;
import org.waterwood.waterfunservice.service.post.PostService;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.waterfunservice.infrastructure.utils.generator.SlugGenerator;

import java.util.HashSet;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final SlugGenerator slugGenerator;
    private final UserService userService;
    private final TagRepository tagRepository;

    public PostServiceImpl(PostRepository postRepository, SlugGenerator slugGenerator, UserRepository userRepository, UserService userService, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.slugGenerator = slugGenerator;
        this.userService = userService;
        this.tagRepository = tagRepository;
    }

    @Override
    public void add(Post post, Set<Long> tagIds) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
        User u = userService.getUserById(ThreadLocalUtil.getCurrentUserId());
        post.setAuthor(u);
        post.setSlug(slugGenerator.generateSlug(post.getTitle(), postRepository));
        post.setTags(tags);
        postRepository.save(post);
    }

    @Override
    public Page<Post> listPosts(Specification<Post> spec, Pageable pageable) {
        return postRepository.findAll(spec, pageable);
    }
}
