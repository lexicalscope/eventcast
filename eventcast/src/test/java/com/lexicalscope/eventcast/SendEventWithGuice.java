package com.lexicalscope.eventcast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class SendEventWithGuice
{
    public interface MyEventListener {
        void eventOccured();
    }

    public static class ReceiverImpl implements Receiver, MyEventListener {
        private boolean gotEvent;

        public boolean gotEvent() {
            return gotEvent;
        }

        public void eventOccured() {
            gotEvent = true;
        }
    }

    public static class SenderImpl implements Sender {
        private final MyEventListener listener;

        @Inject public SenderImpl(final MyEventListener listener) {
            this.listener = listener;
        }

        public void triggerSendNow() {
            listener.eventOccured();
        }
    }

    public interface Receiver {

    }

    public interface Sender {

    }

    @org.junit.Test public void canSendAndEventToListener() throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(Sender.class).to(SenderImpl.class);
                bind(Receiver.class).to(ReceiverImpl.class);
                install(new EventCastModuleBuilder().implement(MyEventListener.class).build());
            }
        });

        final ReceiverImpl receiver = injector.getInstance(ReceiverImpl.class);
        injector.getInstance(SenderImpl.class).triggerSendNow();
        assertThat(receiver.gotEvent(), equalTo(true));
    }
}
