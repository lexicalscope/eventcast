package com.lexicalscope.eventcast.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lexicalscope.eventcast.EventCastModuleBuilder;

public class TestSendEventWithArgumentsWithGuice
{
    public interface MyEventListener {
        void eventOccured(Object argument);
    }

    public static class Receiver implements MyEventListener {
        private Object argument;

        public Object getEvent() {
            return argument;
        }

        public void eventOccured(final Object argument) {
            this.argument = argument;
        }
    }

    public static class Sender {
        private final MyEventListener listener;

        @Inject public Sender(final MyEventListener listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final Object argument) {
            listener.eventOccured(argument);
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(new EventCastModuleBuilder().implement(MyEventListener.class).build());
            }
        });

        final Object message = new Object();

        final Receiver receiver = injector.getInstance(Receiver.class);
        injector.getInstance(Sender.class).triggerSendNow(message);
        assertThat(receiver.getEvent(), sameInstance(message));
    }
}
