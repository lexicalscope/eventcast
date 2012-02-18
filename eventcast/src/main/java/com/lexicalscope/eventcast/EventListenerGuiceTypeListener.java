package com.lexicalscope.eventcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

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

final class EventListenerGuiceTypeListener implements TypeListener {
	private final Set<TypeLiteral<?>> bindings;

	EventListenerGuiceTypeListener(final Set<TypeLiteral<?>> bindings) {
		this.bindings = bindings;
	}

    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
        for (final Class<?> interfaceClass : getAllInterfacesInInheritanceHierarchy(type.getRawType())) {
            registerInterfaceIfInBindings(type, encounter, interfaceClass);
        }
    }

    private <I> void registerInterfaceIfInBindings(final TypeLiteral<I> type,
            final TypeEncounter<I> encounter, final Class<?> interfaceClass)
    {
        final TypeLiteral<?> interfaceType = type.getSupertype(interfaceClass);
        if (bindings.contains(interfaceType))
        {
            final Provider<EventCasterInternal> eventCasterProvider =
                    encounter.getProvider(EventCasterInternal.class);
            encounter.register(new RegisterInjectedEventListeners<I>(interfaceType, eventCasterProvider));
        }
    }

    private Collection<Class<?>> getAllInterfacesInInheritanceHierarchy(Class<?> rawType) {
        final List<Class<?>> interfaces = new ArrayList<Class<?>>();
        while (rawType != null) {
            interfaces.addAll(listAllInterfacesInTree(rawType.getInterfaces()));
            rawType = rawType.getSuperclass();
        }
        return interfaces;
    }

    private Collection<? extends Class<?>> listAllInterfacesInTree(final Class<?>[] rawInterfaces)
    {
        final List<Class<?>> interfaces = new ArrayList<Class<?>>();
        for (final Class<?> interfaceType : rawInterfaces) {
            interfaces.add(interfaceType);
            interfaces.addAll(listAllInterfacesInTree(interfaceType.getInterfaces()));
        }
        return interfaces;
    }

}