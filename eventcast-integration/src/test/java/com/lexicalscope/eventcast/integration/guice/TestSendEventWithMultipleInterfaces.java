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

public class TestSendEventWithMultipleInterfaces
{
    public interface MyEventListenerOne {
        void eventOneOccured(Object message);
    }

    public interface MyEventListenerTwo {
        void eventTwoOccured(Object message);
    }

    public static class Receiver implements MyEventListenerOne, MyEventListenerTwo {
        private final List<Object> messages = new ArrayList<Object>();

        public List<Object> gotMessages() {
            return messages;
        }

        public void eventOneOccured(final Object message) {
            messages.add(message);
        }

        public void eventTwoOccured(final Object message) {
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

        public void triggerSendOneNow(final Object message) {
            listenerOne.eventOneOccured(message);
        }

        public void triggerSendTwoNow(final Object message) {
            listenerTwo.eventTwoOccured(message);
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class);
                bind(Receiver.class);
                install(eventCastModuleBuilder().
                        implement(MyEventListenerOne.class).
                        implement(MyEventListenerTwo.class).build());
            }
        });

        final Object messageOne = new Object();
        final Object messageTwo = new Object();

        final Receiver receiver = injector.getInstance(Receiver.class);
        final Sender sender = injector.getInstance(Sender.class);

        sender.triggerSendOneNow(messageOne);
        sender.triggerSendTwoNow(messageTwo);

        assertThat(receiver.gotMessages(), contains(messageOne, messageTwo));
    }
}
