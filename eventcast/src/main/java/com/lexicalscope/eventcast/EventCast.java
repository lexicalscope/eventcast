package com.lexicalscope.eventcast;

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

/**
 * Factory for the EventCaster
 * 
 * <ul>
 * <li>Register listener interfaces with the EventCaster using the module
 * builder</li>
 * <li>The module will detect if guice creates any instance that implements one
 * of the registered interfaces</li>
 * <li>The module will provide an implementation of the registered interface for
 * injection</li>
 * <li>If you call a method on the provided implementation, the same method will
 * be called on every other implementation of the interface</li>
 * </ul>
 * 
 * <p>
 * Please see the <a
 * href="https://github.com/lexicalscope/eventcast/wiki">wiki</a> for more
 * documentation
 * </p>
 * 
 * @author tim
 */
public class EventCast {
    /**
     * This module will bind the EventCaster and any listeners you bind to the
     * builder.
     * 
     * If you want to use the EventCaster in your Injector, you must install a
     * module built by this method exactly once.
     * 
     * @return A module builder that will bind the EventCaster and any listeners
     *         you specify
     */
    public static EventCastModuleBuilder eventCastModuleBuilder()
    {
        return new EventCastModuleBuilderImpl();
    }

    /**
     * This module will bind any listeners you bind to the builder. It will not
     * bind the EventCaster. You can use this method if you want to bind
     * listener interfaces in multiple modules without binding the EventCaster
     * more than once.
     * 
     * If you want to use the EventCaster in your Injector, you must install a
     * module built by the {@code eventCastModuleBuilder()} method as well.
     * 
     * @return A module builder that will bind only the listeners you specify
     */
    public static EventCastModuleBuilder eventCastBindingModuleBuilder()
    {
        return new EventCastBindingModuleBuilderImpl();
    }
}
