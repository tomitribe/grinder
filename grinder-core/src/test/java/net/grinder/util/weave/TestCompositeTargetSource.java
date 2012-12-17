// Copyright (C) 2012 Philip Aston
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
// COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package net.grinder.util.weave;

import static net.grinder.testutility.AssertUtilities.assertContains;
import static net.grinder.testutility.AssertUtilities.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import net.grinder.util.weave.Weaver.TargetSource;

import org.junit.Test;

/**
 * Unit tests for {@link CompositeTargetSource}.
 *
 * @author Philip Aston
 */
public class TestCompositeTargetSource {

  @Test public void testCanApplyGood() throws Exception {
    final Method oneParameterMethod =
        String.class.getMethod("valueOf", Object.class);

    final CompositeTargetSource[] applicable = {
      new CompositeTargetSource(),
      new CompositeTargetSource(ClassSource.CLASS),
      new CompositeTargetSource(ClassSource.CLASS,
                                ParameterSource.FIRST_PARAMETER),
      new CompositeTargetSource(ParameterSource.SECOND_PARAMETER,
                                ParameterSource.FIRST_PARAMETER,
                                ClassSource.CLASS),
    };

    for (final TargetSource s : applicable) {
      assertTrue(s.toString(), s.canApply(oneParameterMethod));
    }
  }

  @Test public void testCanApplyBad() throws Exception {
    final Method zeroParameterMethod = String.class.getMethod("toString");

    final CompositeTargetSource[] notAplicable = {
      new CompositeTargetSource(ParameterSource.SECOND_PARAMETER),
      new CompositeTargetSource(ParameterSource.SECOND_PARAMETER,
                                ParameterSource.FIRST_PARAMETER),
    };

    for (final TargetSource s : notAplicable) {
      assertFalse(s.toString(), s.canApply(zeroParameterMethod));
    }
  }

  @Test
  public void testEquality() {
    final TargetSource t1 = new CompositeTargetSource();
    assertEquals(t1, t1);
    assertNotEquals(t1, this);
    assertNotEquals(t1, null);
    assertEquals(t1.hashCode(), t1.hashCode());

    final TargetSource t2 =
        new CompositeTargetSource(ParameterSource.FIRST_PARAMETER,
                                  ParameterSource.SECOND_PARAMETER);
    assertNotEquals(t1, t2);
    assertNotEquals(t2, t1);

    final TargetSource t3 =
        new CompositeTargetSource(ParameterSource.FIRST_PARAMETER,
                                  ParameterSource.SECOND_PARAMETER);
    assertEquals(t2, t3);
    assertEquals(t2.hashCode(), t3.hashCode());
  }

  @Test
  public void testToString() {
    final TargetSource t1 = ParameterSource.FIRST_PARAMETER;
    final TargetSource t2 = ClassSource.CLASS;
    final TargetSource t3 = new CompositeTargetSource(t1, t2);

    assertContains(t3.toString(), t1.toString());
    assertContains(t3.toString(), t2.toString());
  }
}
