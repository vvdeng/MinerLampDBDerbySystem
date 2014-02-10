package com.vv.minerlamp.util;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SysConfiguration {

	public static final String DBCONFIG_FILE_PATH = "db.properties";
	public static String dbUrl;
	public static String dbIp;
	public static Integer dbPort;
	public static String dbUserName;
	public static String dbPwd;
	public static String dbName="minerlamp";
	public static String backupFileName="智能管理系统数据备份.dat";
	
	public static void init() {
		
		Properties dbProps = PropertiesUtil.getProperties("db.properties");
		dbUrl=dbProps.get("hibernate.connection.url").toString();
		parse(dbUrl);
		dbUserName = dbProps.get("hibernate.connection.username").toString();
		dbPwd = dbProps.get("hibernate.connection.password").toString();
		
	}

	public static void parse( String url) {
		Pattern pattern = Pattern.compile(".*//(.*):(\\d*)/.*");
		Matcher matcher=pattern.matcher(url);
		
		if(matcher.find()){
			dbIp=matcher.group(1);
			dbPort=new Integer(matcher.group(2));
		
		}
	}
	public static String makeDbUrl(String ip, Integer port){
		return new StringBuilder().append("jdbc:mysql://").append(ip).append(":").append(port).append("/minerlamp").toString();
	}
	public static void main(String[] args) {
		init();
	}
}
