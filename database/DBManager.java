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

import com.dyn.DYNServerMod;

public class DBManager {

	private static Connection conn = null;
	private static Statement stmt = null;

	private static boolean initialized = false;

	public static boolean checkLicenseActive(String licenseKey) {
		if (initialized) {
			try {
				String sql = "select * from mc_licenses where license_key='" + licenseKey + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {

					if (rs.getDate("start_dt").before(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))) {
						DYNServerMod.logger.error("License is not active yet");
						return false;
					}

					if (rs.getDate("end_dt").before(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))) {
						DYNServerMod.logger.error("License is expired");
						return false;
					}

					return true;
				}
				DYNServerMod.logger.error("No entry found for license");
				return false;

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return false;
	}

	public static int getIdFromLicenseKey(String licenseKey) {
		if (initialized) {
			if (checkLicenseActive(licenseKey)) {
				try {

					String sql = "select mc_license_id from mc_user_license where license_key='" + licenseKey + "'";

					ResultSet rs;

					rs = stmt.executeQuery(sql);

					if (rs.next()) {
						return rs.getInt("mc_license_id");
					}

					DYNServerMod.logger.error("No entry found for license");
					return -1;

				} catch (SQLException e) {
					DYNServerMod.logger.error("Could not execute database request");
					e.printStackTrace();
				}
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return -1;
	}

	public static String getLicenceFromUsernameAndPassword(String username, String password) {
		if (initialized) {
			try {
				String sql = "select ml.license_key from users u join mc_licenses ml on u.mc_license_id = ml.mc_license_id where u.launcher_username = '"
						+ username +"' and u.launcher_password = '" + password + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("license_key");
				}

				DYNServerMod.logger.error("No license found for username and password");
				return "";

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}

	public static Pair<String, String> getMinecraftCredentials(int id) {
		if (initialized) {
			try {
				String sql = "select * from mc_account where mc_account_id=" + id + "";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return new Pair<String, String>(rs.getString("email"), rs.getString("password"));
				}

				DYNServerMod.logger.error("No entry found for license");
				return null;

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return null;
	}

	public static String getNameFromMCUsername(String username) {
		if (initialized) {
			try {
				String sql = "select display_name from mc_name_map where mc_name='" + username + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("display_name");
				}

				DYNServerMod.logger.error("No name found for username");
				return "";

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}

	public static String getPasswordFromDYNUsername(String username) {
		if (initialized) {
			try {
				String sql = "select launcher_password from users where launcher_username='" + username + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("launcher_password");
				}

				DYNServerMod.logger.error("No password found for username");
				return "";

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}

	public static String getPlayerStatus(String username) {
		if (initialized) {
			try {
				String sql = "select license_type from mc_account where mc_name='" + username + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("license_type");
				}

				DYNServerMod.logger.error("No license found for username");
				return "";

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return "";
	}

	public static void init(String url, String username, String password) {
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
			props.setProperty("user", username);
			props.setProperty("password", password);
			conn = DriverManager.getConnection(url, props);

			stmt = conn.createStatement();
			initialized = true;

			DYNServerMod.logger.info("Database Initialized");

		} catch (ClassNotFoundException e) {
			DYNServerMod.logger.error("Failed to load database class, jar may be missing");
			e.printStackTrace();
		} catch (SQLException e) {
			DYNServerMod.logger.error("Failed to initialize SQL connection to database");
			e.printStackTrace();
		}
	}

}
