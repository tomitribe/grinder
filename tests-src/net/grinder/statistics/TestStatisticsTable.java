// The Grinder
// Copyright (C) 2001  Paco Gomez
// Copyright (C) 2001  Philip Aston

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

package net.grinder.statistics;

import junit.framework.TestCase;
import junit.swingui.TestRunner;
//import junit.textui.TestRunner;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.grinder.common.GrinderException;
import net.grinder.common.Test;
import net.grinder.common.TestImplementation;


/**
 * Unit test case for <code>StatisticsTable</code>.
 *
 * @author Philip Aston
 * @version $Revision$
 * @see RawStatistics
 */
public class TestStatisticsTable extends TestCase
{
    public static void main(String[] args)
    {
	TestRunner.run(TestStatisticsTable.class);
    }

    public TestStatisticsTable(String name)
    {
	super(name);
    }

    private TestStatisticsMap m_testStatisticsMap;

    private StatisticsView m_statisticsView;

    protected void setUp() throws Exception
    {
	m_testStatisticsMap = new TestStatisticsMap();

	final StatisticsIndexMap indexMap = StatisticsIndexMap.getInstance();

	final StatisticsIndexMap.LongIndex aIndex =
	    indexMap.getIndexForLong("userLong0");
	final StatisticsIndexMap.LongIndex bIndex =
	    indexMap.getIndexForLong("userLong1");

	final ExpressionView[] expressionViews = {
	    new ExpressionView("A", "", "userLong0"),
	    new ExpressionView("B", "", "userLong1"),
	    new ExpressionView("A plus B", "", "(+ userLong0 userLong1)"),
	    new ExpressionView("A divided by B", "",
			       "(/ userLong0 userLong1)"),
	};

	m_statisticsView = new StatisticsView();

	for (int i=0; i<expressionViews.length; ++i) {
	    m_statisticsView.add(expressionViews[i]);
	}

	final Test[] tests = {
	    new TestImplementation(9, "Test 9"),
	    new TestImplementation(3, "Test 3"),
	    new TestImplementation(113, "Another test"),
	};

	final RawStatistics[] rawStatistics = new RawStatistics[tests.length];

	final TestStatisticsFactory factory =
	    TestStatisticsFactory.getInstance();

	for (int i=0; i<tests.length; ++i) {
	    rawStatistics[i] = new RawStatisticsImplementation();
	    rawStatistics[i].addValue(aIndex, i);
	    rawStatistics[i].addValue(bIndex, i+1);

	    final TestStatistics testStatistics = factory.create();
	    testStatistics.add(rawStatistics[i]);

	    m_testStatisticsMap.put(tests[i], testStatistics);
	}
    }

    public void testStatisticsTable() throws Exception
    {
	final StringWriter expected = new StringWriter();
	final PrintWriter in = new PrintWriter(expected);
	in.println("             A            B            A plus B     A divided by ");
	in.println("                                                    B            ");
	in.println();
	in.println("Test 3       1            2            3            0.50          \"Test 3\"");
	in.println("Test 9       0            1            1            0.00          \"Test 9\"");
	in.println("Test 113     2            3            5            0.67          \"Another test\"");
	in.println();
	in.println("Totals       3            6            9            0.50         ");
	in.close();

	final StatisticsTable table = new StatisticsTable(m_statisticsView,
							  m_testStatisticsMap);

	final StringWriter stringWriter = new StringWriter();
	final PrintWriter out = new PrintWriter(stringWriter);
	table.print(out);
	out.close();

	assertEquals(expected.getBuffer().toString(),
		     stringWriter.getBuffer().toString());
    }
}
