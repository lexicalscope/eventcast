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
    public static EventCastModuleBuilder eventCastModuleBuilder()
    {
        return new EventCastModuleBuilderImpl();
    }
}
