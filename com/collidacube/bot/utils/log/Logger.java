package com.collidacube.bot.utils.log;

import com.collidacube.bot.utils.specialized.SqlUtils;
import com.collidacube.bot.utils.specialized.Utils;
import org.javacord.api.entity.user.User;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Logger {
    
	private static final Connection con = SqlUtils.getConnection("D:\\NewDrive\\Projects\\MCO Discord\\Data\\logs\\" + Utils.getFormattedTime("MM-dd-yyyy") + ".db");
	private static final List<String> fields = Arrays.asList("time", "category", "mode", "culprit", "victim", "title", "description");

	static {
		SqlUtils.addTable(con, "data", fields);
	}

    public static boolean DEBUG_MODE = false;

	public static void debug(LoggingCategory category, User culprit, User victim, String title, String description) {
		if (!DEBUG_MODE) log(category, LogMode.DEBUG, culprit, victim, title, description);
	}

    public static void info(LoggingCategory category, User culprit, User victim, String title, String description) {
		log(category, LogMode.INFO, culprit, victim, title, description);
    }

	public static void warning(LoggingCategory category, User culprit, User victim, String title, String description) {
		log(category, LogMode.WARNING, culprit, victim, title, description);
	}

	public static void error(LoggingCategory category, User culprit, User victim, String title, String description) {
		log(category, LogMode.ERROR, culprit, victim, title, description);
	}

	public static void success(LoggingCategory category, User culprit, User victim, String title, String description) {
		log(category, LogMode.SUCCESS, culprit, victim, title, description);
	}

	public static void log(LogMode mode, String description) {
		// log(null, mode, null, null, null, description);
		System.out.println(Utils.getFormattedTime("yyyy-MM-dd HH:mm:ss") + mode.prefix + ": " + description);
	}

	public static void log(LoggingCategory category, LogMode mode, User culprit, User victim, String title, String description) {
		if (category != null) category.sendLogMessage(mode, title, culprit, victim, description);

		String sTime 				= Utils.getFormattedTime("MM-dd-yyyy HH:mm:ss");
		String sCategory 			= category 				== null ? "" : category.name();
		String sMode 				= mode 					== null ? "" : mode.name();
		String sCulprit 			= culprit 				== null ? "" : culprit.getIdAsString();
		String sVictim 				= victim 				== null ? "" : victim.getIdAsString();
		String sTitle				= title					== null ? "" : title;
		String sDescription			= description 			== null ? "" : description;

		SqlUtils.insert(con, "data", fields, Arrays.asList(sTime, sCategory, sMode, sCulprit, sVictim, sTitle, sDescription));
	}

}
