package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lexicalscope.eventcast.EventCastingExceptionListener;

public class TestSendEventIsNotStoppedByAnException
{
    public interface MyEventListener {
        void eventOccured(Object message) throws Exception;
    }

    public static class Receiver implements MyEventListener {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> gotMessages() {
            return messages;
        }

        public void eventOccured(final Object message) {
            messages.add(message);
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

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                bind(ThrowingReceiver.class);
                bind(ExceptionReceiver.class);
                install(eventCastModuleBuilder().implement(MyEventListener.class).build());
            }
        });

        final Exception exception = new Exception("my exception");
        final Object message = new Object();

        final Receiver receiverOne = injector.getInstance(Receiver.class);
        final ThrowingReceiver throwingReceiver = injector.getInstance(ThrowingReceiver.class);
        final Receiver receiverTwo = injector.getInstance(Receiver.class);

        final ExceptionReceiver exceptionReveiver = injector.getInstance(ExceptionReceiver.class);

        throwingReceiver.setException(exception);
        injector.getInstance(Sender.class).triggerSendNow(message);

        assertThat(receiverOne, not(sameInstance(receiverTwo)));
        assertThat(receiverOne.gotMessages(), contains(message));
        assertThat(receiverTwo.gotMessages(), contains(message));

        assertThat(exceptionReveiver.cause, sameInstance((Throwable) exception));
        assertThat(exceptionReveiver.listenerType, equalTo((Type) MyEventListener.class));
        assertThat(exceptionReveiver.listener, sameInstance((Object) throwingReceiver));
        assertThat(exceptionReveiver.listenerMethod.getName(), equalTo("eventOccured"));
        assertThat(exceptionReveiver.eventArguments[0], sameInstance(message));
    }
}
