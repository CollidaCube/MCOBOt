package com.collidacube.bot.utils.specialized;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtils {

	public static String getContent(File file) throws FileNotFoundException {
		String content = "";

		Scanner reader = new Scanner(file);
		while (reader.hasNextLine())
			content = content + reader.nextLine() + "\n";
		reader.close();

		return content;
	}

	public static void append(File file, String content) throws IOException {
		write(file, content + getContent(file));
	}

	public static void write(File file, String content) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}

}
