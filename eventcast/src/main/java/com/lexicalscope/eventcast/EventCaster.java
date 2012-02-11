package com.lexicalscope.eventcast;

import static com.google.common.collect.Multimaps.synchronizedSetMultimap;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
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

class EventCaster {
    private final SetMultimap<Object, Object> listeners = synchronizedSetMultimap(LinkedHashMultimap.create());

    void addListener(final TypeLiteral<?> interfaceType, final Object injectee) {
        listeners.put(interfaceType, injectee);
    }

    void fire(final TypeLiteral<?> listenerType, final Method method, final Object[] args) throws Throwable
    {
        for (final Object object : new ArrayList<Object>(listeners.get(listenerType))) {
            method.invoke(object, args);
        }
    }
}
