import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Example {

    class Item {
        String info1;
        String info2;

        public void setInfo1(String info1) {
            this.info1 = info1;
        }
    }

    class Basket {
        String user;
        List<Item> items;

        public Basket(String user, List<Item> items) {
            this.user = user;
            this.items = items;
        }
    }

    Mono<String> mockService1() {
        return Mono.just("some data " + ThreadLocalRandom.current().nextInt(100)).delayElement(Duration.ofMillis(100));
    }

    Mono<String> mockService2() {
        return Mono.just("some other data " + ThreadLocalRandom.current().nextInt(1000)).delayElement(Duration.ofMillis(100));
    }

    Mono<Item> callService1(Item item) {
        return mockService1().zipWith(Mono.just(item))
                .map(it -> {
                    var result = it.getT2();
                    result.setInfo1(it.getT1());
                    return result;
                });
    }

    Mono<Item> callService2(Item item) {
        return mockService2().zipWith(Mono.just(item))
                .map(it -> {
                    var result = it.getT2();
                    result.setInfo1(it.getT1());
                    return result;
                });
    }


    @Test
    public void testBasket() {
        var basket = new Basket("first", List.of(new Item(), new Item(), new Item()));
        Mono<Basket> basketMono = Mono.just(basket)
                .flatMap(b ->
                        Flux.fromIterable(b.items)
                                .flatMap(this::callService1)
                                .flatMap(this::callService2)
                                .then(Mono.just(b))
                );


        StepVerifier.create(basketMono)
                .expectNextMatches(b -> b.items.get(0).info1 != null)
                .verifyComplete();


    }


}
