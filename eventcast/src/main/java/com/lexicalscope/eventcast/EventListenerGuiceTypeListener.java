package com.lexicalscope.eventcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
        Provider<EventCasterInternal> eventCasterProvider = null;

        for (final Class<?> interfaceClass : interfacesInInheritanceHierarchy(type.getRawType())) {
            final TypeLiteral<?> interfaceType = type.getSupertype(interfaceClass);
            if (bindings.contains(interfaceType))
            {
                if(eventCasterProvider == null)
                {
                    eventCasterProvider = encounter.getProvider(EventCasterInternal.class);
                }
                encounter.register(new RegisterInjectedEventListeners<I>(interfaceType, eventCasterProvider));
            }
        }
    }

    private Collection<Class<?>> interfacesInInheritanceHierarchy(final Class<?> rawType) {
        return new ArrayList<Class<?>>(listInterfaces(rawType, new HashSet<Class<?>>()));
    }

    private Set<Class<?>> listInterfaces(Class<?> rawType, final Set<Class<?>> interfaces) {
        while(rawType != null)
        {
            for (final Class<?> interfac3 : rawType.getInterfaces()) {
                if (interfaces.add(interfac3)) {
                    listInterfaces(interfac3, interfaces);
                }
            }
            rawType = rawType.getSuperclass();
        }
        return interfaces;
    }
}