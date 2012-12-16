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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.grinder.util.weave.Weaver.TargetSource;

/**
 * Composite {@link TargetSource}.
 *
 * @author Philip Aston
 */
public final class CompositeTargetSource implements TargetSource {

  private final Set<TargetSource> m_sources;

  /**
   * Constructor.
   *
   * @param sources The included target sources.
   */
  public CompositeTargetSource(final TargetSource... sources) {
    m_sources = new HashSet<TargetSource>(Arrays.asList(sources));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canApply(final Method method) {
    for (final TargetSource s : m_sources) {
      if (!s.canApply(method)) {
        return false;
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int targetCount() {
    int n = 0;

    for (final TargetSource s : m_sources) {
      n += s.targetCount();
    }

    return n;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return m_sources.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return m_sources.equals(((CompositeTargetSource) o).m_sources);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return m_sources.toString();
  }
}
