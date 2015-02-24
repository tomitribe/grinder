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

import net.grinder.statistics.StatisticsServices;
import net.grinder.statistics.TestStatisticsMap;

public interface StatsPlugin {
    public void scriptComplete(StatisticsServices statisticsServices, TestStatisticsMap accumulatedStatistics, long elapsedTime);
}
