package com.lexicalscope.eventcast;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Provider;
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

public class TestRegisterInjectedEventListeners {
    @Rule public final JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock private Provider<EventCasterInternal> eventCasterProvider;
    @Mock private EventCasterInternal eventCaster;

    private final Object injectee = new Object();

    interface MyListener
    {

    }

    @Test public void injectedObjectIsRegisteredWithTheEventCaster()
    {
        context.checking(new Expectations() {{
            oneOf(eventCasterProvider).get(); will(returnValue(eventCaster));
            oneOf(eventCaster).registerListener(TypeLiteral.get(MyListener.class), injectee);
        }});

        new RegisterInjectedEventListeners<Object>(TypeLiteral.get(MyListener.class), eventCasterProvider).afterInjection(injectee);
    }
}
