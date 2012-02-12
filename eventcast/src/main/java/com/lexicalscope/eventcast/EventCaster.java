package com.lexicalscope.eventcast;

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

/**
 * The event caster is responsible for broadcasting the events
 * 
 * @author tim
 */
public interface EventCaster {
    /**
     * Unregister this listener from all events
     * 
     * @param listener
     *            the listener to unregister
     */
    void unregister(Object listener);

    /**
     * Unregister this listener from specific event
     * 
     * @param type
     *            the type of the interface the listener will not longer be
     *            notified of events to
     * @param listener
     *            the listener to unregister
     */
    void unregister(TypeLiteral<?> type, Object listener);

    /**
     * Unregister this listener from specific event
     * 
     * @param type
     *            the type of the interface the listener will not longer be
     *            notified of events to
     * @param listener
     *            the listener to unregister
     */
    void unregister(Class<?> type, Object listener);
}
