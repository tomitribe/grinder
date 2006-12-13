// Copyright (C) 2006 Philip Aston
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

package net.grinder.console.communication.server;

import net.grinder.communication.Message;
import net.grinder.communication.MessageDispatchRegistry;
import net.grinder.communication.MessageDispatchRegistry.AbstractBlockingHandler;
import net.grinder.console.communication.server.messages.ResetRecordingMessage;
import net.grinder.console.communication.server.messages.StartRecordingMessage;
import net.grinder.console.communication.server.messages.StopRecordingMessage;
import net.grinder.console.communication.server.messages.SuccessMessage;
import net.grinder.console.model.Model;



/**
 * DispatchClientCommands.
 *
 * @author Philip Aston
 * @version $Revision:$
 */
public class DispatchClientCommands {

  private final Model m_model;

  /**
   * Constructor for DispatchClientCommands.
   *
   * @param model The model.
   */
  public DispatchClientCommands(Model model) {
    m_model = model;
  }

  /**
   * Registers message handlers with a dispatcher.
   *
   * @param messageDispatcher The dispatcher.
   */
  public void registerMessageHandlers(
    MessageDispatchRegistry messageDispatcher) {

    messageDispatcher.set(
      StartRecordingMessage.class,
      new AbstractBlockingHandler() {
        public Message blockingSend(Message message) {
          m_model.start();
          return new SuccessMessage();
        }
      });

    messageDispatcher.set(
      StopRecordingMessage.class,
      new AbstractBlockingHandler() {
        public Message blockingSend(Message message) {
          m_model.stop();
          return new SuccessMessage();
        }
      });

    messageDispatcher.set(
      ResetRecordingMessage.class,
      new AbstractBlockingHandler() {
        public Message blockingSend(Message message) {
          m_model.reset();
          return new SuccessMessage();
        }
      });

  }
}