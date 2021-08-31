package com.toby.live.lecture5;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PubSub {

    // https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.3/README.md#specification
    // protocol(규약) : onSubscribe onNext* (onError | onComplete)?
    // onSubscribe 메서드는 필수적으로 항상 호출되어야 한다. (mandatory)
    // onNext 메서드는 옵셔널이지만 한계 없이 호출될 수 있다. (optional)
    // onError 또는 onComplete 둘 중 하나는 호출된다.

    public static void main(String[] args) {

        Iterable<Integer> itr = Arrays.asList(1,2,3,4,5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber s) {
                Iterator<Integer> it = itr.iterator();
                // 필수적으로 호출해야 하는 메서드. (onSubscribe)
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            int i = 0;
                            try {
                                while(i++ < n) {
                                    if(it.hasNext()) {
                                        s.onNext(it.next());
                                    }else {
                                        s.onComplete();
                                        break;
                                    }
                                }
                            }catch (RuntimeException e) {
                                s.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                this.subscription = s;
                this.subscription.request(1);
            }
            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext " + integer);
                this.subscription.request(1);
            }
            @Override
            public void onError(Throwable t) {
                System.out.println("onError: " + t.getMessage());
            }
            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
        es.shutdown();


    }

}
