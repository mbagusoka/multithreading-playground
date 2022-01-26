package com.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MultithreadingPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultithreadingPlaygroundApplication.class, args);
    }

    // ======= HOW WE MANAGE RESOURCE ========

    // CONCURRENCY
    //1 A------> Bxxxxxx> A-------> Bxxxxx> --> CPU scheduling -> round robin

    // PARALLEL
    //1 A --------------->
    //2 B xxxxxxxxxxxxxxx>

    // PARALLELISM
    // A ---------------->
    //1 A ---->
    //2 A ---->
    //3 A ---->

    // ======= HOW WE MANAGE FLOW ========

    // SYNCHRONOUS => BLOCKING

    // ASYNCHRONOUS => NON BLOCKING
    // - 1 thread -> event loop / reactive
    // - > thread -> delegate task
}

//@Service
//class Concurrent implements CommandLineRunner {
//
//    @Override
//    public void run(String... args) throws Exception {
//        ExecutorService es = Executors.newSingleThreadExecutor();
//        es.submit(() -> {
//            Sleeper.sleep(2000);
//            System.out.println("Masak nasgor pedas oleh " + Thread.currentThread().getName());
//        });
//        es.submit(() -> {
//            Sleeper.sleep(1000);
//            System.out.println("Bikin es teh manis oleh " + Thread.currentThread().getName());
//        });
//
//        es.shutdown();
//    }
//}

//@Service
//class Parallel implements CommandLineRunner {
//
//    @Override
//    public void run(String... args) throws Exception {
//        ExecutorService es = Executors.newFixedThreadPool(2);
//        es.submit(() -> {
//            Sleeper.sleep(2000);
//            System.out.println("Masak nasgor pedas oleh " + Thread.currentThread().getName());
//        });
//        es.submit(() -> {
//            Sleeper.sleep(1000);
//            System.out.println("Bikin es teh manis oleh " + Thread.currentThread().getName());
//        });
//
//        es.shutdown();
//    }
//}

//@Service
//class Parallellism implements CommandLineRunner {
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Note: premature optimization is root of evil.
//        //1 . jangan langsung pake -> profile Thread java 32b -> 512kb || java 64b -> 1MB --> OOM
//        // --> 5secs >> 1secs
//        //2. introduce parallelism > profile > 1secs > Subramaniam Goetz equation ->
//        // IO == cpu/ 1- (blocking factor) => 1 / 1- 0.1 = 1 / 0.1 = 10
//        //   blocking factor = how long thread idle (blocking);
//        //   A ---------B X -> IO ke DB -> 10% -> 0.1
//        // CPU == CPU core
//        // 3. profile parallelism as equation -> 1secs
//
//        // easiest way to profile --> postman time
//
//        Arrays.asList(1, 2, 3, 4)
//            .parallelStream() // 10 thread
//            .forEach(x -> {
//                System.out.println("Nomor " + x + " oleh " + Thread.currentThread().getName());
//                Sleeper.sleep(1000);
//            });
//    }
//}

// ASYNC

@RestController
class LongController {

    private final ExecutorService executorService = Executors.newWorkStealingPool();

    @GetMapping("/long")
    public String get() {
        System.out.println(" PAKE TOMCAT : " + Thread.currentThread().getName());
        // do rigorous process
        CompletableFuture.runAsync(() -> {
            System.out.println(" DO HARD WORK");
            Sleeper.sleep(10000);
        });

        return "IN PROGRESS";
    }

    @GetMapping("/pooling")
    public CompletableFuture<String> getPool() {
        System.out.println(" PAKE TOMCAT : " + Thread.currentThread().getName());
        // do rigorous process

        return CompletableFuture.supplyAsync(
            () -> {
                Sleeper.sleep(10000);
                return "IN PROGRESS in thread " + Thread.currentThread().getName();
            }, executorService)
            .thenApply(x -> x + " BONUS");
    }
}

class Sleeper {

    public static void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (Exception e) {
            //no ops
        }
    }
}