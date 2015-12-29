import Misc.Constants;
import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by jman0_000 on 12/25/2015.
 */
public class sentOrRcvdConn {
	public static void getSentOrRcvdConns(HttpServletRequest req, HttpServletResponse resp, Connection connection, String mode, String id)
			throws IOException {
		try {
			PreparedStatement stmt = connection.prepareStatement(setUpSQL(mode));
			try {
				stmt.setLong(1, Long.parseLong(id));
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}
			ResultSet rs = stmt.executeQuery();
			resp.getWriter().print(setUpInfoJSON(rs, Long.parseLong(id)));
			stmt.close();
		}
		catch (SQLException|JSONException e) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static String setUpSQL(String mode) {
		if (mode.equals("sent")) {
			return "Select c.target_id, c.status, p.* from connections c " +
					"join profile p on p.user_id = c.target_id " +
					"where c.requester_id = ?;";
		}
		else {
			return "Select c.requester_id, c.status, p.* from connections c " +
					"join profile p on p.user_id = c.requester_id " +
					"where c.target_id = ?;";
		}
	}

	public static String setUpInfoJSON(ResultSet rs, Long id) throws SQLException, JSONException {
		JSONArray resultArr = new JSONArray();
		JSONObject userJSON;
		while (rs.next()) {
			userJSON = new JSONObject();
			userJSON.put(Constants.USER_ID, rs.getLong(Constants.USER_ID));
			userJSON.put(Constants.NAME, rs.getString(Constants.NAME));
			userJSON.put(Constants.VILLAGE, rs.getString(Constants.VILLAGE));
			userJSON.put(Constants.ZIP_CODE, rs.getInt(Constants.ZIP_CODE));
			userJSON.put(Constants.STATUS, rs.getString(Constants.STATUS));
			resultArr.put(userJSON);
		}
		return resultArr.toString();
	}
}
