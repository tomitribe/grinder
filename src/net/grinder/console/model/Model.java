// The Grinder
// Copyright (C) 2000, 2001 Paco Gomez
// Copyright (C) 2000, 2001 Philip Aston

// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

package net.grinder.console.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.grinder.console.ConsoleException;
import net.grinder.plugininterface.GrinderPlugin;
import net.grinder.plugininterface.Test;
import net.grinder.statistics.Statistics;
import net.grinder.statistics.TestStatisticsMap;
import net.grinder.util.GrinderException;
import net.grinder.util.GrinderProperties;
import net.grinder.util.PropertiesHelper;


/**
 * @author Philip Aston
 * @version $Revision$
 */
public class Model
{
    private final Map m_tests = new TreeMap();
    private final HashMap m_samples = new HashMap();
    private final Sample m_totalSample = new Sample();
    private TestStatisticsMap m_summaryStatistics = new TestStatisticsMap();
    private final Thread m_sampleThread;
    private int m_sampleInterval = 500;
    private boolean m_stopSampler = false;

    public Model(GrinderProperties properties)
	throws GrinderException
    {
	final PropertiesHelper propertiesHelper = new PropertiesHelper();

	final GrinderPlugin grinderPlugin =
	    propertiesHelper.instantiatePlugin(null);

	// Shove the tests into a TreeMap so that they're ordered.
	final Iterator testSetIterator =
	    propertiesHelper.getTestSet(grinderPlugin).iterator();

	while (testSetIterator.hasNext())
	{
	    final Test test = (Test)testSetIterator.next();
	    final Integer testNumber = test.getTestNumber();
	    m_tests.put(test.getTestNumber(), test);
	    m_samples.put(test.getTestNumber(), new Sample());
	}

	m_sampleThread = new Thread(new Sampler());
	m_sampleThread.start();
    }

    public Collection getTests() 
    {
	return m_tests.values();
    }

    public TestStatisticsMap getSummaryStatistics()
    {
	return m_summaryStatistics;
    }

    private Sample getSample(Integer testNumber)
	throws ConsoleException
    {
	final Sample sample =(Sample)m_samples.get(testNumber);

	if (sample == null) {
	    throw new ConsoleException("Unknown test '" + testNumber + "'");
	}

	return sample;
    }

    public void addSampleListener(Integer testNumber, SampleListener listener)
	throws ConsoleException
    {
	getSample(testNumber).addSampleListener(listener);
    }

    public void addTotalSampleListener(SampleListener listener)
    {
	m_totalSample.addSampleListener(listener);
    }

    public void reset()
    {
	final Iterator iterator = m_samples.values().iterator();

	while (iterator.hasNext()) {
	    final Sample sample = (Sample)iterator.next();
	    sample.reset();
	}

	m_totalSample.reset();
	m_summaryStatistics = new TestStatisticsMap();
    }

    public void add(TestStatisticsMap testStatisticsMap)
	throws ConsoleException
    {
	final TestStatisticsMap.Iterator iterator =
	    testStatisticsMap.new Iterator();

	while (iterator.hasNext()) {
	    final TestStatisticsMap.Pair pair = iterator.next();

	    final Integer testNumber = pair.getTest().getTestNumber();
	    final Statistics statistics = pair.getStatistics();

	    getSample(testNumber).add(statistics);
	    m_totalSample.add(statistics);
	}

	m_summaryStatistics.add(testStatisticsMap);
    }

    private class Sample
    {
	private final List m_listeners = new LinkedList();
	private Statistics m_total = new Statistics();
	private long m_transactionsInInterval = 0;
	private double m_peakTPS = 0d;

	private synchronized void addSampleListener(SampleListener listener)
	{
	    m_listeners.add(listener);
	}

	private void add(Statistics statistics)
	{
	    m_transactionsInInterval += statistics.getTransactions();
	    m_total.add(statistics);
	}

	private synchronized void fireSample()
	{
	    final Iterator iterator = m_listeners.iterator();

	    while (iterator.hasNext()) {
		final SampleListener listener =
		    (SampleListener)iterator.next();

		final double tps =
		    1000d*m_transactionsInInterval/(double)m_sampleInterval;

		if (tps > m_peakTPS) {
		    m_peakTPS = tps;
		}

		listener.update(tps, m_peakTPS, m_total);
	    }

	    m_transactionsInInterval = 0;
	}

	private void reset()
	{
	    m_transactionsInInterval = 0;
	    m_peakTPS = 0;
	    m_total = new Statistics();
	}
    }

    private class Sampler implements Runnable
    {
	public void run()
	{
	    while (!m_stopSampler) {
		long currentTime = System.currentTimeMillis();
		
		final long wakeUpTime = currentTime + m_sampleInterval;

		while (currentTime < wakeUpTime) {
		    try {
			Thread.sleep(wakeUpTime - currentTime);
			currentTime = wakeUpTime;
		    }
		    catch(InterruptedException e) {
			currentTime = System.currentTimeMillis();
		    }
		}

		final Iterator iterator = m_samples.values().iterator();

		while (iterator.hasNext()) {
		    ((Sample)iterator.next()).fireSample();
		}

		m_totalSample.fireSample();
	    }
	}
    }

    public int getSampleInterval()
    {
	return m_sampleInterval;
    }

    public void setSampleInterval(int l)
    {
	m_sampleInterval = l;
    }
}
