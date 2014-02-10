package com.vv.minerlamp.util;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.vv.minerlamp.GBC;

public class Util {
	public static Point calculatePosition(Window parent, JDialog dialog) {
		int x = parent.getX() + (parent.getWidth() - dialog.getWidth()) / 2;
		int y = parent.getY() + (parent.getHeight() - dialog.getHeight()) / 2;
		Point p = new Point(x, y);
		return p;

	}

	public static JButton makeButton(Action action) {
		JButton button = new JButton(action);
		return button;
	}

	public static GBC fillParentPanel() {
		return new GBC(0, 0).setWeight(100, 100).setFill(
				GridBagConstraints.BOTH);
	}

	public static List<Long> makeArray(Long num) {
		List<Long> numList = new ArrayList<Long>();
		for (int i = 1; i <= num; i++) {
			numList.add(new Long(i));
		}
		return numList;
	}

	public static int backup(String url, String name, String pwd,
			String dbName, String file) throws IOException, InterruptedException {
		// ¡°mysqldump -u username -pPassword --opt database_name >
		// direction/backup_name.sql¡±
		int result=-1;
		String str = "mysqldump -u " + name + " -p" + pwd + " --opt   "
				+ dbName + " >  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process=rt.exec("cmd /c" + str);
		result=process.waitFor();
		System.out.println(result);
		return result;
	}

	public static int load(String url, String name, String pwd, String dbName,
			String file) throws IOException, InterruptedException {
		int result=-1;
		// ¡°mysql -u Username -pPassword database_name < back_up_dir ¡±
		String str = "mysql -u " + name + " -p" + pwd + " " + dbName
				+ "  <  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process=rt.exec("cmd /c" + str);
		result=process.waitFor();
		System.out.println(result);

		return result;

	}
	public static NumberFormat getIntegerNumberFormat(){
		NumberFormat nf=NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		return nf;
	}

}
