package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.AsyncTask;
import roboguice.RoboGuice;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 10.08.12
 * Time: 01:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleRoboAsyncTask extends AsyncTask<Void,  Void, Void> {
    final protected Context context;
    private Exception exception;
    private Throwable throwable;

    protected SimpleRoboAsyncTask(Context context) {
        this.context = context;
        RoboGuice.getInjector(context).injectMembers(this);
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exception!=null) {
            onException(exception);
        } else if (throwable!=null) {
            onThrowable(throwable);
        } else {
            onSuccess();
        }
        onFinal();
    }



    @Override
    protected Void doInBackground(Void... voids) {
        try {
            doInBackground();
        } catch (Exception e) {
            synchronized (this) {
                this.exception = e;
            }
        } catch (Throwable t) {
            synchronized (this) {
                this.throwable = t;
            }
        }
        return null;
    }

    protected abstract void doInBackground() throws Exception;

    protected void onException(Exception e) {
    }
    protected void onThrowable(Throwable t) {
    }
    protected void onFinal() {
    }
    protected void onSuccess() {
    }

}
