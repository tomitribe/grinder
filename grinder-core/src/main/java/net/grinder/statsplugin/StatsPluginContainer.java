/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package net.grinder.statsplugin;

import net.grinder.engine.common.EngineException;
import net.grinder.statistics.StatisticsServices;
import net.grinder.statistics.TestStatisticsMap;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;

import java.util.List;

import static net.grinder.util.ClassLoaderUtilities.loadRegisteredImplementations;

public class StatsPluginContainer {

    private static final StatsPluginContainer instance = new StatsPluginContainer();
    private static final String RESOURCE_NAME = "META-INF/net.grinder.statsplugin";

    private final MutablePicoContainer container = new DefaultPicoContainer(new Caching());


    private StatsPluginContainer() {
        try {
            final List<Class<? extends StatsPlugin>> registeredImplementations =
                    loadRegisteredImplementations(RESOURCE_NAME, StatsPlugin.class);

            for (final Class<? extends StatsPlugin> implementation : registeredImplementations) {
                container.addComponent(implementation);
            }

            container.getComponents(StatsPlugin.class);
        } catch (EngineException e) {
        }
    }

    public static StatsPluginContainer getInstance() {
        return instance;
    }

    public void scriptComplete(StatisticsServices statisticsServices, TestStatisticsMap accumulatedStatistics, long elapsedTime) {
        final List<StatsPlugin> statsPlugins = container.getComponents(StatsPlugin.class);
        for (StatsPlugin statsPlugin : statsPlugins) {
            statsPlugin.scriptComplete(statisticsServices, accumulatedStatistics, elapsedTime);
        }
    }
}