import Misc.Constants;
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

/**
 * Created by jman0_000 on 12/25/2015.
 */
public class getSuggestions {
	public static void getSuggestions(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id) throws IOException {
		try {
			String select_sql = "SELECT village, zip_code FROM Profile where user_id = ?";
			long req_id;
			PreparedStatement stmt = connection.prepareStatement(select_sql);
			try {
				req_id = Long.parseLong(id);
				stmt.setLong(1, req_id);
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}
			String village;
			int zip_code;
			ResultSet rs = stmt.executeQuery();
			//Think of it like this: each item in the DB is an item in the RS.

			if (rs.next()) {
				village = rs.getString(Constants.VILLAGE);
				zip_code = rs.getInt(Constants.ZIP_CODE);
			}
			else {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			rs = getSuggestionResults(stmt, connection, village, req_id, zip_code);
			resp.getWriter().print(getJSONArr(rs));
		}
		catch (SQLException|JSONException e){
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static ResultSet getSuggestionResults(PreparedStatement stmt, Connection connection, String village, Long req_id, int zip_code)
	throws SQLException{
		int min_zip = zip_code - 2;
		int max_zip = zip_code + 2;
		String select_sql = "Select user_id, name, village, zip_code FROM Profile where village = ? and user_id != ? " +
				"and visible = ? and zip_code BETWEEN ? and ? ORDER BY user_id DESC";
		stmt = connection.prepareStatement(select_sql);
		stmt.setString(1, village);
		stmt.setLong(2, req_id);
		stmt.setBoolean(3, true);
		stmt.setInt(4, min_zip);
		stmt.setInt(5, max_zip);
		ResultSet rs  = stmt.executeQuery();
		return rs;
	}

	public static String getJSONArr(ResultSet rs) throws JSONException, SQLException{
		JSONArray suggestionArr = new JSONArray();
		JSONObject user;
		while (rs.next()) {
			user = new JSONObject();
			user.put(Constants.USER_ID, rs.getInt(Constants.USER_ID));
			user.put(Constants.NAME, rs.getString(Constants.NAME));
			user.put(Constants.VILLAGE, rs.getString(Constants.VILLAGE));
			user.put(Constants.ZIP_CODE, rs.getInt(Constants.ZIP_CODE));
			suggestionArr.put(user);
		}
		return suggestionArr.toString();
	}
}
