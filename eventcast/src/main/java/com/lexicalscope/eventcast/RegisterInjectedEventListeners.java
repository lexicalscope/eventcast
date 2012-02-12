package com.lexicalscope.eventcast;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;

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

final class RegisterInjectedEventListeners<I> implements InjectionListener<I> {
    private final TypeLiteral<?> interfaceType;
    private final Provider<EventCasterInternal> eventCasterProvider;

    protected RegisterInjectedEventListeners(
            final TypeLiteral<?> interfaceType,
            final Provider<EventCasterInternal> eventCasterProvider) {
        this.interfaceType = interfaceType;
        this.eventCasterProvider = eventCasterProvider;
    }

    @Override public void afterInjection(final Object injectee) {
        eventCasterProvider.get().registerListener(interfaceType, injectee);
    }

    @Override public boolean equals(final Object that) {
        if(that != null && this.getClass().equals(that.getClass()))
        {
            @SuppressWarnings("unchecked") final RegisterInjectedEventListeners<I> castedThat = (RegisterInjectedEventListeners<I>) that;
            return this.interfaceType.equals(castedThat.interfaceType) &&
                   this.eventCasterProvider.equals(castedThat.eventCasterProvider);
        }
        return false;
    }
}