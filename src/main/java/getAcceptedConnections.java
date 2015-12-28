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
import java.util.ArrayList;

/**
 * Created by jman0_000 on 12/27/2015.
 */
public class getAcceptedConnections {
	public static void getAcceptedConnections(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id)
	throws IOException {
		Long src_id;
		try {
			src_id = Long.parseLong(id);
		}
		catch (NumberFormatException e) {
			resp.setStatus(Constants.BAD_REQUEST);
			return;
		}
		try {
			ResultSet rs = runSelectQuery(connection, src_id);
			ArrayList<Long> id_arr = parseIDs(rs, src_id);
			if (id_arr == null) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}
			resp.getWriter().print(makeUserJSON(connection, id_arr));
		}
		catch (SQLException| JSONException e) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static ResultSet runSelectQuery(Connection connection, Long id) throws SQLException{
		String select_sql = "Select * from Connections where (requester_id = ? or target_id = ?) " +
				"and status = ?";
		PreparedStatement stmt = connection.prepareStatement(select_sql);
		stmt.setLong(1, id);
		stmt.setLong(2, id);
		stmt.setString(3, Constants.ACCEPTED);
		return stmt.executeQuery();
	}

	public static ArrayList<Long> parseIDs(ResultSet rs, Long src_id) throws SQLException{
		ArrayList<Long> id_list = new ArrayList<Long>();
		Long req_id, target_id;
		while (rs.next()) {
			req_id = rs.getLong(Constants.REQ_ID);
			target_id = rs.getLong(Constants.TARGET_ID);
			if (src_id.equals(req_id))
				id_list.add(rs.getLong(Constants.TARGET_ID));
			else if (src_id.equals(target_id))
				id_list.add(rs.getLong(Constants.REQ_ID));
			else
				return null;
		}
		return id_list;
	}

	public static String makeUserJSON(Connection connection, ArrayList<Long> id_list) throws SQLException, JSONException{
		String select_sql = "Select user_id, name, zip_code, village from Profile where user_id = ANY (?)";
		PreparedStatement stmt = connection.prepareStatement(select_sql);
		stmt.setArray(1, connection.createArrayOf("bigint", id_list.toArray()));
		ResultSet rs  = stmt.executeQuery();
		JSONArray userArr = new JSONArray();
		JSONObject user;
		while (rs.next()) {
			user = new JSONObject();
			user.put(Constants.USER_ID, rs.getLong(Constants.USER_ID));
			user.put(Constants.STATUS, Constants.ACCEPTED);
			userArr.put(user);
		}
		return userArr.toString();
	}
}
