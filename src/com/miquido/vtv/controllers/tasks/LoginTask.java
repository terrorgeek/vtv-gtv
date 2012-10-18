package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.SessionService;
import com.miquido.vtv.events.modelchanges.SessionModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class LoginTask extends SimpleRoboAsyncTask {

    private static final Logger logger = LoggerFactory.getLogger(LoginTask.class);

    @Inject
    private SessionService sessionService;

    @Inject
    private EventManager eventManager;

    @Inject
    TaskProvider<SessionInitializationTask> sessionInitializationTaskTaskProvider;

    @Inject
    public LoginTask(Context context) {
        super(context);
        logger.debug("LoginTask constructor.");
        logger.debug("contex:"+context);
    }


    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            logger.debug("onPreExecute: sessionService: {}", sessionService);
            logger.debug("onPreExecute: Event Manager: " + eventManager);
            sessionService.setLoggingIn();
            eventManager.fire(new SessionModelChanged(sessionService));
            logger.debug("onPreExecute: SessionModelChanged Event fired");
        } catch (Exception e) {
            logger.error("po ustawieniu stanu: wyjatek: "+e.getMessage());
        }
    }

    @Override
    public void doInBackground () throws Exception {
        logger.debug("call: Logging in start");
        sessionService.login();
        logger.debug("call: logged in");
    }


    @Override
    protected void onFinal() throws RuntimeException {
        logger.debug("onFinally: fire event");
        eventManager.fire(new SessionModelChanged(sessionService));
        logger.debug("onFinally: SessionModelChanged Event fired");
        sessionInitializationTaskTaskProvider.get().execute();
    }

}
