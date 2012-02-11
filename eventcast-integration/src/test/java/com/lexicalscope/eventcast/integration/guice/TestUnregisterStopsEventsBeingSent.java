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
import com.lexicalscope.eventcast.EventCaster;

public class TestUnregisterStopsEventsBeingSent
{
    public interface MyEventListener {
        void eventOccured(Object argument);
    }

    public static class Receiver implements MyEventListener {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> getMessages() {
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

        public void triggerSendNow(final Object argument) {
            listener.eventOccured(argument);
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

        final Receiver receiver = injector.getInstance(Receiver.class);
        final Sender sender = injector.getInstance(Sender.class);

        sender.triggerSendNow(message);
        assertThat(receiver.getMessages(), contains(message));

        injector.getInstance(EventCaster.class).unregister(receiver);

        sender.triggerSendNow(new Object());
        assertThat(receiver.getMessages(), contains(message));

    }
}
