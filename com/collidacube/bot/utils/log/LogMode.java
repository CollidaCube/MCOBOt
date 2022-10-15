package com.collidacube.bot.utils.log;

import java.awt.*;

public enum LogMode {
	DEBUG 		(Color.DARK_GRAY, 	"      DEBUG"),
	INFO 		(Color.BLUE,		"       INFO"),
	WARNING 	(Color.YELLOW,		"    WARNING"),
	ERROR 		(Color.RED,			"      ERROR"),
	SUCCESS 	(Color.GREEN,		"    SUCCESS");

	public final Color color;
	public final String prefix;
	LogMode(Color color, String prefix) {
		this.color = color;
		this.prefix = prefix;
	}
}
