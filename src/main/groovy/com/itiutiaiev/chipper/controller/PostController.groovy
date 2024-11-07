package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.controller.api.PostApi
import com.itiutiaiev.chipper.model.dto.CommentCreateDto
import com.itiutiaiev.chipper.model.dto.CommentDto
import com.itiutiaiev.chipper.model.dto.PostCreateDto
import com.itiutiaiev.chipper.model.dto.PostUpdateDto
import com.itiutiaiev.chipper.service.impl.PostServiceImpl
import jakarta.annotation.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/post")
class PostController implements PostApi {

    @Resource
    private PostServiceImpl postService

    @PostMapping("/create")
    ResponseEntity<String> createPost(@RequestBody PostCreateDto postCreateDto) {
        ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postCreateDto).id.toString())
    }

    @PutMapping("/update")
    ResponseEntity<Void> updatePost(@RequestBody PostUpdateDto postUpdateDto) {
        postService.update(postUpdateDto)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @DeleteMapping("/delete/{postId}")
    ResponseEntity<Void> deletePost(@PathVariable("postId") String postId) {
        postService.delete(postId)
        ResponseEntity.noContent().build()
    }

    @PatchMapping("/{postId}/postLike")
    ResponseEntity<Void> postLike(@PathVariable("postId") String postId) {
        postService.processLike(postId, true)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PatchMapping("/{postId}/removeLike")
    ResponseEntity<Void> removeLike(@PathVariable("postId") String postId) {
        postService.processLike(postId, false)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @GetMapping("/{postId}/comments")
    ResponseEntity<List<CommentDto>> getComments(@PathVariable("postId") String postId) {
        ResponseEntity.ok(postService.getCommentsDto(postId))
    }

    @PatchMapping("/{postId}/addComment")
    ResponseEntity<Void> addComment(@PathVariable("postId") String postId, @RequestBody CommentCreateDto commentCreateDto) {
        postService.addComment(postId, commentCreateDto)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PatchMapping("/{postId}/removeComment/{commentId}")
    ResponseEntity<Void> removeComment(@PathVariable("postId") String postId, @PathVariable("commentId") String commentId) {
        postService.removeComment(postId, commentId)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

}
