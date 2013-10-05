// Copyright (C) 2000 - 2013 Philip Aston
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

package net.grinder.console.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import net.grinder.common.Closer;
import net.grinder.common.UncheckedInterruptedException;


/**
 * Type safe interface to resource bundle.
 *
 * @author Philip Aston
 */
public final class ResourcesImplementation implements Resources {

  private PrintWriter m_errorWriter = new PrintWriter(System.err, true);
  private final ResourceBundle m_resources;
  private final String m_package;

  /**
   * Constructor.
   *
   * @param bundleName Name of resource bundle. The package part of
   * the name is used to resolve the location of resources referred to
   * in the resource bundle.
   */
  public ResourcesImplementation(final String bundleName) {

    m_resources = ResourceBundle.getBundle(bundleName);

    final int lastDot = bundleName.lastIndexOf(".");

    if (lastDot > 0) {
      m_package = "/" + bundleName.substring(0, lastDot + 1).replace('.', '/');
    }
    else {
      m_package = "/";
    }
  }

  /**
   * Set a writer to report warnings to.
   *
   * @param writer The writer.
   */
  public void setErrorWriter(final PrintWriter writer) {
    m_errorWriter = writer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString(final String key) {
    return getString(key, true);
  }

  private String getString(final String key, final boolean warnIfMissing) {

    try {
      return m_resources.getString(key);
    }
    catch (final MissingResourceException e) {
      if (warnIfMissing) {
        m_errorWriter.println(
          "Warning - resource " + key + " not specified");
        return "";
      }

      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageIcon getImageIcon(final String key) {
    final URL resource = get(key, false);

    return resource != null ? new ImageIcon(resource) : null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStringFromFile(final String key,
    final boolean warnIfMissing) {

    final URL resource = get(key, warnIfMissing);

    if (resource != null) {
      Reader in = null;

      try {
        in = new InputStreamReader(resource.openStream());

        final StringWriter out = new StringWriter();

        final char[] buffer = new char[128];

        while (true) {
          final int n = in.read(buffer);

          if (n == -1) {
            break;
          }

          out.write(buffer, 0, n);
        }

        out.close();

        return out.toString();
      }
      catch (final IOException e) {
        UncheckedInterruptedException.ioException(e);
        m_errorWriter.println("Warning - could not read " + resource);
      }
      finally {
        Closer.close(in);
      }
    }

    return null;
  }

  private URL get(final String key, final boolean warnIfMissing) {
    final String name = getString(key, warnIfMissing);

    if (name == null || name.length() == 0) {
      return null;
    }

    final URL url = this.getClass().getResource(m_package + name);

    if (url == null) {
      m_errorWriter.println("Warning - could not load resource " + name);
    }

    return url;
  }
}
