// Copyright (C) 2000 Paco Gomez
// Copyright (C) 2000, 2001, 2002 Philip Aston
// All rights reserved.
//
// This file is part of The Grinder software distribution. Refer to
// the file LICENSE which is part of The Grinder distribution for
// licensing details. The Grinder distribution is available on the
// Internet at http://grinder.sourceforge.net/
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package net.grinder.statistics;

import junit.framework.TestCase;
import junit.swingui.TestRunner;
//import junit.textui.TestRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import net.grinder.common.GrinderException;


/**
 * Unit test case for <code>TestStatisticsFactory</code>.
 *
 * @author Philip Aston
 * @version $Revision$
 * @see RawStatistics
 */
public class TestTestStatisticsFactory extends TestCase
{
    public static void main(String[] args)
    {
	TestRunner.run(TestTestStatisticsFactory.class);
    }

    public TestTestStatisticsFactory(String name)
    {
	super(name);
    }

    public void testCreation() throws Exception
    {
	final TestStatisticsFactory factory =
	    TestStatisticsFactory.getInstance();

	assertSame(factory, TestStatisticsFactory.getInstance());
    }

    public void testFactory() throws Exception
    {
	final TestStatisticsFactory factory =
	    TestStatisticsFactory.getInstance();

	final TestStatistics testStatistics1 = factory.createImplementation();
	assert(testStatistics1 != null);

	final TestStatistics testStatistics2 = factory.create();
	assert(testStatistics2 instanceof TestStatisticsImplementation);
    }

    public void testSerialisation() throws Exception
    {
	final TestStatisticsFactory factory =
	    TestStatisticsFactory.getInstance();

	final Random random = new Random();

	final StatisticsIndexMap indexMap = StatisticsIndexMap.getInstance();
	final StatisticsIndexMap.LongIndex aIndex =
	    indexMap.getIndexForLong("userLong0");
	final StatisticsIndexMap.LongIndex bIndex =
	    indexMap.getIndexForLong("userLong1");
	final StatisticsIndexMap.LongIndex cIndex =
	    indexMap.getIndexForLong("userLong2");

	final TestStatisticsImplementation original0 =
	    factory.createImplementation();
	original0.addValue(aIndex, Math.abs(random.nextLong()));
	original0.addValue(bIndex, Math.abs(random.nextLong()));
	original0.addValue(cIndex, Math.abs(random.nextLong()));

	final TestStatisticsImplementation original1 =
	    factory.createImplementation();

	final ByteArrayOutputStream byteOutputStream =
	    new ByteArrayOutputStream();

	final ObjectOutputStream objectOutputStream =
	    new ObjectOutputStream(byteOutputStream);

	factory.writeStatisticsExternal(objectOutputStream, original0);
	factory.writeStatisticsExternal(objectOutputStream, original1);

	objectOutputStream.close();

	final ObjectInputStream objectInputStream =
	    new ObjectInputStream(
		new ByteArrayInputStream(byteOutputStream.toByteArray()));

	final TestStatistics received0 =
	    factory.readStatisticsExternal(objectInputStream);

	final TestStatistics received1 =
	    factory.readStatisticsExternal(objectInputStream);

	assertEquals(original0, received0);
	assertEquals(original1, received1);
    }
}
