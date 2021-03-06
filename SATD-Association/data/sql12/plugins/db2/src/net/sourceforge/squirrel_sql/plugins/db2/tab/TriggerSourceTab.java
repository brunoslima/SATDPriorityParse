package net.sourceforge.squirrel_sql.plugins.db2.tab;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
/**
 * This class will display the source for an DB2 trigger.
 *
 * @author manningr
 */
public class TriggerSourceTab extends FormattedSourceTab
{
	/** SQL that retrieves the source of a trigger. */
	private final static String SQL =
        "select TEXT from SYSCAT.TRIGGERS " +
        "where TABSCHEMA = ? " +
        "and TRIGNAME = ? ";
    
	/** SQL that retrieves the source of a trigger on DB2 on OS/400. */
	private final static String OS2_400_SQL = 
	    "select action_statement " +
	    "from qsys2.systriggers " +
	    "where trigger_schema = ? " +
	    "and trigger_name = ? ";
	
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerSourceTab.class);
	
	private boolean isOS2400 = false;

	public TriggerSourceTab(String hint, boolean isOS2400, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
        this.isOS2400 = isOS2400;
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createStatement()
     */
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		String sql = SQL;
		if (isOS2400) {
		    sql = OS2_400_SQL;
		}
		
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("trigname="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
