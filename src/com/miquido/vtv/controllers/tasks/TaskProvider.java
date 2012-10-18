package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;
import roboguice.inject.ContextScopedProvider;
import roboguice.inject.ContextSingleton;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
@ContextSingleton
public class TaskProvider<T extends SimpleRoboAsyncTask> implements Provider<T> {

    @Inject
    Context context;

    @Inject
    ContextScopedProvider<T> taskContextScopedProvider;

    @Override
    public T get() {
        return taskContextScopedProvider.get(context);
    }
}
