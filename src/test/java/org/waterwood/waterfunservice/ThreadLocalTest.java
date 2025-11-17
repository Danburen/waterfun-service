package org.waterwood.waterfunservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class ThreadLocalTest {
    @Test
    public void testThreadLocalSetAndGet(){
        ThreadLocal tl = new ThreadLocal<>();
        new Thread(()->{
           tl.set("A");
           System.out.println(Thread.currentThread().getName() + ": " + tl.get());
           System.out.println(Thread.currentThread().getName() + ": " + tl.get());
           System.out.println(Thread.currentThread().getName() + ": " + tl.get());

        },"Blue");

        new Thread(()->{
            tl.set("B");
            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
            System.out.println(Thread.currentThread().getName() + ": " + tl.get());

            System.out.println(Thread.currentThread().getName() + ": " + tl.get());

        },"Red");
    }
}
