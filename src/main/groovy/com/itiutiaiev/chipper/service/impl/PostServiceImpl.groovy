package com.itiutiaiev.chipper.service.impl

import com.itiutiaiev.chipper.model.dto.CommentCreateDto
import com.itiutiaiev.chipper.model.dto.CommentDto
import com.itiutiaiev.chipper.model.dto.PostCreateDto
import com.itiutiaiev.chipper.model.dto.PostDto
import com.itiutiaiev.chipper.model.dto.PostUpdateDto
import com.itiutiaiev.chipper.model.entity.Comment
import com.itiutiaiev.chipper.model.entity.Post
import com.itiutiaiev.chipper.model.entity.User
import com.itiutiaiev.chipper.repository.CommentRepository
import com.itiutiaiev.chipper.repository.PostRepository
import com.itiutiaiev.chipper.service.BaseService
import groovy.util.logging.Slf4j
import jakarta.annotation.Resource
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.domain.jaxb.SpringDataJaxb
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.management.InstanceNotFoundException

@Slf4j
@Service
@Transactional
class PostServiceImpl implements BaseService<PostCreateDto, Post> {

    @Resource
    private UserServiceImpl userService
    @Resource
    private PostRepository postRepository
    @Resource
    private CommentRepository commentRepository

    @Override
    PostCreateDto toDto(Post post) {
        PostCreateDto postCreateDto = new PostCreateDto()
        copyPropertiesFromEntity(post, postCreateDto)
        postCreateDto
    }

    @Override
    Post fromDto(PostCreateDto postCreateDto) {
        Post post = new Post()
        copyPropertiesFromDto(postCreateDto, post)
        post
    }

    Post create(PostCreateDto postCreateDto) {
        if (!userService.currentUser()) {
            log.error "Post creation failed, because user is not authenticated"
            throw new IllegalArgumentException("Post creation failed, because user is not authenticated")
        }
        Post post = fromDto(postCreateDto)
        post.authorEmail = userService.findByEmail(userService.currentUser()).email
        postRepository.save(post)
    }

    Post update(PostUpdateDto postUpdateDto) {
        if (!postUpdateDto) {
            log.error "No data provided for post update"
            throw new IllegalArgumentException("No data provided for post update")
        } else if (!userService.currentUser()) {
            log.error "Post [${postUpdateDto._id}] update failed, because user is not authenticated"
            throw new IllegalArgumentException("Post [${postUpdateDto.id}] update failed, because user is not authenticated")
        }
        User user = userService.findByEmail(userService.currentUser())
        Post post = findById(postUpdateDto._id)
        if (user.email != post.authorEmail) {
            log.error "User [${user.email}] is trying update post [${post.id}], created by another user"
            throw new IllegalArgumentException("User [${user.email}] is trying update post [${post.id}], created by another user")
        }
        copyPropertiesFromDto(postUpdateDto, post)
        postRepository.save(post)
    }

    void delete(String postId) {
        if (!postId) {
            log.error "No data provided for post delete"
            throw new IllegalArgumentException("No data provided for post delete")
        } else if (!userService.currentUser()) {
            log.error "Post [$postId] delete failed, because user is not authenticated"
            throw new IllegalArgumentException("Post [$postId] update failed, because user is not authenticated")
        }
        User user = userService.findByEmail(userService.currentUser())
        Post post = findById(postId)
        if (user.email != post.authorEmail) {
            log.error "User [${user.email}] is trying delete post [${post.id}], created by another user"
            throw new IllegalArgumentException("User [${user.email}] is trying delete post [${post.id}], created by another user")
        }
        postRepository.delete(post)
    }

    void processLike(String postId, boolean isAdding) {
        if (!postId) {
            log.error "processLike: postId is not provided"
            throw new IllegalArgumentException("processLike: postId is not provided")
        } else if (!userService.currentUser()) {
            log.error "Processing like failed, because user is not authenticated"
            throw new RuntimeException("Processing like failed, because user is not authenticated")
        }
        Post post = findById(postId)
        User actionUser = userService.findByEmail(userService.currentUser())
        if (isAdding && post.userLikes.contains(actionUser.email)) {
            log.error "User [${actionUser.email}] is trying to add a like for a post [${post.id}], but it's already added"
            throw new IllegalArgumentException("User [${actionUser.email}] is trying to add a like for a post [${post.id}], but it's already added")
        } else if (!isAdding && !post.userLikes.contains(actionUser.email)) {
            log.error "User [${actionUser.email}] is trying to add a like for a post [${post.id}], but it's already added"
            throw new IllegalArgumentException("User [${actionUser.email}] is trying to add a like for a post [${post.id}], but it's already added")
        }
        isAdding ? post.userLikes.add(actionUser.email) : post.userLikes.remove(actionUser.email)
        post.countLikes = post.countLikes + (isAdding ? 1 : -1)
        postRepository.save(post)
    }

