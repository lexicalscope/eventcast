package com.lexicalscope.eventcast;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;

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

public class TestEventListenerGuiceTypeListener {
	@Rule public final MyJUnitRuleMockery context = new MyJUnitRuleMockery();

	private final Set<TypeLiteral<?>> bindings = new HashSet<TypeLiteral<?>>();

	@Mock private Provider<EventCasterInternal> eventCasterProvider;

	interface MyListener
	{

	}

	static class MyListenerImpl implements MyListener
	{

	}

	@Test public void typesThatImplementARegisteredListenerInterfaceAreMonitoredForInjection()
	{
		final TypeEncounter<MyListenerImpl> encounter = context.mock(new TypeLiteral<TypeEncounter<MyListenerImpl>>(){});

		bindings.add(TypeLiteral.get(MyListener.class));

		context.checking(new Expectations() {{
			oneOf(encounter).getProvider(EventCasterInternal.class); will(returnValue(eventCasterProvider));
			oneOf(encounter).register(new RegisterInjectedEventListeners<Object>(TypeLiteral.get(MyListener.class), eventCasterProvider));
		}});

		new EventListenerGuiceTypeListener(bindings).hear(TypeLiteral.get(MyListenerImpl.class), encounter);
	}

	static class MyClassWhichExtendsMyListenerImpl extends MyListenerImpl
	{
	}

	@Test public void typesThatHaveASuperClassWhichImplementARegisteredListenerInterfaceAreMonitoredForInjection() throws Exception
	{
		final TypeEncounter<MyClassWhichExtendsMyListenerImpl> encounter = context.mock(new TypeLiteral<TypeEncounter<MyClassWhichExtendsMyListenerImpl>>(){});

		bindings.add(TypeLiteral.get(MyListener.class));

		context.checking(new Expectations() {{
			oneOf(encounter).getProvider(EventCasterInternal.class); will(returnValue(eventCasterProvider));
			oneOf(encounter).register(new RegisterInjectedEventListeners<Object>(TypeLiteral.get(MyListener.class), eventCasterProvider));
		}});

		new EventListenerGuiceTypeListener(bindings).hear(TypeLiteral.get(MyClassWhichExtendsMyListenerImpl.class), encounter);
	}

}
