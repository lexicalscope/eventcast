package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lexicalscope.eventcast.EventCastUnhandledListener;

public class TestUnhandledEventIsNotifed
{
    public interface MyEventListener {
        void eventOccured(Object message) throws Exception;
    }

    public static class UnhandledReceiver implements EventCastUnhandledListener
    {
        private Type listenerType;
        private Method listenerMethod;
        private Object[] eventArguments;

        public void unhandledEventCast(
                final Type listenerType,
                final Method listenerMethod,
                final Object[] eventArguments) {
            this.listenerType = listenerType;
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
                bind(UnhandledReceiver.class);
                install(eventCastModuleBuilder().implement(MyEventListener.class).build());
            }
        });

        final Object message = new Object();

        final UnhandledReceiver exceptionReveiver = injector.getInstance(UnhandledReceiver.class);

        injector.getInstance(Sender.class).triggerSendNow(message);

        assertThat(exceptionReveiver.listenerType, equalTo((Type) MyEventListener.class));
        assertThat(exceptionReveiver.listenerMethod.getName(), equalTo("eventOccured"));
        assertThat(exceptionReveiver.eventArguments[0], sameInstance(message));
    }
}
