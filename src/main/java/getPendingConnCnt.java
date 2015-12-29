import Misc.Constants;
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
 * Created by jman0_000 on 12/28/2015.
 */
public class getPendingConnCnt {
	public static void getPendingConnCnt(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id)
			throws IOException {
		try {
			Long src_id;
			try {
				src_id = Long.parseLong(id);
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			ResultSet rs = runSelectSQL(connection, src_id);
			if (rs.next())
				resp.getWriter().print(getCountJSON(rs));
			else
				resp.setStatus(Constants.BAD_REQUEST);
		}
		catch (SQLException|JSONException exception) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(Main.getStackTrace(exception));
		}
	}

	public static ResultSet runSelectSQL(Connection connection, long src_id) throws SQLException{
		String select_sql = "Select Count(*) from Connections WHERE target_id = ? and status = ?";
		PreparedStatement stmt = connection.prepareStatement(select_sql);
		stmt.setLong(1, src_id);
		stmt.setString(2, Constants.PENDING);
		ResultSet rs =  stmt.executeQuery();
		return rs;
	}

	public static String getCountJSON(ResultSet rs) throws SQLException, JSONException{
		JSONObject cnt = new JSONObject();
		cnt.put(Constants.COUNT, rs.getLong(Constants.COUNT));
		return cnt.toString();
	}
}
