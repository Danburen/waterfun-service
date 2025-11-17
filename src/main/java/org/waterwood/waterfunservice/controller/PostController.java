package org.waterwood.waterfunservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.common.enums.PostStatus;
import org.waterwood.waterfunservice.dto.common.enums.PostVisibility;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.entity.post.Post;
import org.waterwood.waterfunservice.infrastructure.mapper.PostMapper;
import org.waterwood.waterfunservice.dto.response.post.PostResponse;
import org.waterwood.waterfunservice.infrastructure.persistence.utils.PostSpec;
import org.waterwood.waterfunservice.service.post.PostService;
import org.waterwood.waterfunservice.dto.request.post.CreatePostRequest;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    
    @PostMapping
    public ApiResponse<Void> addPost(@Valid @RequestBody CreatePostRequest body){
        postService.add(postMapper.toEntity(body), body.getTagIds());
        return ApiResponse.success();
    }

    /***
     * Get All the posts by page and optional params;
     * @param page page number
     * @param size page size
     * @param categoryId category id
     * @param tagIds tag ids belong to the post
     * @return  page of posts
     */
    @GetMapping("/list")
    public ApiResponse<Page<PostResponse>> listPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false)List<Integer> tagIds
            ){

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Specification<Post> spec = PostSpec.ofPublic(categoryId, tagIds,
                null);
        Page<Post> posts = postService.listPosts(spec, pageable);
        Page<PostResponse> postResponses = posts.map(postMapper::toPostResponseDto);
        return ApiResponse.success(postResponses);
    }
    @GetMapping("/me/list")
    public ApiResponse<Page<PostResponse>> listMyPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) PostVisibility visibility,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false)List<Integer> tagIds
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Specification<Post> spec = PostSpec.ofSelf(status,visibility, categoryId, tagIds);
        Page<Post> posts = postService.listPosts(spec, pageable);
        Page<PostResponse> postResponses = posts.map(postMapper::toPostResponseDto);
        return ApiResponse.success(postResponses);
    }
}
