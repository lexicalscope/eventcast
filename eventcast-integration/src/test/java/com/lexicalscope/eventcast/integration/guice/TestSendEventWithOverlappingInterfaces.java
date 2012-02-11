package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TestSendEventWithOverlappingInterfaces
{
    public interface MyEventListenerOne {
        void eventOccured(Object message);
    }

    public interface MyEventListenerTwo {
        void eventOccured(Object message);
    }

    public static class Receiver implements MyEventListenerOne, MyEventListenerTwo {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> gotMessages() {
            return messages;
        }

        public void eventOccured(final Object message) {
            messages.add(message);
        }
    }

    public static class Sender {
        private final MyEventListenerOne listener;

        @Inject public Sender(final MyEventListenerOne listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final Object message) {
            listener.eventOccured(message);
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(eventCastModuleBuilder().implement(MyEventListenerOne.class).build());
            }
        });

        final Object message = new Object();

        final Receiver receiver = injector.getInstance(Receiver.class);
        injector.getInstance(Sender.class).triggerSendNow(message);
        assertThat(receiver.gotMessages(), contains(message));
    }
}
