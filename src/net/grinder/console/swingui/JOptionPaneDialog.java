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

package net.grinder.console.swingui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * <code>JDialog</code> that is more useful than that returned by
 * <code>JOptionPane.createDialog()</code>.
 *
 * @author Philip Aston
 * @version $Revision$
 */
public class JOptionPaneDialog extends JDialog
{
    public JOptionPaneDialog(JFrame frame, final JOptionPane optionPane,
			     String title, boolean modal) 
    {
	super(frame, title, modal);

	setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

	final Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());
	contentPane.add(optionPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(frame);

	addWindowListener(
	    new WindowAdapter() {
		private boolean m_gotFocus = false;

		public void windowClosing(WindowEvent e)
		{
		    optionPane.setValue(null);
		}

		public void windowActivated(WindowEvent e)
		{
		    // Once window gets focus, set initial focus
		    if (!m_gotFocus) {
			optionPane.selectInitialValue();
			m_gotFocus = true;
		    }
		}
	    });

	optionPane.addPropertyChangeListener(
	    new PropertyChangeListener()
	    {
		private boolean m_disable = false;

		public void propertyChange(PropertyChangeEvent e) {
		    if(isVisible() && 
		       e.getSource() == optionPane &&
		       !m_disable &&
		       (e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)
			||
			e.getPropertyName().equals(
			    JOptionPane.INPUT_VALUE_PROPERTY))) {

			final Cursor oldCursor = getCursor();
			setCursor(
			    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			try {
			    if (shouldClose()) {
				setVisible(false);
				dispose();
			    }
			}
			finally {
			    m_disable = true;
			    optionPane.setValue(null);
			    m_disable = false;
			    setCursor(oldCursor);
			}
		    }
		}
	    });

	optionPane.setValue(null);
    }

    protected boolean shouldClose()
    {
	return true;
    }
}
