package com.lexicalscope.eventcast;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.inject.Module;
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

class EventCastBindingModuleBuilderImpl implements EventCastModuleBuilder {
    private final Set<TypeLiteral<?>> bindings = new LinkedHashSet<TypeLiteral<?>>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lexicalscope.eventcast.EventCastModuleBuilder#implement(java.lang
     * .Class)
     */
    @Override public EventCastModuleBuilder implement(final Class<?> source) {
        return implement(TypeLiteral.get(source));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lexicalscope.eventcast.EventCastModuleBuilder#implement(com.google
     * .inject.TypeLiteral)
     */
    @Override public EventCastModuleBuilder implement(final TypeLiteral<?> typeLiteral) {
        bindings.add(typeLiteral);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.lexicalscope.eventcast.EventCastModuleBuilder#build()
     */
    @Override public Module build() {
        return new EventCastBindingModule(new LinkedHashSet<TypeLiteral<?>>(bindings));
    }
}
