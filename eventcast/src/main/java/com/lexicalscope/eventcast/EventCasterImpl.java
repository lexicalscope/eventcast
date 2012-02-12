package com.lexicalscope.eventcast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.TypeLiteral;

/*
 * Copyright 2011 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

class EventCasterImpl implements EventCasterInternal {
    private final ThreadLocal<List<Event>> pending = new ThreadLocal<List<Event>>();
    private final EventListenersMultimap listeners = new EventListenersMultimap();

    public EventCasterImpl() {
        registerListener(TypeLiteral.get(EventCaster.class), this);
    }

    @Override public void registerListener(final TypeLiteral<?> interfaceType, final Object injectee) {
        listeners.register(interfaceType, injectee);
    }

    @Override public void fire(final TypeLiteral<?> listenerType, final Method method, final Object[] args)
            throws Throwable {
        final Event event = new Event(listenerType, method, args);
        if (pending.get() == null) {
            final List<Event> pendingEvents = new LinkedList<Event>();
            pendingEvents.add(event);

            pending.set(pendingEvents);
            try {
                while (!pendingEvents.isEmpty()) {
                    broadcastEvent(pendingEvents.remove(0));
                }
            } finally {
                pending.set(null);
            }
        } else {
            pending.get().add(event);
        }
    }

    private void broadcastEvent(final Event event) throws Throwable {
        final List<Object> listenersForThisEvent = listeners.copyListenersFor(event.listenerType);
        for (final Object object : listenersForThisEvent) {
            try {
                event.method.invoke(object, event.args);
            } catch (final InvocationTargetException e) {
                fireExceptionDuringEventCast(event, object, e.getCause());
            } catch (final Exception e) {
                fireExceptionDuringEventCast(event, object, e);
            }
        }

        if (listenersForThisEvent.isEmpty()
                && !event.listenerType.equals(TypeLiteral.get(EventCastUnhandledListener.class)))
        {
            fireUnhandledEventCast(event);
        }
    }

    private void fireExceptionDuringEventCast(final Event event, final Object object, final Throwable e)
            throws Throwable,
            NoSuchMethodException {
        fire(
                TypeLiteral.get(EventCastingExceptionListener.class),
                EventCastingExceptionListener.class.getMethod(
                        "exceptionDuringEventCast",
                        new Class[] { Throwable.class, Type.class, Object.class, Method.class, Object[].class }),
                new Object[] { e, event.listenerType.getType(), object, event.method, event.args });
    }

    private void fireUnhandledEventCast(final Event event)
            throws Throwable,
            NoSuchMethodException {
        fire(
                TypeLiteral.get(EventCastUnhandledListener.class),
                EventCastUnhandledListener.class.getMethod(
                        "unhandledEventCast",
                        new Class[] { Type.class, Method.class, Object[].class }),
                new Object[] { event.listenerType.getType(), event.method, event.args });
    }

    @Override public void unregister(final Object listener) {
        listeners.unregister(listener);
    }

    @Override public void unregister(final TypeLiteral<?> type, final Object listener) {
        listeners.unregister(type, listener);
    }

    @Override public void unregister(final Class<?> type, final Object listener) {
        unregister(TypeLiteral.get(type), listener);
    }
}
