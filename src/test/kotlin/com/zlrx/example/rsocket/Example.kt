package com.zlrx.example.rsocket

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

data class User(
    val id: String
)

data class Post(
    val id: String
)

data class Comment(
    val id: String
)

class UserRepo {

    fun finById(id: String): Mono<User> {
        println("Find an user")
        return Mono.just(User("1"))
    }

    //example if delete gives back the object
    fun delete(user: User): Mono<User> {
        println("delete $user")
        return Mono.just(user)
    }

}

class PostServiceInt {

    fun findPostsByAuthorId(userId: String): Flux<Post> = Flux.fromIterable(listOf(Post("1"), Post("2")))

    //and if not
    fun delete(post: Post): Mono<Void> {
        println("delete $post")
        return Mono.empty()
    }

}


class CommentServiceInt {

    fun findCommentsByPostId(postId: String): Flux<Comment> = Flux.fromIterable(listOf(Comment("10"), Comment("7"), Comment("3"), Comment("4"))).filter {
        (it.id.toInt() - postId.toInt()) % 2 == 0
    }.delayElements(Duration.ofMillis(ThreadLocalRandom.current().nextLong(100, 200)))

    fun delete(comment: Comment): Mono<Comment> {
        println("delete $comment")
        return Mono.just(comment)
    }

}


class Example {

    val userRepo = UserRepo()
    val postServ = PostServiceInt()
    val comServ = CommentServiceInt()

    @Test
    fun test() {
        val result = userRepo.finById("1")
            .switchIfEmpty(Mono.error { RuntimeException() })
            .flatMap { user -> userRepo.delete(user) }
            .flatMapMany { user -> postServ.findPostsByAuthorId(user.id) }
            .flatMap { post -> postServ.delete(post).then(Mono.just(post)) }
            .flatMap { post -> comServ.findCommentsByPostId(post.id) }
            .flatMap { comment -> comServ.delete(comment) }

        StepVerifier.create(result)
            .expectNextCount(4)
            .verifyComplete()


    }


    @Test
    fun testParallel() {
        val result = userRepo.finById("1")
            .switchIfEmpty(Mono.error { RuntimeException() })
            .flatMap { user -> userRepo.delete(user) }
            .flatMap { user -> postServ.findPostsByAuthorId(user.id).collectList() }
            .flatMapMany { posts -> Flux.fromIterable(posts).parallel().flatMap { post -> postServ.delete(post).then(Mono.just(post)) } }
            .flatMap { post -> comServ.findCommentsByPostId(post.id) }
            .flatMap { comment -> comServ.delete(comment) }

        StepVerifier.create(result)
            .expectNextCount(4)
            .verifyComplete()


    }

}