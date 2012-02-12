package com.lexicalscope.eventcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
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

class EventListenersMultimap {
    private final Object monitor = new Object();
    private final SetMultimap<TypeLiteral<?>, Object> listeners = LinkedHashMultimap
            .<TypeLiteral<?>, Object>create();

    void register(final TypeLiteral<?> interfaceType, final Object injectee) {
        synchronized (monitor) {
            listeners.put(interfaceType, injectee);
        }
    }

    List<Object> copyListenersFor(final TypeLiteral<?> listenerType) {
        synchronized (monitor) {
            return new ArrayList<Object>(listeners.get(listenerType));
        }
    }

    void unregister(final Object listener) {
        synchronized (monitor) {
            final Collection<Object> listenerObjects = listeners.values();
            while (listenerObjects.remove(listener))
            {}
        }
    }

    void unregister(final TypeLiteral<?> type, final Object listener) {
        synchronized (monitor) {
            listeners.remove(type, listener);
        }
    }
}
