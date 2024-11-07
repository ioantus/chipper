package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.model.dto.CommentCreateDto
import com.itiutiaiev.chipper.model.dto.PostCreateDto
import com.itiutiaiev.chipper.model.dto.PostUpdateDto
import com.itiutiaiev.chipper.model.entity.Comment
import com.itiutiaiev.chipper.model.entity.Post
import com.itiutiaiev.chipper.repository.PostRepository
import com.jayway.jsonpath.JsonPath
import jakarta.annotation.Resource
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PostControllerSpec extends BaseControllerSpec {

    @Resource
    private PostRepository postRepository

    @AfterEach
    void cleanup() {
        postRepository.deleteAll()
    }

    def "test_createPost"() {
        when:" Unauthorised"
        def response = performPost("/api/v1/post/create", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPost("/api/v1/post/create", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" No Title"
        response = performPost("/api/v1/post/create", token,
                new PostCreateDto(body: "testBody".bytes))

        then:
        noExceptionThrown()
        response.status == 400

        when:" No body"
        response = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "testTitle"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Success"
        response = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "testTitle", body: "testBody".bytes))
        Post post = postRepository.findById(new ObjectId(response.contentAsString)).orElse(null)

        then:
        noExceptionThrown()
        response.status == 201
        post?.authorEmail == firstTestUser.email
        post.title == "testTitle"
        post.body == "testBody".bytes
        post.comments.size() == 0
        post.userLikes.size() == 0
        post.countLikes == 0
        post.dateCreated
        post.lastModified
        post.dateCreated == post.lastModified
    }

    def "test_updatePost"() {
        when:" Unauthorised"
        def response = performPut("/api/v1/post/update", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPut("/api/v1/post/update", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" No Id"
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(title: "testTitle", body: "testBody".bytes))

        then:
        noExceptionThrown()
        response.status == 400

        when:" No Title"
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(_id: "1", body: "testBody".bytes))

        then:
        noExceptionThrown()
        response.status == 400

        when:" No body"
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(_id: "1", title: "testTitle"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Incorrect id"
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(_id: "1", title: "testTitle", body: "testBody".bytes))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post is not exists"
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(_id: ObjectId.get().toString(), title: "testTitle", body: "testBody".bytes))

        then:
        noExceptionThrown()
        response.status == 404

        when:" Success"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performPut("/api/v1/post/update", token,
                new PostUpdateDto(_id: postId, title: "secondTestTitle", body: "secondTestBody".bytes))
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        post?.authorEmail == firstTestUser.email
        post.title == "secondTestTitle"
        post.body == "secondTestBody".bytes
        post.comments.size() == 0
        post.userLikes.size() == 0
        post.countLikes == 0
        post.dateCreated
        post.lastModified
        post.dateCreated != post.lastModified
    }

    def "test_deletePost"() {
        when:" Unauthorised"
        def response = performDelete("/api/v1/post/delete/1", null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Incorrect id"
        String token = login(firstTestUser.email, standardPassword)
        response = performDelete("/api/v1/post/delete/1", token)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post is not exists"
        response = performDelete("/api/v1/post/delete/${ObjectId.get()}", token)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Success"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performDelete("/api/v1/post/delete/$postId", token)
        def post = postRepository.findById(new ObjectId(postId))

        then:
        noExceptionThrown()
        response.status == 204
        post.empty
    }

    def "test_postLike"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/post/1/postLike", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/post/1/postLike", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post id is incorrect"
        response = performPatch("/api/v1/post/1/postLike", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post not found"
        response = performPatch("/api/v1/post/${ObjectId.get().toString()}/postLike", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Success"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performPatch("/api/v1/post/$postId/postLike", token, null)
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        post.countLikes == 1
        post.userLikes.contains(firstTestUser.email)

        when:" Success"
        response = performPatch("/api/v1/post/$postId/postLike", token, null)
        post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        response.status == 400
        response.contentAsString == "User [${firstTestUser.email}] is trying to add a like for a post [$postId], but it's already added"
        post.countLikes == 1
        post.userLikes.contains(firstTestUser.email)
    }

    def "test_removeLike"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/post/1/removeLike", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/post/1/removeLike", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post id is incorrect"
        response = performPatch("/api/v1/post/1/removeLike", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post not found"
        response = performPatch("/api/v1/post/${ObjectId.get().toString()}/removeLike", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Like is missing"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performPatch("/api/v1/post/$postId/removeLike", token, null)
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        noExceptionThrown()
        response.status == 400
        response.contentAsString == "User [${firstTestUser.email}] is trying to add a like for a post [$postId], but it's already added"
        post.countLikes == 0
        post.userLikes.size() == 0

        when:" Success"
        performPatch("/api/v1/post/$postId/postLike", token, null)
        response = performPatch("/api/v1/post/$postId/removeLike", token, null)
        post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        response.status == 202
        post.countLikes == 0
        post.userLikes.size() == 0
    }

    def "test_getComments"() {
        when:" Unauthorised"
        def response = performGet("/api/v1/post/1/comments", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Post id is incorrect"
        String token = login(firstTestUser.email, standardPassword)
        response = performGet("/api/v1/post/1/comments", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post not found"
        response = performGet("/api/v1/post/${ObjectId.get()}/comments", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Comments not found"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performGet("/api/v1/post/$postId/comments", token, null)

        then:
        noExceptionThrown()
        response.status == 200
        response.contentAsString == "[]"

        when:" Success"
        performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 1"))
        performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 2"))
        performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 3"))
        response = performGet("/api/v1/post/$postId/comments", token, null)
        def json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        json?.size() == 3
        json[0]."text" == "test 1"
        json[1]."text" == "test 2"
        json[2]."text" == "test 3"
    }

    def "test_addComment"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/post/1/addComment", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/post/1/addComment", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post id is incorrect"
        response = performPatch("/api/v1/post/1/addComment", token, new CommentCreateDto("test"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Post not found"
        response = performPatch("/api/v1/post/${ObjectId.get().toString()}/addComment", token, new CommentCreateDto("test"))

        then:
        noExceptionThrown()
        response.status == 404

        when:" Success"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        response = performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 1"))
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)
        Comment comment = post.comments.find { it.text == "test 1" }

        then:
        noExceptionThrown()
        response.status == 202
        post.comments.size() == 1
        comment.authorEmail == firstTestUser.email
        comment.dateCreated

        when:" Another comment - success"
        response = performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 2"))
        post = postRepository.findById(new ObjectId(postId)).orElse(null)
        comment = post.comments.find { it.text == "test 2" }

        then:
        response.status == 202
        post.comments.size() == 2
        comment.authorEmail == firstTestUser.email
        comment.dateCreated
    }

    def "test_removeComment"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/post/1/removeComment/1", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Post id is incorrect"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/post/1/removeComment/1", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Comment id is incorrect"
        response = performPatch("/api/v1/post/${ObjectId.get().toString()}/removeComment/1", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Post not found"
        response = performPatch("/api/v1/post/${ObjectId.get().toString()}/removeComment/${ObjectId.get().toString()}", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Comment not found"
        String postId = performPost("/api/v1/post/create", token,
                new PostCreateDto(title: "firstTestTitle", body: "firstTestBody".bytes)).contentAsString
        performPatch("/api/v1/post/$postId/addComment", token, new CommentCreateDto("test 1"))
        response = performPatch("/api/v1/post/$postId/removeComment/${ObjectId.get().toString()}", token, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" Success"
        Post post = postRepository.findById(new ObjectId(postId)).orElse(null)
        Comment comment = post.comments.first()
        response = performPatch("/api/v1/post/$postId/removeComment/${comment.id}", token, null)
        post = postRepository.findById(new ObjectId(postId)).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        post.comments.size() == 0
    }

}