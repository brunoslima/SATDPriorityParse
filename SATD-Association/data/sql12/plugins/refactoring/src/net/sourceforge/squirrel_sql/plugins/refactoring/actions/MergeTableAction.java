package net.sourceforge.squirrel_sql.plugins.refactoring.actions;

/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * manningr@users.sourceforge.net
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
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.MergeTableCommand;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialogFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.MergeTableDialogFactory;

public class MergeTableAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = -7536303163886512534L;

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MergeTableAction.class);

	private static interface i18n
	{
		String ACTION_PART = s_stringMgr.getString("MergeTableAction.actionPart");

		String OBJECT_PART = s_stringMgr.getString("Shared.tableObject");

		String SINGLE_OBJECT_MESSAGE =
			s_stringMgr.getString("Shared.singleObjectMessage", OBJECT_PART, ACTION_PART);
	}

	/** The factory that creates MergeTableDialogs */
	private IMergeTableDialogFactory _dialogFactory = new MergeTableDialogFactory();

	public MergeTableAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#getCommand(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo[])
	 */
	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new MergeTableCommand(_session, info, _dialogFactory);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#getErrorMessage()
	 */
	@Override
	protected String getErrorMessage()
	{
		return i18n.SINGLE_OBJECT_MESSAGE;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#isMultipleObjectAction()
	 */
	@Override
	protected boolean isMultipleObjectAction()
	{
		return false;
	}
}
