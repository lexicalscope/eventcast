package com.lexicalscope.eventcast;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
 * Implement this interface to be notified of events that are not handled by any
 * listener
 * 
 * @author tim
 */
public interface EventCastUnhandledListener {

    /**
     * Called when an event is not handled by any other listener
     * 
     * @param listenerType
     *            the type of listener that is missing
     * @param listenerMethod
     *            the method that would have been called
     * @param args
     *            the message that would have been sent to the listener
     */
    void unhandledEventCast(
            Type listenerType,
            Method listenerMethod,
            Object[] args);
}
