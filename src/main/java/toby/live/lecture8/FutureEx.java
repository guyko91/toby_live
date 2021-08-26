package toby.live.lecture8;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

    // 자바와 스프링의 비동기 기술
    // Future 와 같은 핸들러 사용. (자바8 이전)
    // Callback 을 사용. (자바8 이후)

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 스레드를 생성한다는 건 무거운 작업. (메모리와 CPU 자원을 많이 사용)
        // 스레드 풀을 생성하여 사용-반납 하는 방식으로 사용.
        // newCachedThreadPool : maximum 제한 없음.
        ExecutorService es = Executors.newCachedThreadPool();

        // ex1.execute() runnable 을 인자로 받음. (return 받을 수 없음)
//        es.execute(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.info("hello!");
//        });

        // ex2.submit() runnable 과 callable 을 인자로 받을 수 있음. (return 받을 수 있음)
//        Future<String> f = es.submit(() -> {
//            Thread.sleep(2000);
//            log.info("async");
//            return "Hello";
//        });
//
//        System.out.println(f.isDone());
//        Thread.sleep(2100);
//        log.info("exit");
//        System.out.println(f.isDone());
//        System.out.println(f.get());

        // execute 는 해당 작업을 바로 pass 한다. (non blocking)
        // submit 은 .get() 하는 순간 비동기 결과물을 호출한다. .get()이 호출되기 전까지 blocking 된다. (다음 작업으로 넘어가지 못함.)


        // -------------------------------------------------------------------------------------------------------- //

//        FutureTask<String> f2 = new FutureTask<String>(() -> {
//            Thread.sleep(2000);
//            log.info("async");
//            return "Hello";
//        }) {
//            @Override
//            protected void done() {
//                try {
//                    System.out.println(get());
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        es.execute(f2);
//        es.shutdown();

        // -------------------------------------------------------------------------------------------------------- //

        CallbackFutureTask f = new CallbackFutureTask(() ->
            {
                Thread.sleep(2000);
                if (1==1) throw new RuntimeException("Async ERROR !!");
                log.info("async");
                return "Hello";
            }
            , s -> System.out.println("RESULT : " + s)
            , e -> System.out.println("ERROR : " + e.getMessage())
        );

        es.execute(f);
        es.shutdown();
    }

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

}
