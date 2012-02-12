package com.lexicalscope.eventcast;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/*
 * Copyright 2012 Tim Wood
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

class AsyncMethodInterceptor implements MethodInterceptor {
    private final ExecutorService executorService;

    public AsyncMethodInterceptor(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override public Object invoke(final MethodInvocation invocation) throws Throwable {
        executorService.submit(new Callable<Object>() {
            @Override public Object call() throws Exception {
                try {
                    return invocation.proceed();
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            }});
        return null;
    }
}
