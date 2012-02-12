package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TestSendEventToAsyncMethod
{
    public interface MyEventListener {
        void eventOccured() throws InterruptedException;
    }

    public static class Receiver implements MyEventListener {
        private final BlockingQueue<Thread> messages = new ArrayBlockingQueue<Thread>(1);

        public BlockingQueue<Thread> gotMessages() {
            return messages;
        }

        public void eventOccured() throws InterruptedException {
            messages.put(currentThread());
        }
    }

    public static class Sender {
        private final MyEventListener listener;

        @Inject public Sender(final MyEventListener listener) {
            this.listener = listener;
        }

        public void triggerSendNow() throws InterruptedException {
            listener.eventOccured();
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(eventCastModuleBuilder()
                        .implement(MyEventListener.class, newFixedThreadPool(1))
                        .build());
            }
        });

        final Receiver receiverOne = injector.getInstance(Receiver.class);
        final Receiver receiverTwo = injector.getInstance(Receiver.class);

        injector.getInstance(Sender.class).triggerSendNow();

        assertThat(receiverOne, not(sameInstance(receiverTwo)));
        assertThat(receiverOne.gotMessages().take(), not(equalTo(currentThread())));
        assertThat(receiverTwo.gotMessages().take(), not(equalTo(currentThread())));
    }
}
