package com.lexicalscope.eventcast;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.lexicalscope.eventcast.SendEventWithGuice.MyEventListener;

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

public class EventCastModuleBuilder {
    private final Set<TypeLiteral<?>> bindings = new LinkedHashSet<TypeLiteral<?>>();

    public EventCastModuleBuilder implement(final Class<MyEventListener> source) {
        return implement(TypeLiteral.get(source));
    }

    public EventCastModuleBuilder implement(final TypeLiteral<?> typeLiteral) {
        bindings.add(typeLiteral);
        return this;
    }

    public Module build() {
        return new AbstractModule() {
            private final EventCaster eventCaster = new EventCaster();

            @Override protected void configure() {
                bindListener(Matchers.any(), new TypeListener() {
                    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
                        final Class<? super I> rawType = type.getRawType();
                        for (final Class<?> interfaceClass : rawType.getInterfaces()) {
                            final TypeLiteral<?> interfaceType = type.getSupertype(interfaceClass);
                            if (bindings.contains(interfaceType))
                            {
                                encounter.register(new InjectionListener<I>() {
                                    public void afterInjection(final Object injectee) {
                                        eventCaster.addListener(interfaceType, injectee);
                                    }
                                });
                            }
                        }
                    }
                });

                for (final TypeLiteral<?> listener : bindings) {
                    bindEventCast(listener);
                }
            }

            private <T> void bindEventCast(final TypeLiteral<T> listener) {
                bind(listener).toProvider(new EventCastProvider<T>(listener, eventCaster));
            }
        };
    }
}
