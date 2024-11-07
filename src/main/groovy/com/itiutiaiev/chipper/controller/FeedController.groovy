package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.controller.api.FeedApi
import com.itiutiaiev.chipper.model.dto.PostDto
import com.itiutiaiev.chipper.service.impl.PostServiceImpl
import jakarta.annotation.Resource
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/feed")
class FeedController implements FeedApi {

    @Resource
    PostServiceImpl postService

    @GetMapping("/my")
    ResponseEntity<List<PostDto>> getMyFeed() {
        ResponseEntity.ok(postService.getMyFeed())
    }

    @GetMapping("/{email}")
    ResponseEntity<List<PostDto>> getUserFeed(@PathVariable("email") String email) {
        ResponseEntity.ok(postService.getUserFeed(email))
    }

}
