package com.lexicalscope.eventcast;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.TypeLiteral;

/*
 * Copyright 2012 Tim Wood
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

class EventIntercepted implements Event {
    private final TypeLiteral<?> listenerType;
    private final Method method;
    private final Object[] args;
    private final MethodInterceptor interceptor;

    /**
     * An event
     *
     * @param type
     *            the type of listeners that will be notified
     * @param method
     *            the method that will be notified
     * @param args
     *            the message that is being sent
     * @param interceptor
     *            interceptor to apply to the event method invocations
     */
    EventIntercepted(
            final TypeLiteral<?> listenerType,
            final Method method,
            final Object[] args,
            final MethodInterceptor interceptor) {
        this.listenerType = listenerType;
        this.method = method;
        this.args = args;
        this.interceptor = interceptor;
    }

    public Object invoke(final Object object) throws Throwable {
        return interceptor.invoke(new MethodInvocation() {
            @Override public Object proceed() throws Throwable {
                return method.invoke(object, args);
            }

            @Override public Object getThis() {
                return object;
            }

            @Override public AccessibleObject getStaticPart() {
                return method;
            }

            @Override public Object[] getArguments() {
                return args;
            }

            @Override public Method getMethod() {
                return method;
            }
        });
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
