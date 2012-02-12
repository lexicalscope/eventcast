package com.lexicalscope.eventcast;

import java.util.concurrent.ExecutorService;

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

/**
 * Bind listener interfaces to the EventCaster
 *
 * @author tim
 */
public interface EventCastModuleBuilder {
    /**
     * The module should provide an implementation of the given listener
     *
     * @param listener
     *            the listener to implement
     *
     * @return this
     */
    EventCastModuleBuilder implement(Class<?> listener);

    /**
     * The module should provide an asynchronous implementation of the given listener
     *
     * @param listener
     *            the listener to implement
     *
     * @param executorService the executor that will dispatch the events
     *
     * @return this
     */
    EventCastModuleBuilder implement(Class<?> source, ExecutorService executorService);

    /**
     * The module should provide an asynchronous implementation of the given listener
     *
     * @param listener
     *            the listener to implement
     *
     * @return this
     */
    EventCastModuleBuilder implement(TypeLiteral<?> listener);

    /**
     * The module should provide an implementation of the given listener
     *
     * @param listener
     *            the listener to implement
     *
     * @param executorService the executor that will dispatch the events
     *
     * @return this
     */
    EventCastModuleBuilder implement(TypeLiteral<?> listener, ExecutorService executorService);

    /**
     * Provide a guice module that implements all the registered listeners
     *
     * @return a guice module that implements all the registered listeners
     */
    Module build();
}