    List<CommentDto> getCommentsDto(String postId) {
        if (!postId || postId.empty) {
            log.error "getComments: postId is not provided"
            throw new IllegalArgumentException("getComments: postId is not provided")
        }
        Post post = findById(postId)
        post.comments.collect {new CommentDto(it.authorEmail, it.text)}
    }

    void addComment(String postId, CommentCreateDto commentCreateDto) {
        if (!postId || postId.empty || !commentCreateDto?.text || commentCreateDto?.text?.empty) {
            log.error "addComment: Not enough data provided"
            throw new IllegalArgumentException("addComment: Not enough data provided")
        } else if (!userService.currentUser()) {
            log.error "Adding comment failed, because user is not authenticated"
            throw new RuntimeException("Adding comment failed, because user is not authenticated")
        }
        Post post = findById(postId)
        User actionUser = userService.findByEmail(userService.currentUser())
        Comment comment = new Comment(authorEmail: actionUser.email, text: commentCreateDto.text)
        post.comments.add(commentRepository.save(comment))
        postRepository.save(post)
    }

    void removeComment(String postId, String commentId) {
        if (!postId || postId.empty || !commentId || commentId.empty) {
            log.error "removeComment: Not enough data provided"
            throw new IllegalArgumentException("removeComment: Not enough data provided")
        } else if (!userService.currentUser()) {
            log.error "Removing comment failed, because user is not authenticated"
            throw new RuntimeException("Removing comment failed, because user is not authenticated")
        }
        Post post = findById(postId)
        User actionUser = userService.findByEmail(userService.currentUser())
        Comment comment = findCommentById(commentId)
        if (comment.authorEmail != actionUser.email) {
            log.error "User [${actionUser.email}] is trying to remove comment [${comment.id}] for post ${post.id}," +
                    "but it was originaly added by user [${comment.authorEmail}]"
            throw new RuntimeException("User [${actionUser.email}] is trying to remove comment [${comment.id}]" +
                    "for post ${post.id}, but it was originaly added by user [${comment.authorEmail}]")
        }
        commentRepository.delete(comment)
    }

    List<PostDto> getMyFeed() {
        if (!userService.currentUser()) {
            log.error "Can't get my feed, because user is not authenticated"
            throw new RuntimeException("Can't get my feed, because user is not authenticated")
        }
        User user = userService.findByEmail(userService.currentUser())
        convertPostsToPostDtoList(getFeedByEmails(user.subscriptions.toList()))
    }

    List<PostDto> getUserFeed(String email) {
        if (!email) {
            log.error "getUserFeed: email was not provided"
            throw new RuntimeException("getUserFeed: email was not provided")
        }
        User user = userService.findByEmail(email)
        convertPostsToPostDtoList(getFeedByEmails([user.email]))
    }

    Post findById(String postId) {
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)
        if (!post) {
            throw new InstanceNotFoundException("Can't find post with id `$postId`")
        }
        post
    }

    Comment findCommentById(String commentId) {
        Comment comment = commentRepository.findById(new ObjectId(commentId)).orElse(null)
        if (!comment) {
            throw new InstanceNotFoundException("Can't find comment with id `$commentId`")
        }
        comment
    }

    private List<Post> getFeedByEmails (List<String> emails) {
        postRepository.findAllByAuthorEmailIn(emails, Sort.by(Sort.Direction.ASC, "dateCreated"))
                .orElse(new ArrayList<Post>())
    }

    private List<PostDto> convertPostsToPostDtoList(List<Post> posts) {
        posts.collect { post ->
            PostDto postDto = new PostDto()
            copyPropertiesFromEntity(post, postDto)
            postDto._id = post.id.toString()
            postDto
        }
    }

}
