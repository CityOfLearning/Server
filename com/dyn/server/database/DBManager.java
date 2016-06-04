package com.dyn.server.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;

import com.dyn.server.ServerMod;

public class DBManager {
	private final static String dbURL = "";
	private final static String MasterUsername = "";
	private final static String MasterUserPassword = "";

	private static Connection conn = null;
	private static Statement stmt = null;

	private static boolean initialized = false;

	public static String getNameFromMCUsername(String username){
		if (initialized) {
			try {
				String sql = "select display_name from mc_name_maps where mc_username='" + username + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("display_name");
				}

				ServerMod.logger.error("No name found for username");
				return "";

			} catch (SQLException e) {
				ServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			ServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}
	
	public static boolean checkLicenseActive(String licenseKey) {
		if (initialized) {
			try {
				String sql = "select * from mc_user_license where license_key='" + licenseKey + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {

					if (rs.getDate("start_dt").before(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))) {
						ServerMod.logger.error("License is not active yet");
						return false;
					}

					if (rs.getDate("end_dt").before(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))) {
						ServerMod.logger.error("License is expired");
						return false;
					}

					return true;
				}
				ServerMod.logger.error("No entry found for license");
				return false;

			} catch (SQLException e) {
				ServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			ServerMod.logger.error("Database Manager not initialized");
		}
		return false;
	}

	public static int getIdFromLicenseKey(String licenseKey) {
		if (initialized) {
			if (checkLicenseActive(licenseKey)) {
				try {

					String sql = "select user_id from mc_user_license where license_key='" + licenseKey + "'";

					ResultSet rs;

					rs = stmt.executeQuery(sql);

					if (rs.next()) {
						return rs.getInt("user_id");
					}

					ServerMod.logger.error("No entry found for license");
					return -1;

				} catch (SQLException e) {
					ServerMod.logger.error("Could not execute database request");
					e.printStackTrace();
				}
			}
		} else {
			ServerMod.logger.error("Database Manager not initialized");
		}
		return -1;
	}

	public static String getLicenceFromUsernameAndPassword(String username, String password) {
		if (initialized) {
			try {
				String sql = "select license_key from mc_license where username='" + username + "' and password='"
						+ password + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("license_key");
				}

				ServerMod.logger.error("No license found for username and password");
				return "";

			} catch (SQLException e) {
				ServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			ServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}

	public static Pair<String, String> getMinecraftCredentials(int id) {
		if (initialized) {
			try {
				String sql = "select * from mc_minecraft_license where id=" + id + "";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return new Pair<String, String>(rs.getString("email"), rs.getString("password"));
				}

				ServerMod.logger.error("No entry found for license");
				return null;

			} catch (SQLException e) {
				ServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			ServerMod.logger.error("Database Manager not initialized");
		}
		return null;
	}

	public static void init() {
		try {
			// Dynamically load driver at runtime.
			// Redshift JDBC 4.1 driver: com.amazon.redshift.jdbc41.Driver
			// Redshift JDBC 4 driver: com.amazon.redshift.jdbc4.Driver
			Class.forName("com.amazon.redshift.jdbc41.Driver");

			// Open a connection and define properties.
			System.out.println("Connecting to database...");
			Properties props = new Properties();

			// Uncomment the following line if using a keystore.
			// props.setProperty("ssl", "true");
			props.setProperty("user", MasterUsername);
			props.setProperty("password", MasterUserPassword);
			conn = DriverManager.getConnection(dbURL, props);

			stmt = conn.createStatement();
			initialized = true;

		} catch (ClassNotFoundException e) {
			ServerMod.logger.error("Failed to load database class, jar may be missing");
			e.printStackTrace();
		} catch (SQLException e) {
			ServerMod.logger.error("Failed to initialize SQL connection to database");
			e.printStackTrace();
		}
	}

}
