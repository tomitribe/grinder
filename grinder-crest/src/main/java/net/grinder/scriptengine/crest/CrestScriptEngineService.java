// Copyright (C) 2001 - 2013 Philip Aston
// Copyright (C) 2005 Martin Wagner
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

package net.grinder.scriptengine.crest;

import net.grinder.common.GrinderProperties;
import net.grinder.engine.common.EngineException;
import net.grinder.engine.common.ScriptLocation;
import net.grinder.scriptengine.DCRContext;
import net.grinder.scriptengine.Instrumenter;
import net.grinder.scriptengine.ScriptEngineService;
import net.grinder.util.FileExtensionMatcher;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;


/**
 * Crest {@link net.grinder.scriptengine.ScriptEngineService} implementation.
 *
 * @author Jonathan Gallimore
 */
public final class CrestScriptEngineService implements ScriptEngineService {

    private final FileExtensionMatcher matcher = new FileExtensionMatcher(".jar");

    private final DCRContext m_dcrContext;

    /**
     * Constructor.
     *
     * @param properties     Properties.
     * @param dcrContext     DCR context.
     * @param scriptLocation Script location.
     */
    public CrestScriptEngineService(final GrinderProperties properties,
                                    final DCRContext dcrContext,
                                    final ScriptLocation scriptLocation) {
        m_dcrContext = dcrContext;
    }

    /**
     * Constructor used when DCR is unavailable.
     */
    public CrestScriptEngineService() {
        m_dcrContext = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Instrumenter> createInstrumenters()
            throws EngineException {

        if (m_dcrContext != null) {
            return asList(new CrestDCRInstrumenter(m_dcrContext));
        }

        return emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptEngine createScriptEngine(final ScriptLocation script)
            throws EngineException {

        if (matcher.accept(script.getFile())) {
            return new CrestScriptEngine();
        }

        return null;
    }
}
