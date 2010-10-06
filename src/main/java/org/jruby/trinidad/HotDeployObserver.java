package org.jruby.trinidad;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;

public class HotDeployObserver extends Thread {
    private final Context applicationContext;
    private final File monitor;
    private final long delay;

    private boolean interrupted;
    private long lastModified;

    public HotDeployObserver(Context applicationContext, String monitor, long delay) {
        super("TrinidadHotDeployObserver");
        this.applicationContext = applicationContext;
        this.monitor = new File(monitor);
        this.delay = delay;
        setDaemon(true);
    }

    public static void observe(Context applicationContext, String monitor, long delay) {
        HotDeployObserver observer = new HotDeployObserver(applicationContext, monitor, delay);
        observer.createMonitor();
        observer.start();
    }

    public void createMonitor() {
        try {
            monitor.createNewFile();
            lastModified = monitor.lastModified();
        } catch (IOException io) {
            System.err.println("Was not allowed to create the monitor: " + monitor.getAbsolutePath());
            interrupted = true;
        }
    }

    public void observeAndRestart() {
        boolean monitorExists;
        try {
            monitorExists = monitor.exists();
        } catch(SecurityException  e) {
            System.err.println("Was not allowed to check monitor existance: " + monitor.getAbsolutePath());
            interrupted = true;
            return;
        }

        if (monitorExists) {
            long last = monitor.lastModified();
            if (last > lastModified) {
                lastModified = last;
                restartApplicationContext();
            }
        } else {
            createMonitor();
        }
    }

    public void run() {
        while(!interrupted) {
          try {
            Thread.sleep(delay);
          } catch(InterruptedException e) {
          }
          observeAndRestart();
        }
    }

    private void restartApplicationContext() {
        interrupted = true;
        applicationContext.reload();
    }
}
