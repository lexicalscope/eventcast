package com.lexicalscope.eventcast;

import java.util.Map;
import java.util.Map.Entry;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

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

final class EventCastBindingModule extends AbstractModule {
     private final Map<TypeLiteral<?>, MethodInterceptor> bindings;

    public EventCastBindingModule(final Map<TypeLiteral<?>, MethodInterceptor> bindings) {
        this.bindings = bindings;
    }

    @Override protected void configure() {
        bindListener(Matchers.any(), new EventListenerGuiceTypeListener(bindings.keySet()));

        for (final Entry<TypeLiteral<?>, MethodInterceptor> listener : bindings.entrySet()) {
            bindEventCast(listener.getKey(), listener.getValue());
        }
    }

    private <T> void bindEventCast(final TypeLiteral<T> listener, final MethodInterceptor interceptor) {
        bind(listener).toProvider(new EventCastProvider<T>(listener, getProvider(EventCasterInternal.class), interceptor));
    }
}