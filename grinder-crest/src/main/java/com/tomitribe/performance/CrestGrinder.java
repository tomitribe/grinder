package com.tomitribe.performance;

import net.grinder.Grinder;
import net.grinder.util.weave.agent.ExposeInstrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.util.JarLocation;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrestGrinder {

    final static Logger logger = LoggerFactory.getLogger("agent");

    public static void main(String[] args) {

        // construct a classloader
        try {
            final List<URL> urlList = new ArrayList<URL>();
            final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            if (URLClassLoader.class.isAssignableFrom(currentClassLoader.getClass())) {
                final URLClassLoader urlClassLoader = URLClassLoader.class.cast(currentClassLoader);
                urlList.addAll(Arrays.asList(urlClassLoader.getURLs()));
            }

            final String javaHome = System.getProperty("java.home");
            final String toolsJar = new File(javaHome, ".." + File.separator + "lib" + File.separator + "tools.jar").getAbsolutePath();
            urlList.add(new URL("file", "", toolsJar));

            // use the new classloader
            final ClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]), ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load java agent
        final Class<?> reference = CrestGrinder.class;
        final File file = JarLocation.jarLocation(reference);
        final String agentPath = file.getAbsolutePath();
        final String pid = getPid();

        try {
            final Class<?> vmClass = Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
            final Method attachMethod = vmClass.getMethod("attach", String.class);
            final Method loadAgentMethod = vmClass.getMethod("loadAgent", String.class);

            // attach to the vm
            final Object vm = attachMethod.invoke(null, new String[]{pid});

            // load our agent
            loadAgentMethod.invoke(vm, agentPath);
        } catch (final ClassNotFoundException e) {
            // not a Sun VM
        } catch (final NoSuchMethodException e) {
            // not a Sun VM
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (ExposeInstrumentation.getInstrumentation() == null) {
            logger.info("Unable to dynamically load the java agent");
        }

        // config self as the Grinder script to run
        System.setProperty("grinder.script", file.getAbsolutePath());

        // delegate to Grinder
        Grinder.main(args);
    }

    private static String getPid() {
        // This relies on the undocumented convention of the
        // RuntimeMXBean's name starting with the PID, but
        // there appears to be no other way to obtain the
        // current process' id, which we need for the attach
        // process
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String pid = bean.getName();
        if (pid.contains("@")) {
            pid = pid.substring(0, pid.indexOf("@"));
        }
        return pid;
    }
}
