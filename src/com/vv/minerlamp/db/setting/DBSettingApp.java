package com.vv.minerlamp.db.setting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.vv.minerlamp.ButtonAction;
import com.vv.minerlamp.GBC;
import com.vv.minerlamp.util.PropertiesUtil;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

public class DBSettingApp extends JFrame {
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	
	private String backupDbPos;
	private static String backupsqlstmt = "CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)";

	public DBSettingApp() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(DBSettingApp.this);
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		} catch (InstantiationException e1) {

			e1.printStackTrace();
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {

			e1.printStackTrace();
		}
		;
		try {
			Image icon = ImageIO.read(new File("resources/dbSettingIcon.png"));
			setIconImage(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}

		setTitle("数据库设置");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (screenSize.getWidth() - WIDTH) / 2;
		int y = (int) (screenSize.getHeight() - HEIGHT) / 2;
		setLocation(new Point(x, y));
		SysConfiguration.init();
		add(new DbPanel2(), BorderLayout.CENTER);
	}

	class DbPanel2 extends JPanel {

		public DbPanel2() {
			setLayout(new GridBagLayout());
			JPanel btnPanel = new JPanel();
			btnPanel.setBorder(BorderFactory.createTitledBorder("操作"));
			btnPanel.setLayout(new GridBagLayout());
			JButton conBtn = new JButton("连接数据库");
			conBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					infoList.clear();
					infoList.add("连接数据库");
					showInfoMsg();
					connectTest();
					showInfoMsg();
				}
			});
			JButton createSysBtn = new JButton("创建数据库*");
			createSysBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "该操作会删除原先的数据库，确定要创建数据库吗", "提示",
							JOptionPane.YES_NO_OPTION);

					if (selection == JOptionPane.YES_OPTION) {
						infoList.clear();
						infoList.add("创建数据库");

						showInfoMsg();
						createSysDb();
						showInfoMsg();

					}

				}
			});
			JButton createBtn = new JButton("创建数据库");
			createBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "该操作会删除原先的数据库，确定要创建数据库吗", "提示",
							JOptionPane.YES_NO_OPTION);

					if (selection == JOptionPane.YES_OPTION) {
						infoList.clear();
						infoList.add("创建数据库");

						showInfoMsg();
						createDb();
						showInfoMsg();

					}

				}
			});
			JButton backupBtn = new JButton("备份数据库");
			backupBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser pathChooser = new JFileChooser();
					pathChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {

						File selDir = pathChooser.getSelectedFile();
						System.out.println("backup path="
								+ selDir.getAbsolutePath());
						infoList.clear();
						infoList.add("备份数据库");
						if (selDir.listFiles() != null
								&& selDir.listFiles().length > 0) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"请选择一个空目录");
							infoList.add("请选择一个空目录！");
							showInfoMsg();

						} else {
							backupDbPos = selDir.getAbsolutePath();

							showInfoMsg();
							backupDb();
							showInfoMsg();
						}

					}
				}
			});
			JButton restoreBtn = new JButton("还原数据库");
			restoreBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean check = false;
					JFileChooser pathChooser = new JFileChooser();
					pathChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {

						File selDir = pathChooser.getSelectedFile();
						System.out.println("backup path="
								+ selDir.getAbsolutePath());
						infoList.clear();
						infoList.add("还原数据库");
						if (selDir.listFiles() == null
								|| selDir.listFiles().length == 0) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"不能为空目录");
							infoList.add("不能为空目录！");
							showInfoMsg();

						} else if (selDir.listFiles().length == 1) {
							File subFile = selDir.listFiles()[0];
							if (subFile.isDirectory()) {
								File[] bkFiles = subFile.listFiles();
								for (File file : bkFiles) {
									if (file.getName().endsWith("")) {
										check = true;
										break;
									}
								}
								if (check == true) {
									backupDbPos = subFile.getAbsolutePath();

								}

							}

						} else {
							File[] bkFiles = selDir.listFiles();
							for (File file : bkFiles) {
								if (file.getName().endsWith("")) {
									check = true;
									break;
								}
							}
							if (check == true) {
								backupDbPos = selDir.getAbsolutePath();

							}

						}
						if (check == true) {
							System.out.println(" restore from file:"
									+ backupDbPos);

							showInfoMsg();
							restoreDb();
							showInfoMsg();
						}
						else{
							infoList.add("备份文件格式不正确");
							showInfoMsg();
						}

					}

				}
			});
			btnPanel.add(conBtn, new GBC(0, 0).setInsets(0, 0, 10, 0));
			btnPanel.add(createBtn, new GBC(0, 1).setInsets(0, 0, 10, 0));
			btnPanel.add(backupBtn, new GBC(0, 2).setInsets(0, 0, 10, 0));
			btnPanel.add(restoreBtn, new GBC(0, 3).setInsets(0, 0, 10, 0));
			btnPanel.add(createSysBtn, new GBC(0, 4).setInsets(0, 0, 10, 0));
			JPanel infoPanel = new JPanel();
			infoPanel.setBorder(BorderFactory.createTitledBorder("信息"));
			infoLabel = new JLabel("<html>数据库管理</html>");

			infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
			infoLabel.setVerticalAlignment(SwingConstants.TOP);
			infoLabel.setPreferredSize(new Dimension(150, 100));
			// infoLabel.setBorder(BorderFactory.createEtchedBorder());

			infoPanel.add(infoLabel);
			JPanel tipsPanel = new JPanel();
			tipsPanel.setBorder(BorderFactory.createTitledBorder("提示"));
			tipsPanel.add(new JLabel("请确保未同时运行其他程序"));
			add(btnPanel, new GBC(0, 0).setWeight(20, 100).setFill(GBC.BOTH));
			add(infoPanel, new GBC(1, 0).setWeight(80, 100).setFill(GBC.BOTH));
			add(tipsPanel,
					new GBC(0, 1, 2, 1).setFill(GBC.BOTH).setInsets(2, 2, 5, 2));

		}
	}

	class Dbpanel extends JPanel {
		public Dbpanel() {

			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("数据库设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("数据库IP："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("    端口："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("  用户名："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    密码："), new GBC(0, 3).setWeight(0, 0));
			ipTxt = new JTextField();
			ipTxt.setText(SysConfiguration.dbIp);
			ipTxt.setPreferredSize(new Dimension(180, 20));
			portTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			portTxt.setValue(SysConfiguration.dbPort);
			portTxt.setPreferredSize(new Dimension(180, 20));
			userTxt = new JTextField();
			userTxt.setText(SysConfiguration.dbUserName);
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JTextField();
			pwdTxt.setText(SysConfiguration.dbPwd);
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			panel.add(ipTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(portTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(userTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(pwdTxt,
					new GBC(1, 3).setWeight(100, 0).setInsets(5, 3, 0, 0));
			JPanel bottomPanel = new JPanel();
			bottomPanel.add(Util.makeButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						String dbIp = ipTxt.getText();
						Integer dbPort = new Integer(portTxt.getText());
						String dbUserName = userTxt.getText();
						String dbPwd = pwdTxt.getText();
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.url",
								SysConfiguration.makeDbUrl(dbIp, dbPort));
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.username", dbUserName);
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.password", dbPwd);

						SysConfiguration.dbIp = dbIp;
						SysConfiguration.dbPort = dbPort;
						SysConfiguration.dbUserName = dbUserName;
						SysConfiguration.dbPwd = dbPwd;

						JOptionPane
								.showMessageDialog(DBSettingApp.this, "保存成功");
					}

				}
			}));
			bottomPanel.add(Util.makeButton(new ButtonAction("连接测试", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Connection con = null;
					String url = SysConfiguration.dbUrl;
					/*
					 * if (url.endsWith(SysConfiguration.dbName)) { url =
					 * url.substring(0, url.indexOf(SysConfiguration.dbName)); }
					 */
					try {
						Class.forName(driver);
						con = DriverManager.getConnection(url, null, null);
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接成功");
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} catch (SQLException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} finally {
						try {
							con.close();
						} catch (SQLException e1) {

							e1.printStackTrace();
						}
					}
				}
			}));
			bottomPanel.add(Util.makeButton(new ButtonAction("创建数据库", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "该操作会删除原先的数据库，确定要创建数据库吗", "提示",
							JOptionPane.YES_NO_OPTION);

					if (selection == JOptionPane.YES_OPTION) {

					}

				}
			}));
			panel.add(bottomPanel, new GBC(0, 4, 2, 1).setWeight(100, 0)
					.setInsets(2));
			JPanel bottomPanel2 = new JPanel();
			bottomPanel2.add(Util.makeButton(new ButtonAction("数据库备份", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser pathChooser = new JFileChooser();
					pathChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {
						int execResult = -1;
						try {
							String path = pathChooser.getSelectedFile()
									.getAbsolutePath();
							if (!path.endsWith("\\")) {
								path += "\\";
							}
							path += SysConfiguration.backupFileName;
							execResult = Util.backup(SysConfiguration.dbUrl,
									SysConfiguration.dbUserName,
									SysConfiguration.dbPwd,
									SysConfiguration.dbName, path);
							if (execResult == 0) {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "备份成功");
							} else {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "备份失败，请重启系统后再次尝试");
							}
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"备份失败，请重启系统后再次尝试");
							ex.printStackTrace();
						} catch (InterruptedException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"备份失败，请重启系统后再次尝试");
							ex.printStackTrace();
						}
					}
				}
			}));
			bottomPanel2.add(Util.makeButton(new ButtonAction("数据库还原", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser pathChooser = new JFileChooser();
					pathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {
						try {
							int execResult = -1;
							String path = pathChooser.getSelectedFile()
									.getAbsolutePath();
							execResult = Util.load(SysConfiguration.dbUrl,
									SysConfiguration.dbUserName,
									SysConfiguration.dbPwd,
									SysConfiguration.dbName, path);
							if (execResult == 0) {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "还原成功");
							} else {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "还原失败，请重启系统后再次尝试");
							}
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"还原失败，请重启系统后再次尝试");
							ex.printStackTrace();
						} catch (InterruptedException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"还原失败，请重启系统后再次尝试");
							ex.printStackTrace();
						}
					}
				}
			}));
			panel.add(bottomPanel2, new GBC(0, 5, 2, 1).setWeight(100, 0)
					.setInsets(2));
			add(panel, Util.fillParentPanel());
		}

		private JTextField ipTxt;
		private JFormattedTextField portTxt;
		private JTextField userTxt;
		private JTextField pwdTxt;

	}

	void showInfoMsg() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		for (String info : infoList) {
			sb.append(info).append("<br>");
		}
		sb.append("</html>");

		infoLabel.setText(sb.toString());

	}

	public boolean connectTest() {
		boolean result = false;
		Connection con = null;
		String url = SysConfiguration.dbUrl;
		/*
		 * if (url.endsWith(SysConfiguration.dbName)) { url = url.substring(0,
		 * url.indexOf(SysConfiguration.dbName)); }
		 */
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, null, null);
			errMsg = "数据库连接成功";
			result = true;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			errMsg = "数据库连接失败,请检查数据库配置是否正确";
		} catch (SQLException e1) {

			System.out.println("sqlErrMsg=" + e1.getMessage());

			e1.printStackTrace();
			String em = e1.getMessage();
			if (em.contains("未找到")) {
				errMsg = "数据库不存在，请创建数据库";
			} else if (em.contains("无法使用类加载器")) {
				errMsg = "连接失败，请先关闭其他程序";
			} else {
				errMsg = "数据库连接失败,请检查数据库配置是否正确";
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			errMsg = "数据库连接失败,请检查数据库配置是否正确";
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
			}
		}
		infoList.add(errMsg);
		return result;
	}

	public void createSysDb() {
		System.out.println(new File(SysConfiguration.dbName).exists());
		deleteFolder(new File(SysConfiguration.dbName));
		System.out.println(new File(SysConfiguration.dbName).exists());
		if (!new File(SysConfiguration.dbName).exists()) {
			Connection con = null;
			Statement sta = null;
			Scanner scanner = null;
			StringBuilder sb = new StringBuilder();
			String url = SysConfiguration.dbUrl;
			url += ";create=true";
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, null, null);
				sta = con.createStatement();
				scanner = new Scanner(new File("create.sql"));
				while (scanner.hasNextLine()) {
					sb.append(scanner.nextLine());
				}
				String[] statements = sb.toString().split(";");
				for (String statement : statements) {
					sta.addBatch(statement);
				}
				sta.executeBatch();
				errMsg = "数据库创建成功";
			} catch (FileNotFoundException ex) {

				ex.printStackTrace();
			} catch (SQLException e1) {
				System.out.println("sqlErrMsg=" + e1.getMessage());

				e1.printStackTrace();
				String em = e1.getMessage();
				if (em.contains("未找到")) {
					errMsg = "数据库不存在，请创建数据库";
				} else if (em.contains("无法使用类加载器")) {
					errMsg = "连接失败，请先关闭其他程序";
				} else {
					errMsg = "数据库连接失败,请检查数据库配置是否正确";
				}
			} catch (ClassNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} finally {
				try {
					sta.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					con.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {// 目录删除不成功
			errMsg = "请先关闭其他应用程序";
		}
		infoList.add(errMsg);
	}
	public void createDb() {
		System.out.println(new File(SysConfiguration.dbName).exists());
		deleteFolder(new File(SysConfiguration.dbName));
		System.out.println(new File(SysConfiguration.dbName).exists());
		if (new File(SysConfiguration.dbName).exists()) {
			return;
		}
		Connection con = null;
		DatabaseMetaData dbmd = null ;

		String url = SysConfiguration.dbUrl;
		url += ";restoreFrom=" + SysConfiguration.dbInitPos;
	//	url += ";createFrom=" + backupDbPos;
		System.out.println(" restore url="+url);
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, null, null);
			dbmd = con.getMetaData() ;

			System.out.println("\n----------------------------------------------------") ;
			System.out.println("Database Name    = " + dbmd.getDatabaseProductName()) ;
			System.out.println("Database Version = " + dbmd.getDatabaseProductVersion()) ;
			System.out.println("Driver Name      = " + dbmd.getDriverName()) ;
			System.out.println("Driver Version   = " + dbmd.getDriverVersion()) ;
			System.out.println("Database URL     = " + dbmd.getURL()) ;
			System.out.println("----------------------------------------------------") ;
				errMsg = "数据库创建成功";
			
			
		} catch (SQLException e1) {
			System.out.println("sqlErrMsg=" + e1.getMessage());

			e1.printStackTrace();
			String em = e1.getMessage();
			if (em.contains("未找到")) {
				errMsg = "初始数据库不存在";
			} else if (em.contains("无法使用类加载器")) {
				errMsg = "连接失败，请先关闭其他程序";
			} else {
				errMsg = "数据库连接失败,请检查数据库配置是否正确";
			}
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				if (con != null) {
					con.close();
					con=null;
					System.gc();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		infoList.add(errMsg);
	}
	public void backupDb() {

		Connection con = null;
		CallableStatement cs = null;

		String url = SysConfiguration.dbUrl;
		// url += ";create=true";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, null, null);
			cs = con.prepareCall(backupsqlstmt);
			cs.setString(1, backupDbPos);
			cs.execute();

			errMsg = "数据库备份成功";
		} catch (SQLException e1) {
			System.out.println("sqlErrMsg=" + e1.getMessage());

			e1.printStackTrace();
			String em = e1.getMessage();
			if (em.contains("未找到")) {
				errMsg = "数据库不存在，请创建数据库";
			} else if (em.contains("无法使用类加载器")) {
				errMsg = "连接失败，请先关闭其他程序";
			} else {
				errMsg = "数据库连接失败,请检查数据库配置是否正确";
			}
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {

			try {
				if (cs != null) {
					cs.close();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		infoList.add(errMsg);
	}

	public void restoreDb() {

		Connection con = null;
		DatabaseMetaData dbmd = null ;

		String url = SysConfiguration.dbUrl;
		url += ";restoreFrom=" + backupDbPos;
	//	url += ";createFrom=" + backupDbPos;
		System.out.println(" restore url="+url);
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, null, null);
			dbmd = con.getMetaData() ;

			System.out.println("\n----------------------------------------------------") ;
			System.out.println("Database Name    = " + dbmd.getDatabaseProductName()) ;
			System.out.println("Database Version = " + dbmd.getDatabaseProductVersion()) ;
			System.out.println("Driver Name      = " + dbmd.getDriverName()) ;
			System.out.println("Driver Version   = " + dbmd.getDriverVersion()) ;
			System.out.println("Database URL     = " + dbmd.getURL()) ;
			System.out.println("----------------------------------------------------") ;
				errMsg = "数据库还原成功";
			
			
		} catch (SQLException e1) {
			System.out.println("sqlErrMsg=" + e1.getMessage());

			e1.printStackTrace();
			String em = e1.getMessage();
			if (em.contains("未找到")) {
				errMsg = "数据库不存在，请创建数据库";
			} else if (em.contains("无法使用类加载器")) {
				errMsg = "连接失败，请先关闭其他程序";
			} else {
				errMsg = "数据库连接失败,请检查数据库配置是否正确";
			}
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				if (con != null) {
					con.close();
					con=null;
					System.gc();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		infoList.add(errMsg);
	}

	private void deleteFolder(File dir) {
		File filelist[] = dir.listFiles();
		if (filelist != null) {
			int listlen = filelist.length;
			for (int i = 0; i < listlen; i++) {
				if (filelist[i].isDirectory()) {
					deleteFolder(filelist[i]);
				} else {
					filelist[i].delete();
				}
			}
		}
		dir.delete();
	}

	public static void main(String[] args) {
		DBSettingApp dbSettingApp = new DBSettingApp();
		dbSettingApp.setVisible(true);
	}

	private JLabel infoLabel;
	private String errMsg = "";
	private List<String> infoList = new ArrayList<String>();
	private static final int WIDTH = 360;
	private static final int HEIGHT = 250;
}
