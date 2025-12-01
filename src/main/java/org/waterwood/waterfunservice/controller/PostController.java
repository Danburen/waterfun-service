package org.waterwood.waterfunservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.api.enums.PostStatus;
import org.waterwood.api.enums.PostVisibility;
import org.waterwood.waterfunservice.dto.request.post.PatchUserPostReq;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservicecore.entity.post.Post;
import org.waterwood.waterfunservice.infrastructure.mapper.PostMapper;
import org.waterwood.waterfunservice.dto.response.post.PostResponse;
import org.waterwood.waterfunservicecore.infrastructure.persistence.utils.PostSpec;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservice.service.post.PostService;
import org.waterwood.waterfunservice.dto.request.post.CreatePostRequest;

import java.util.List;

@Slf4j
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

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updatePost(@PathVariable Long id, @Valid @RequestBody PatchUserPostReq body){
        Post post = postService.getPostById(id);
        if(! AuthContextHelper.getCurrentUserId().equals(post.getAuthor().getId())){
            return ApiResponse.response(BaseResponseCode.FORBIDDEN);
        }
        Post p = postMapper.partialUpdate(body, post);;
        postService.updatePost(p, body.getTagIds(), body.getCategoryId());
        return ApiResponse.success();
    }
}
