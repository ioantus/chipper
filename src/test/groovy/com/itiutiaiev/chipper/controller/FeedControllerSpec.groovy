package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.model.dto.CommentCreateDto
import com.itiutiaiev.chipper.model.dto.PostCreateDto
import com.itiutiaiev.chipper.repository.PostRepository
import com.jayway.jsonpath.JsonPath
import jakarta.annotation.Resource
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FeedControllerSpec extends BaseControllerSpec {

    @Resource
    private PostRepository postRepository

    @AfterEach
    void cleanup() {
        postRepository.deleteAll()
    }

    def "test_myFeed"() {
        when:" Unauthorised"
        def response = performGet("/api/v1/feed/my", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" No posts"
        String firstUserToken = login(firstTestUser.email, standardPassword)
        String secondUserToken = login(secondTestUser.email, standardPassword)
        response = performGet("/api/v1/feed/my", firstUserToken, null)

        then:
        noExceptionThrown()
        response.status == 200
        response.contentAsString == "[]"

        when:" No records because no subscriptions"
        prepareTestData(firstUserToken, secondUserToken)
        response = performGet("/api/v1/feed/my", firstUserToken, null)

        then:
        noExceptionThrown()
        response.status == 200
        response.contentAsString == "[]"

        when:" firstTestUser user got 1 post from secondTestUser with 1 comment & 1 like"
        // Subscribe users to each other
        performPatch("/api/v1/user/subscribe/${secondTestUser.email}", firstUserToken, null)
        performPatch("/api/v1/user/subscribe/${firstTestUser.email}", secondUserToken, null)
        response = performGet("/api/v1/feed/my", firstUserToken, null)
        def json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        json?.size() == 1
        json[0]?."comments"?.size() == 1
        json[0]?."countLikes" == 1

        when:" secondTestUser user got 2 posts from firstTestUser with 1 comment & 1 like per post"
        // Subscribe users to each other
        performPatch("/api/v1/user/subscribe/${secondTestUser.email}", firstUserToken, null)
        performPatch("/api/v1/user/subscribe/${firstTestUser.email}", secondUserToken, null)
        response = performGet("/api/v1/feed/my", secondUserToken, null)
        json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        json?.size() == 2
        json[0]?."comments".size() == 1
        json[0]?."countLikes" == 1
        json[1]?."comments"?.size() == 1
        json[1]?."countLikes" == 1
    }


    def "test_userFeed"() {
        when:" Unauthorised"
        def response = performGet("/api/v1/feed/${firstTestUser.email}", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Incorrect email"
        String firstUserToken = login(firstTestUser.email, standardPassword)
        String secondUserToken = login(secondTestUser.email, standardPassword)
        response = performGet("/api/v1/feed/test", firstUserToken, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" User not found"
        response = performGet("/api/v1/feed/test@tesst.ua", firstUserToken, null)

        then:
        noExceptionThrown()
        response.status == 404

        when:" No posts"
        response = performGet("/api/v1/feed/${firstTestUser.email}", firstUserToken, null)

        then:
        noExceptionThrown()
        response.status == 200
        response.contentAsString == "[]"

        when:" firstTestUser user have 2 posts with 1 comment & 1 like per post"
        prepareTestData(firstUserToken, secondUserToken)
        response = performGet("/api/v1/feed/${firstTestUser.email}", secondUserToken, null)
        def json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        json?.size() == 2
        json[0]?."comments".size() == 1
        json[0]?."countLikes" == 1
        json[1]?."comments"?.size() == 1
        json[1]?."countLikes" == 1

        when:" secondTestUser user have 1 post with 1 comment & 1 like"
        response = performGet("/api/v1/feed/${secondTestUser.email}", firstUserToken, null)
        json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        json?.size() == 1
        json[0]?."comments"?.size() == 1
        json[0]?."countLikes" == 1
    }

    private void prepareTestData(String firstUserToken, String secondUserToken) {
        // Creating 2 new posts for firstTestUser
        String firstUserFirstPostId = performPost("/api/v1/post/create", firstUserToken,
                new PostCreateDto(title: "title1", body: "test1".bytes)).contentAsString
        String firstUserSecondPostId = performPost("/api/v1/post/create", firstUserToken,
                new PostCreateDto(title: "title2", body: "test2".bytes)).contentAsString
        // Adding 1 comment per post from secondTestUser
        performPatch("/api/v1/post/$firstUserFirstPostId/addComment", secondUserToken,
                new CommentCreateDto("test comment 1"))
        performPatch("/api/v1/post/$firstUserSecondPostId/addComment", secondUserToken,
                new CommentCreateDto("test comment 2"))
        // Adding 1 like per post from secondTestUser
        performPatch("/api/v1/post/$firstUserFirstPostId/postLike", secondUserToken, null)
        performPatch("/api/v1/post/$firstUserSecondPostId/postLike", secondUserToken, null)
        // Creating 1 post for secondTestUser with 1 comment from firstTestUser
        String secondUserFirstPostId = performPost("/api/v1/post/create", secondUserToken,
                new PostCreateDto(title: "title3", body: "test3".bytes)).contentAsString
        // Adding 1 comment from firstTestUser
        performPatch("/api/v1/post/$secondUserFirstPostId/addComment", firstUserToken,
                new CommentCreateDto("test comment 3"))
        // Adding 1 like from firstTestUser
        performPatch("/api/v1/post/$secondUserFirstPostId/postLike", firstUserToken, null)
    }

}