package toby.live.lecture6;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub2 {

    // Operator (오퍼레이터)
    // Publisher -> [Data1] -> mapPub -> [Data 2] -> logSub
    // 1. map (d1 -> f -> d2)
    // 2. sum (d1 -> SUM() -> d2)

    public static void main(String[] args) {

        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
//        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
//        Publisher<Integer> sumPub = sumPub(pub);
        Publisher<StringBuilder> reducePub = reducePub(pub
                , new StringBuilder()
                , (a, b) -> a.append(b+","));
        reducePub.subscribe(logSub());
    }

    private static <T,R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    R result = init;
                    @Override
                    public void onNext(T i) {
                        result = bf.apply(result, i);
                    }
                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

    // 합을 구해서 합의 결과만 리턴하는 Publisher
//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSub(sub) {
//                    Integer sum = 0;
//                    @Override
//                    public void onNext(Integer integer) {
//                        sum += integer;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum);
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
//    }

    // 함수를 수행하는 Publisher
    // T type to R type Publisher
    private static <T,R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T,R>(sub) {
                    @Override
                    public void onNext(T integer) {
                        sub.onNext(f.apply(integer));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T integer) {
                System.out.println("onNext : " + integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError : " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> list) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            list.forEach(i -> s.onNext(i));
                            s.onComplete();
                        }catch (Throwable t) {
                            s.onError(t);
                        }
                    }
                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

}
