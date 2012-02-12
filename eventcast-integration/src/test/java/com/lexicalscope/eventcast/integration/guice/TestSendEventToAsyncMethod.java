package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lexicalscope.eventcast.EventCastingExceptionListener;

public class TestSendEventToAsyncMethod
{
    public interface MyEventListener {
        void eventOccured(Object message) throws InterruptedException, Exception;
    }

    public static class Receiver implements MyEventListener {
        private final BlockingQueue<Thread> messages = new ArrayBlockingQueue<Thread>(1);

        public BlockingQueue<Thread> gotMessages() {
            return messages;
        }

        public void eventOccured(final Object message) throws InterruptedException {
            messages.put(currentThread());
        }
    }

    public static class Sender {
        private final MyEventListener listener;

        @Inject public Sender(final MyEventListener listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final Object message) throws Exception {
            listener.eventOccured(message);
        }
    }

    public static class ThrowingReceiver implements MyEventListener {
        private Exception exception;

        public void eventOccured(final Object message) throws Exception {
            throw exception;
        }

        public void setException(final Exception exception) {
            this.exception = exception;
        }
    }

    public static class ExceptionReceiver implements EventCastingExceptionListener
    {
        private final BlockingQueue<ExceptionReceiver> messages = new ArrayBlockingQueue<ExceptionReceiver>(1);
        private Throwable cause;
        private Type listenerType;
        private Object listener;
        private Method listenerMethod;
        private Object[] eventArguments;

        public void exceptionDuringEventCast(
                final Throwable cause,
                final Type listenerType,
                final Object listener,
                final Method listenerMethod,
                final Object[] eventArguments) {
            this.cause = cause;
            this.listenerType = listenerType;
            this.listener = listener;
            this.listenerMethod = listenerMethod;
            this.eventArguments = eventArguments;
            messages.add(this);
        }
    }

    @org.junit.Test public void canSendAndEventToAsyncListener() throws Exception {
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

        injector.getInstance(Sender.class).triggerSendNow(new Object());

        assertThat(receiverOne, not(sameInstance(receiverTwo)));
        assertThat(receiverOne.gotMessages().take(), not(equalTo(currentThread())));
        assertThat(receiverTwo.gotMessages().take(), not(equalTo(currentThread())));
    }

    @org.junit.Test public void canBeNotifiedAboutExceptionsFromAsyncListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                bind(ThrowingReceiver.class);
                bind(ExceptionReceiver.class);
                install(eventCastModuleBuilder()
                        .implement(MyEventListener.class, newFixedThreadPool(1))
                        .build());
            }
        });

        final Object message = new Object();

        final Receiver receiverOne = injector.getInstance(Receiver.class);
        final ThrowingReceiver receiverTwo = injector.getInstance(ThrowingReceiver.class);
        final Receiver receiverThree = injector.getInstance(Receiver.class);
        final ExceptionReceiver exceptionReceiver = injector.getInstance(ExceptionReceiver.class);

        final RuntimeException exception = new RuntimeException("my message");
        receiverTwo.setException(exception);

        injector.getInstance(Sender.class).triggerSendNow(message);

        assertThat(receiverOne.gotMessages().take(), not(equalTo(currentThread())));
        assertThat(receiverThree.gotMessages().take(), not(equalTo(currentThread())));
        final ExceptionReceiver exceptionReceiverData = exceptionReceiver.messages.take();
        assertThat(exceptionReceiverData.cause, equalTo((Throwable) exception));
        assertThat(exceptionReceiverData.listenerType, equalTo((Type) MyEventListener.class));
        assertThat(exceptionReceiverData.listener, sameInstance((Object) receiverTwo));
        assertThat(exceptionReceiverData.listenerMethod.getName(), equalTo("eventOccured"));
        assertThat(exceptionReceiverData.eventArguments[0], sameInstance(message));
    }
}
