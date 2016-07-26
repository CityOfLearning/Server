package com.dyn.server.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import com.dyn.DYNServerMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DBManager {

	private static Connection conn = null;
	private static Statement stmt = null;

	private static boolean initialized = false;

	public static boolean checkLicenseActive(String licenseKey) {
		if (initialized) {
			try {
				String sql = "select * from mc_user_license where license_key='" + licenseKey + "'";

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

	public static JsonObject getAchievementDBAsJson() {
		if (initialized) {
			try {
				JsonObject reply = new JsonObject();
				String sql = "select ach_id from achievements";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				List<Integer> achievement_ids = new ArrayList<Integer>();

				while (rs.next()) {
					achievement_ids.add(rs.getInt("ach_id"));
				}

				Collections.sort(achievement_ids);

				JsonArray achievments = new JsonArray();

				for (Integer id : achievement_ids) {
					JsonObject achievement = new JsonObject();
					sql = "select * from achievements where ach_id=" + id;

					rs = stmt.executeQuery(sql);
					rs.next();

					// build the achievement body first

					achievement.addProperty("name", rs.getString("name"));
					achievement.addProperty("desc", rs.getString("description"));
					achievement.addProperty("ach_id", id);
					achievement.addProperty("map_id", rs.getInt("map_id"));
					achievement.addProperty("world", rs.getInt("world"));
					achievement.addProperty("x_coord", rs.getInt("x_coord"));
					achievement.addProperty("y_coord", rs.getInt("y_coord"));

					if ((rs.getString("texture") != null) || !rs.getString("texture").equals("null")) {
						achievement.addProperty("texture", rs.getString("texture"));
					}
					if ((rs.getInt("ccol_badge_id") > 0) && (rs.getInt("ccol_org_id") > 0)) {
						JsonObject badgeObj = new JsonObject();
						badgeObj.addProperty("org_id", rs.getInt("ccol_org_id"));
						badgeObj.addProperty("badge_id", rs.getInt("ccol_badge_id"));
						achievement.add("badge", badgeObj);
					}
					if (rs.getInt("parent_ach") > 0) {
						achievement.addProperty("parent_ach", rs.getInt("parent_ach"));
					}

					// now we need to build all the requirements

					JsonObject req = new JsonObject();

					sql = "select req_type from achievement_requirements where ach_id=" + id;

					rs = stmt.executeQuery(sql);

					List<String> types = new ArrayList<String>();

					while (rs.next()) {
						types.add(rs.getString("req_type"));
					}

					for (String type : types) {
						JsonArray reqTypes = new JsonArray();
						switch (type) {
						case "CRAFT":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
									
								reqTypes.add(reqSubTypes);
							}
							req.add("craft_requirements", reqTypes);
							break;
						case "SMELT":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("smelt_requirements", reqTypes);
							break;
						case "PICKUP":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("pick_up_requirements", reqTypes);

							break;
						case "KILL":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("entity", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("kill_requirements", reqTypes);

							break;
						case "BREW":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("brew_requirements", reqTypes);

							break;
						case "PLACE":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("place_requirements", reqTypes);

							break;
						case "BREAK":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();
								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("item", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));
								reqSubTypes.addProperty("item_id", rs.getInt("item_id"));
								reqSubTypes.addProperty("sub_id", rs.getInt("item_sub_id"));
								if(rs.getObject("zone_ids") != null && rs.getString("zone_ids").length()>0){
									JsonArray jArr = new JsonArray();
									for(String ids : rs.getString("zone_ids").split(Pattern.quote(","))){
										jArr.add(new JsonPrimitive(ids));
									}
									reqSubTypes.add("zones", jArr);
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("break_requirements", reqTypes);

							break;
						case "MENTOR":
							req.add("mentor_requirements", reqTypes);
							break;
						case "LOCATION":
							sql = "select * from achievement_requirements where ach_id=" + id + " and req_type='" + type
									+ "'";

							rs = stmt.executeQuery(sql);

							while (rs.next()) {
								JsonObject reqSubTypes = new JsonObject();

								reqSubTypes.addProperty("id", rs.getInt("req_id"));
								reqSubTypes.addProperty("name", rs.getString("entity"));
								reqSubTypes.addProperty("amount", rs.getInt("amount"));

								reqSubTypes.addProperty("x", rs.getInt("loc_x"));
								reqSubTypes.addProperty("y", rs.getInt("loc_y"));
								reqSubTypes.addProperty("z", rs.getInt("loc_z"));
								if (rs.getInt("loc_r") > 0) { 
									// null values become 0
									reqSubTypes.addProperty("radius", rs.getInt("loc_r"));
								} else {
									reqSubTypes.addProperty("x2", rs.getInt("loc_x2"));
									reqSubTypes.addProperty("y2", rs.getInt("loc_y2"));
									reqSubTypes.addProperty("z2", rs.getInt("loc_z2"));
								}
								reqTypes.add(reqSubTypes);
							}
							req.add("location_requirements", reqTypes);
							break;
						default:
							break;
						}
						achievement.add("requirements", req);
					}
					achievments.add(achievement);
				}
				reply.add("achievements", achievments);

				return reply;

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return null;
	}

	public static JsonObject getAchievementMapDBAsJson() {
		if (initialized) {
			try {
				JsonObject reply = new JsonObject();
				String sql = "select map_id from achievement_maps";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				List<Integer> map_ids = new ArrayList<Integer>();

				while (rs.next()) {
					map_ids.add(rs.getInt("map_id"));
				}

				Collections.sort(map_ids);

				JsonArray maps = new JsonArray();

				for (Integer id : map_ids) {
					JsonObject map = new JsonObject();
					sql = "select * from achievement_maps where map_id=" + id;

					rs = stmt.executeQuery(sql);
					rs.next();

					map.addProperty("map_id", id);
					map.addProperty("name", rs.getString("name"));

					if ((rs.getString("icon_texture") == null) || !rs.getString("icon_texture").equals("null")) {
						map.add("texture", JsonNull.INSTANCE);
					} else {
						map.addProperty("texture", rs.getString("icon_texture"));
					}
					maps.add(map);
				}

				reply.add("achievement_maps", maps);

				return reply;

			} catch (SQLException e) {
				DYNServerMod.logger.error("Could not execute database request");
				e.printStackTrace();
			}
		} else {
			DYNServerMod.logger.error("Database Manager not initialized");
		}
		return null;
	}

	// this can be a null value
	public static UUID getCCOLId(UUID user_id) {
		if (initialized) {
			try {
				String sql = "select ccol_id from users where user_id='" + user_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					if (rs.getString("ccol_id") != null) {
						return UUID.fromString(rs.getString("ccol_id"));
					}
				}

				DYNServerMod.logger.error("No ccol id found for user id");
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

	public static String getDisplayName(UUID user_id) {
		if (initialized) {
			try {
				String sql = "select display_name from users where user_id='" + user_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return rs.getString("display_name");
				}

				DYNServerMod.logger.error("No name found for user id");
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

	public static String getDisplayNameFromMCUsername(String mc_username) {
		return getDisplayName(getUserIDFromMCUsername(mc_username));
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

	// this can be a null value
	public static JsonObject getInfoFromCCOLAccount(UUID ccol_id) {
		if (initialized) {
			try {
				JsonObject reply = new JsonObject();
				String sql = "select * from ccol_account where ccol_id='" + ccol_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					reply.addProperty("full_name", rs.getString("full_name"));
					reply.addProperty("username", rs.getString("username"));
					reply.addProperty("program_id", rs.getInt("program_id"));
					return reply;
				}

				DYNServerMod.logger.error("No ccol id found for user id");
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

	// this can be a null value
	public static JsonObject getInfoFromUserAccount(UUID user_id) {
		if (initialized) {
			try {
				JsonObject reply = new JsonObject();
				String sql = "select * from users where user_id='" + user_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					reply.addProperty("ccol_id", rs.getString("ccol_id"));
					reply.addProperty("user_type", rs.getString("user_type"));
					reply.addProperty("display_name", rs.getString("display_name"));
					reply.addProperty("username", rs.getString("launcher_username"));
					reply.addProperty("password", rs.getString("launcher_password"));
					return reply;
				}

				DYNServerMod.logger.error("No ccol id found for user id");
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

	public static String getMCUsernameFromUserId(UUID user_id) {
		if (initialized) {
			try {
				String sql = "select mc_account_id from user_registrations where user_id='" + user_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					sql = "select mc_name from mc_account where mc_account_id='" + rs.getString("mc_account_id") + "'";
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						return rs.getString("mc_name");
					}
				}

				DYNServerMod.logger.error("No name found for user id");
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

	public static UUID getUserIdFromCCOLId(UUID ccol_id) {
		if (initialized) {
			try {
				String sql = "select user_id from users where ccol_id='" + ccol_id + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					return UUID.fromString(rs.getString("user_id"));
				}

				DYNServerMod.logger.error("No user found for CCOL account");
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

	public static UUID getUserIDFromMCUsername(String username) {
		if (initialized) {
			try {
				// get accoutnt id, then check the registration and dates, then
				// user info
				String sql = "select mc_account_id from mc_account where mc_name='" + username + "'";

				ResultSet rs;

				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					sql = "select user_id from user_registrations where mc_account_id='" + rs.getString("mc_account_id")
							+ "' and '" + LocalDateTime.now()
							+ "' between user_registrations.start_dt and user_registrations.end_dt";
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						return UUID.fromString(rs.getString("user_id"));
					}
				}

				DYNServerMod.logger.error("No license found for username");
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

	public static void init(String url, String username, String password) {
		try {
			// Dynamically load driver at runtime.
			// Redshift JDBC 4.1 driver: com.amazon.redshift.jdbc41.Driver
			// Redshift JDBC 4 driver: com.amazon.redshift.jdbc4.Driver
			Class.forName("com.amazon.redshift.jdbc41.Driver");

			// Open a connection and define properties.
			DYNServerMod.logger.info("Connecting to database...");
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
