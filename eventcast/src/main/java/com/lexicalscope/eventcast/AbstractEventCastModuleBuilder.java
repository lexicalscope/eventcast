package com.lexicalscope.eventcast;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.aopalliance.intercept.MethodInterceptor;

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

abstract class AbstractEventCastModuleBuilder implements EventCastModuleBuilder {
    private final Map<TypeLiteral<?>, MethodInterceptor> bindings = new LinkedHashMap<TypeLiteral<?>, MethodInterceptor>();

    @Override public EventCastModuleBuilder implement(final Class<?> source) {
        return implement(TypeLiteral.get(source));
    }

    @Override public EventCastModuleBuilder implement(final Class<?> source, final ExecutorService executorService) {
        return implement(TypeLiteral.get(source), new AsyncMethodInterceptor(executorService));
    }

    @Override public EventCastModuleBuilder implement(final TypeLiteral<?> typeLiteral) {
        return implement(typeLiteral, new NullMethodInterceptor());
    }

    @Override public EventCastModuleBuilder implement(final TypeLiteral<?> typeLiteral, final ExecutorService executorService) {
        return implement(typeLiteral, new AsyncMethodInterceptor(executorService));
    }

    private EventCastModuleBuilder implement(final TypeLiteral<?> typeLiteral, final MethodInterceptor interceptor) {
        bindings.put(typeLiteral, interceptor);
        return this;
    }

    protected LinkedHashMap<TypeLiteral<?>, MethodInterceptor> bindings() {
        return new LinkedHashMap<TypeLiteral<?>, MethodInterceptor>(bindings);
    }
}
