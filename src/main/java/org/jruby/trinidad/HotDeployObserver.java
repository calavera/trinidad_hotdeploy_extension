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
            System.err.println("[ERROR] Unable to create the monitor file: " + monitor.getAbsolutePath());
            interrupted = true;
        }
    }

    public void observeAndRestart() {
        boolean monitorExists;
        try {
            monitorExists = monitor.exists();
        } catch (SecurityException  e) {
            // double check. Capistrano removes the parent directory temporarily
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                System.err.println("[ERROR] Thread interrupted");
                interrupted = true;
                return;
            }
            try {
                monitorExists = monitor.exists();
            } catch (SecurityException e1) {
                System.err.println("[WARNING] The monitor file doesn't exist: " + monitor.getAbsolutePath());
                System.err.println("[WARNING] Trying to check it again in " + delay + "ms");
                return;
            }
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
