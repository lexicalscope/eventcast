package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.eventCastModuleBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TestSendEventIsBroadcast
{
    public interface MyEventListener {
        void eventOccured(Object message);
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

    public static class Sender {
        private final MyEventListener listener;

        @Inject public Sender(final MyEventListener listener) {
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
                install(eventCastModuleBuilder().implement(MyEventListener.class).build());
            }
        });

        final Object message = new Object();

        final Receiver receiverOne = injector.getInstance(Receiver.class);
        final Receiver receiverTwo = injector.getInstance(Receiver.class);

        injector.getInstance(Sender.class).triggerSendNow(message);

        assertThat(receiverOne, not(sameInstance(receiverTwo)));
        assertThat(receiverOne.gotMessages(), contains(message));
        assertThat(receiverTwo.gotMessages(), contains(message));
    }
}
