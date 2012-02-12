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

class EventDirect implements Event {
    final TypeLiteral<?> listenerType;
    final Method method;
    final Object[] args;

    /**
     * An event
     *
     * @param type
     *            the type of listeners that will be notified
     * @param method
     *            the method that will be notified
     * @param args
     *            the message that is being sent
     */
    public EventDirect(final TypeLiteral<?> listenerType, final Method method, final Object[] args) {
        this.listenerType = listenerType;
        this.method = method;
        this.args = args;
    }

    public Object invoke(final Object object) throws Exception {
        return method.invoke(object, args);
    }

    @Override public TypeLiteral<?> getListenerType() {
        return listenerType;
    }

    @Override public Method method() {
        return method;
    }

    @Override public Object[] args() {
        return args;
    }
}
