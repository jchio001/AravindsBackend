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
			String select_sql = "SELECT src_zip, dest_zip FROM Profile where user_id = ?";
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
			int src_zip;
			int dest_zip;
			ResultSet rs = stmt.executeQuery();
			//Think of it like this: each item in the DB is an item in the RS.

			if (rs.next()) {
				src_zip = rs.getInt(Constants.SRC_ZIP);
				dest_zip = rs.getInt(Constants.DEST_ZIP);
			}
			else {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			rs = getSuggestionResults(stmt, connection, src_zip, dest_zip, req_id);
			resp.getWriter().print(getJSONArr(rs));
		}
		catch (SQLException|JSONException e){
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static ResultSet getSuggestionResults(PreparedStatement stmt, Connection connection, int src_zip, int dest_zip, Long req_id)
	throws SQLException{
		int min_src = src_zip - 2;
		int max_src = src_zip + 2;
		int min_dest = dest_zip - 2;
		int max_dest = dest_zip + 2;
		String select_sql = "Select user_id, name, src_zip, dest_zip, gender FROM Profile where src_zip BETWEEN ? and ? and user_id != ? " +
				"and visible = ? and dest_zip BETWEEN ? and ? ORDER BY user_id DESC";
		stmt = connection.prepareStatement(select_sql);
		stmt.setInt(1, min_src);
		stmt.setInt(2, max_src);
		stmt.setLong(3, req_id);
		stmt.setBoolean(4, true);
		stmt.setInt(5, min_dest);
		stmt.setInt(6, max_dest);
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
			user.put(Constants.SRC_ZIP, rs.getInt(Constants.SRC_ZIP));
			user.put(Constants.DEST_ZIP, rs.getInt(Constants.DEST_ZIP));
			user.put(Constants.GENDER, rs.getString(Constants.GENDER));
			suggestionArr.put(user);
		}
		return suggestionArr.toString();
	}
}
