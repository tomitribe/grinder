package com.tomitribe.performance.scriptengine.crest;

import net.grinder.common.GrinderProperties;
import net.grinder.script.Grinder;
import net.grinder.script.InvalidContextException;
import net.grinder.script.NonInstrumentableTypeException;
import net.grinder.script.Test;
import org.tomitribe.crest.cmds.Cmd;
import org.tomitribe.crest.cmds.processors.Commands;
import org.tomitribe.crest.cmds.utils.CommandLine;
import org.tomitribe.crest.contexts.DefaultsContext;
import org.tomitribe.crest.contexts.SystemPropertiesDefaultsContext;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCrestTestRunner implements Runnable {

    private static final Map<String, Cmd> commands = new ConcurrentHashMap<String, Cmd>();

    private final List<TestCommand> tests = new ArrayList<TestCommand>();

    static {
        final DefaultsContext defaultsContext = new SystemPropertiesDefaultsContext();
        final Iterable<Class<?>> classes = Commands.load();

        for (final Class clazz : classes) {
            commands.putAll(Commands.get(clazz, defaultsContext));
        }
    }

    public DefaultCrestTestRunner() {

        final GrinderProperties properties = Grinder.grinder.getProperties();
        final Enumeration<?> propertyNames = properties.propertyNames();
        int testCount = 1;

        while (propertyNames.hasMoreElements()) {
            final String propertyName = (String) propertyNames.nextElement();
            final String propertyValue = properties.getProperty(propertyName);

            tests.add(new TestCommand(testCount, propertyName, propertyValue));
            testCount++;
        }
    }

    @Override
    public void run() {
        final int threadNumber = Grinder.grinder.getThreadNumber();

        final int index = threadNumber % tests.size();
        final TestCommand testCommand = tests.get(index);

        try {
            testCommand.run();
        } catch (Throwable t) {
            fail();
        }
    }

    private void fail() {
        try {
            Grinder.grinder.getStatistics().getForLastTest().setSuccess(false);
        } catch (InvalidContextException e) {
            //ignore
        }
    }

    private class CmdInstrumentationFilter implements Test.InstrumentationFilter {
        @Override
        public boolean matches(Object item) {
            if (Method.class.isAssignableFrom(item.getClass())) {
                final Method method = Method.class.cast(item);
                return ("exec".equals(method.getName()));
            }

            return false;
        }
    }

    private class TestCommand {

        private final Test test;
        private final Cmd cmd;
        private final String[] args;

        public TestCommand(int testNumber, String testName, String commandLine) {

            final Test test = new Test(testNumber, testName);

            final List<String> list = Arrays.asList(CommandLine.translateCommandline(commandLine));
            final String commandName = list.remove(0);
            final String[] args = list.toArray(new String[list.size()]);

            final Cmd cmd = new WrapperCmd(commands.get(commandName));
            try {
                test.record(cmd, new CmdInstrumentationFilter());
            } catch (NonInstrumentableTypeException e) {
                e.printStackTrace();
            }

            this.test = test;
            this.cmd = cmd;
            this.args = args;
        }

        public Test getTest() {
            return test;
        }

        public Cmd getCmd() {
            return cmd;
        }

        public String[] getArgs() {
            return args;
        }

        public void run() {
            cmd.exec(args);
        }
    }

    private class WrapperCmd implements Cmd {
        private final Cmd cmd;

        public WrapperCmd(Cmd cmd) {
            this.cmd = cmd;
        }

        @Override
        public String getUsage() {
            return cmd.getUsage();
        }

        @Override
        public String getName() {
            return cmd.getName();
        }

        @Override
        public Object exec(String... rawArgs) {
            return cmd.exec(rawArgs);
        }

        @Override
        public void help(PrintStream out) {
            cmd.help(out);
        }

        @Override
        public Collection<String> complete(String buffer, int cursorPosition) {
            return cmd.complete(buffer, cursorPosition);
        }
    }
}
