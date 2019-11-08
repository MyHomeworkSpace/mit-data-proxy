package space.myhomework.proxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class App {
	public static Connection connection;

	public static void panic(String message) {
		System.err.println(message);
		System.exit(1);
	}

	public static String getPropertyOrCrash(Properties config, String key) {
		String value = config.getProperty(key);
		if (value == null) {
			panic("Config file missing required property '" + key + "'; exiting!");
			return null;
		}
		return value;
	}

	public static void handleFetch(Context ctx) {
		try {
			String source = ctx.queryParam("source");
			String lastUpdateDateInput = ctx.queryParam("lastUpdateDate");
			String limitInput = ctx.queryParam("limit");
			String termCode = ctx.queryParam("termCode");

			if (source == null || source.isBlank()) {
				ctx.json(new ErrorResponse("error", "missing_params"));
				return;
			}

			String lastUpdateDate = "1970-01-01";
			if (lastUpdateDateInput != null && !lastUpdateDateInput.isEmpty()) {
				SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date parsed = parser.parse(lastUpdateDateInput);
					lastUpdateDate = parser.format(parsed);
				} catch (ParseException e) {
					ctx.json(new ErrorResponse("error", "invalid_params"));
					return;
				}
			}

			int limit = -1;
			if (limitInput != null && !limitInput.isEmpty()) {
				try {
					limit = Integer.parseInt(limitInput);
				} catch (NumberFormatException e) {
					ctx.json(new ErrorResponse("error", "invalid_params"));
					return;
				}
			}

			if (source.equals("catalog")) {
				ArrayList<CatalogListing> listings = new ArrayList<CatalogListing>();

				PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM cis_course_catalog WHERE ACADEMIC_YEAR = ? AND LAST_ACTIVITY_DATE > to_date(?, 'YYYY-MM-DD')"
				);
				stmt.setString(1, lastUpdateDate);
				ResultSet rs = stmt.executeQuery();

				int count = 0;
				while (rs.next()) {
					listings.add(new CatalogListing(rs));
					count++;
					if (limit != -1 && count >= limit) {
						break;
					}
				}

				ctx.json(listings);
			} else if (source.equals("offerings")) {
				if (termCode == null || termCode.isEmpty()) {
					ctx.json(new ErrorResponse("error", "missing_params"));
					return;
				}

				ArrayList<SubjectOffering> offerings = new ArrayList<SubjectOffering>();

				PreparedStatement stmt = connection.prepareStatement(
					"SELECT * FROM subject_offered WHERE term_code = ?"
				);
				stmt.setString(1, termCode);
				ResultSet rs = stmt.executeQuery();

				int count = 0;
				while (rs.next()) {
					offerings.add(new SubjectOffering(rs));
					count++;
					if (limit != -1 && count >= limit) {
						break;
					}
				}

				ctx.json(offerings);
			} else {
				ctx.json(new ErrorResponse("error", "invalid_params"));
			}
		} catch (SQLException e) {
			System.out.println(e);
			ctx.json(new ErrorResponse("error", "internal_server_error"));
		}
	}

	public static void handleConnect(Context ctx) {
		System.out.println("handleConnect");
		ctx.json("wheeeeee");
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		Class.forName("oracle.jdbc.OracleDriver");

		Properties config = new Properties();
		FileInputStream configInput = null;
		try {
			configInput = new FileInputStream("app.config");
		} catch (FileNotFoundException ex) {
			panic("Couldn't find config file 'app.config'; exiting!");
			return;
		}
		config.load(configInput);

		String dbHost = getPropertyOrCrash(config, "db.host");
		int dbPort = Integer.parseInt(getPropertyOrCrash(config, "db.port"));
		String dbSid = getPropertyOrCrash(config, "db.sid");
		String dbUsername = getPropertyOrCrash(config, "db.username");
		String dbPassword = getPropertyOrCrash(config, "db.password");

		String authToken = getPropertyOrCrash(config, "auth.token");

		String dbURL = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbSid;
		connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
		if (connection == null) {
			panic("Couldn't connect, connection == null!");
			return;
		}

		Javalin app = Javalin.create(javalinConfig -> {
			javalinConfig.server(() -> {
				Server server = new Server();

				HandlerCollection collection = new HandlerCollection();
				collection.addHandler(new AuthenticationHandler(authToken));
				collection.addHandler(new ProxyConnectHandler());

				server.setHandler(collection);

				return server;
			});
		}).start(7000);
		app.get("/", ctx -> ctx.redirect("https://myhomework.space", 302));
		app.get("/ping", ctx -> ctx.json(new StatusResponse("ok")));
		app.get("/fetch", App::handleFetch);
		app.addHandler(HandlerType.CONNECT, "*", App::handleConnect);
	}
}
