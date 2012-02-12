package com.lexicalscope.eventcast;

import java.lang.reflect.Method;

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

interface EventCasterInternal extends EventCaster {
    /**
     * Register this a listener instance with the EventCaster
     * 
     * @param type
     *            the type of the listener
     * @param listener
     *            the listener to register
     */
    void registerListener(TypeLiteral<?> type, Object listener);

    /**
     * Fire an event to listeners of the given type
     * 
     * @param type
     *            the type of listeners that will be notified
     * @param method
     *            the method that will be notified
     * @param args
     *            the message that is being sent
     * 
     * @throws Throwable
     *             any exception
     */
    void fire(TypeLiteral<?> type, Method method, Object[] args) throws Throwable;
}
