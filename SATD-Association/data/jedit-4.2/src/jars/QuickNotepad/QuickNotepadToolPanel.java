/*
 * QuickNotepadToolPanel.java
 * part of the QuickNotepad plugin for the jEdit text editor
 * Copyright (C) 2001 John Gellene
 * jgellene@nyc.rr.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: QuickNotepadToolPanel.java,v 1.4 2003/05/29 19:02:55 spestov Exp $
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

public class QuickNotepadToolPanel extends JPanel
{
	private QuickNotepad pad;
	private JLabel label;

	public QuickNotepadToolPanel(QuickNotepad qnpad)
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		pad = qnpad;

		Box labelBox = new Box(BoxLayout.Y_AXIS);
		labelBox.add(Box.createGlue());

		label = new JLabel(pad.getFilename());
		label.setVisible(jEdit.getProperty(
			QuickNotepadPlugin.OPTION_PREFIX + "show-filepath").equals("true"));

		labelBox.add(label);
		labelBox.add(Box.createGlue());

		add(labelBox);

		add(Box.createGlue());

		add(makeCustomButton("quicknotepad.choose-file",
			new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					QuickNotepadToolPanel.this.pad.chooseFile();
				}
			}));
		add(makeCustomButton("quicknotepad.save-file",
			new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					QuickNotepadToolPanel.this.pad.saveFile();
				}
			}));
		add(makeCustomButton("quicknotepad.copy-to-buffer",
			new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					QuickNotepadToolPanel.this.pad.copyToBuffer();
				}
			}));
	}


	void propertiesChanged()
	{
		label.setText(pad.getFilename());
		label.setVisible(jEdit.getProperty(
			QuickNotepadPlugin.OPTION_PREFIX + "show-filepath").equals("true"));
	}

	private AbstractButton makeCustomButton(String name, ActionListener listener)
	{
		String toolTip = jEdit.getProperty(name.concat(".label"));
		AbstractButton b = new RolloverButton(GUIUtilities.loadIcon(
			jEdit.getProperty(name + ".icon")));
		if(listener != null)
		{
			b.addActionListener(listener);
			b.setEnabled(true);
		}
		else
		{
			b.setEnabled(false);
		}
		b.setToolTipText(toolTip);
		return b;
	}

}

