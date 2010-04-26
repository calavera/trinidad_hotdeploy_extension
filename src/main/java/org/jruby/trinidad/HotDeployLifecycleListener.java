package org.jruby.trinidad;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;


class HotDeployLifecycleListener implements LifecycleListener {

    public final Context applicationContext;
    public final String monitor;
    public final long delay;

    public HotDeployLifecycleListener(Context applicationContext, String monitor, long delay) {
        this.applicationContext = applicationContext;
        this.monitor = new File(monitor).getAbsolutePath();
        this.delay = delay;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_START_EVENT.equals(event.getType())) {
            init();
        }
    }

    private void init() {
        HotDeployObserver.observe(applicationContext, monitor, delay);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                //stop the observer
            }
        });
    }
}
