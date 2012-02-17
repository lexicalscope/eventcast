package com.lexicalscope.eventcast;

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
		Class<? super I> rawType = type.getRawType();
		while (rawType != null) {
			for (final Class<?> interfaceClass : rawType.getInterfaces()) {
				final TypeLiteral<?> interfaceType = type.getSupertype(interfaceClass);
				if (bindings.contains(interfaceType))
				{
					final Provider<EventCasterInternal> eventCasterProvider =
							encounter.getProvider(EventCasterInternal.class);
					encounter.register(new RegisterInjectedEventListeners<I>(interfaceType, eventCasterProvider));
				}
			}
			rawType = rawType.getSuperclass();
		}
	}
}