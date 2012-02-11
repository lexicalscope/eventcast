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

public class TestUnregisterOfOneInterfaceStopsEventsBeingSent
{
    public interface MyEventListenerOne {
        void eventOccured(Object argument);
    }

    public interface MyEventListenerTwo {
        void eventOccured(Object argument);
    }

    public static class Receiver implements MyEventListenerOne, MyEventListenerTwo {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> getMessages() {
            return messages;
        }

        public void eventOccured(final Object message) {
            messages.add(message);
        }
    }

    public static class Sender {
        private final MyEventListenerOne listenerOne;
        private final MyEventListenerTwo listenerTwo;

        @Inject public Sender(
                final MyEventListenerOne listenerOne,
                final MyEventListenerTwo listenerTwo) {
            this.listenerOne = listenerOne;
            this.listenerTwo = listenerTwo;
        }

        public void triggerSendOneNow(final Object argument) {
            listenerOne.eventOccured(argument);
        }

        public void triggerSendTwoNow(final Object argument) {
            listenerTwo.eventOccured(argument);
        }
    }

    @org.junit.Test public void canUnregisterListenerFromSpecificInterface() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(eventCastModuleBuilder().
                        implement(MyEventListenerOne.class).
                        implement(MyEventListenerTwo.class).build());
            }
        });

        final Object firstMessageOne = new Object();
        final Object firstMessageTwo = new Object();
        final Object secondMessageOne = new Object();
        final Object secondMessageTwo = new Object();

        final Receiver receiver = injector.getInstance(Receiver.class);
        final Sender sender = injector.getInstance(Sender.class);

        sender.triggerSendOneNow(firstMessageOne);
        sender.triggerSendTwoNow(firstMessageTwo);
        assertThat(receiver.getMessages(), contains(firstMessageOne, firstMessageTwo));

        injector.getInstance(EventCaster.class).unregister(MyEventListenerOne.class, receiver);

        sender.triggerSendOneNow(secondMessageOne);
        sender.triggerSendTwoNow(secondMessageTwo);

        assertThat(receiver.getMessages(), contains(firstMessageOne, firstMessageTwo, secondMessageTwo));
    }

    @org.junit.Test public void canUnregisterListenerFromAllInterfaces() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(eventCastModuleBuilder().
                        implement(MyEventListenerOne.class).
                        implement(MyEventListenerTwo.class).build());
            }
        });

        final Object firstMessageOne = new Object();
        final Object firstMessageTwo = new Object();
        final Object secondMessageOne = new Object();
        final Object secondMessageTwo = new Object();

        final Receiver receiver = injector.getInstance(Receiver.class);
        final Sender sender = injector.getInstance(Sender.class);

        sender.triggerSendOneNow(firstMessageOne);
        sender.triggerSendTwoNow(firstMessageTwo);
        assertThat(receiver.getMessages(), contains(firstMessageOne, firstMessageTwo));

        injector.getInstance(EventCaster.class).unregister(receiver);

        sender.triggerSendOneNow(secondMessageOne);
        sender.triggerSendTwoNow(secondMessageTwo);

        assertThat(receiver.getMessages(), contains(firstMessageOne, firstMessageTwo));
    }
}
