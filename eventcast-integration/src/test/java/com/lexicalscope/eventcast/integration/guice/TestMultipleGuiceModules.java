package com.lexicalscope.eventcast.integration.guice;

import static com.lexicalscope.eventcast.EventCast.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.lexicalscope.eventcast.EventCaster;

public class TestMultipleGuiceModules
{
    public interface MyEventListener1 {
        void eventOccured(Object argument);
    }

    public static class Receiver1 implements MyEventListener1 {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> getMessages() {
            return messages;
        }

        public void eventOccured(final Object message) {
            messages.add(message);
        }
    }

    public static class Sender1 {
        private final MyEventListener1 listener;

        @Inject public Sender1(final MyEventListener1 listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final Object argument) {
            listener.eventOccured(argument);
        }
    }

    public interface MyEventListener2 {
        void eventOccured(Object argument);
    }

    public static class Receiver2 implements MyEventListener2 {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> getMessages() {
            return messages;
        }

        public void eventOccured(final Object message) {
            messages.add(message);
        }
    }

    public static class Sender2 {
        private final MyEventListener2 listener;

        @Inject public Sender2(final MyEventListener2 listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final Object argument) {
            listener.eventOccured(argument);
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender1.class);
                bind(Receiver1.class);
                install(eventCastModuleBuilder().implement(MyEventListener1.class).build());
            }
        }, new AbstractModule() {
            @Override protected void configure() {
                bind(Sender2.class);
                bind(Receiver2.class);
                install(eventCastBindingModuleBuilder().implement(MyEventListener2.class).build());
            }
        });

        final Object message = new Object();

        final Receiver1 receiver = injector.getInstance(Receiver1.class);
        final Sender1 sender = injector.getInstance(Sender1.class);

        sender.triggerSendNow(message);
        assertThat(receiver.getMessages(), contains(message));

        final Receiver2 receiver2 = injector.getInstance(Receiver2.class);
        final Sender2 sender2 = injector.getInstance(Sender2.class);

        sender2.triggerSendNow(message);
        assertThat(receiver2.getMessages(), contains(message));

        injector.getInstance(EventCaster.class).unregister(receiver);

        sender.triggerSendNow(new Object());
        assertThat(receiver.getMessages(), contains(message));

    }
}
