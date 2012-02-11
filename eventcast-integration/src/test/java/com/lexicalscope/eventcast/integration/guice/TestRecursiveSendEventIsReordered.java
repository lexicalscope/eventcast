package com.lexicalscope.eventcast.integration.guice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.lexicalscope.eventcast.EventCastModuleBuilder;

public class TestRecursiveSendEventIsReordered
{
    public interface FirstEventListener {
        void eventOccured(String message);
    }

    public interface SecondEventListener {
        void eventOccured(String message);
    }

    @Singleton public static class MessageSequence
    {
        private final List<String> messages = new ArrayList<String>();

        public List<String> getMessages() {
            return messages;
        }

        public void messageOccured(final String message) {
            messages.add(message);
        }
    }

    public static class FirstReceiver implements FirstEventListener {
        private final MessageSequence sequence;

        @Inject public FirstReceiver(final MessageSequence sequence) {
            this.sequence = sequence;
        }

        public void eventOccured(final String message) {
            sequence.messageOccured(message);
        }
    }

    public static class SecondReceiver implements SecondEventListener {
        private final MessageSequence sequence;

        @Inject public SecondReceiver(final MessageSequence sequence) {
            this.sequence = sequence;
        }

        public void eventOccured(final String message) {
            sequence.messageOccured(message);
        }
    }

    public static class ReceiverThatSendsFromFirstToSecond implements FirstEventListener {
        private final MessageSequence sequence;
        private final SecondEventListener secondEventSender;

        @Inject public ReceiverThatSendsFromFirstToSecond(
                final MessageSequence sequence,
                final SecondEventListener secondEventSender) {
            this.sequence = sequence;
            this.secondEventSender = secondEventSender;
        }

        public void eventOccured(final String message) {
            sequence.messageOccured(message);
            secondEventSender.eventOccured("nested send");
        }
    }

    public static class Sender {
        private final FirstEventListener listener;

        @Inject public Sender(final FirstEventListener listener) {
            this.listener = listener;
        }

        public void triggerSendNow(final String message) {
            listener.eventOccured(message);
        }
    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MessageSequence.class);
                bind(Sender.class);
                bind(FirstReceiver.class);
                bind(SecondReceiver.class);
                bind(ReceiverThatSendsFromFirstToSecond.class);
                install(new EventCastModuleBuilder().
                        implement(FirstEventListener.class).
                        implement(SecondEventListener.class).build());
            }
        });

        injector.getInstance(ReceiverThatSendsFromFirstToSecond.class);
        injector.getInstance(FirstReceiver.class);
        injector.getInstance(SecondReceiver.class);

        injector.getInstance(Sender.class).triggerSendNow("original message");

        final MessageSequence messageSequence = injector.getInstance(MessageSequence.class);

        System.out.println(messageSequence.getMessages());
        assertThat(messageSequence.getMessages(), contains("original message", "original message", "nested send"));
    }
}
