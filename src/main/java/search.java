import Misc.Constants;
import org.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jman0_000 on 1/16/2016.
 */
public class search {
	public static void doSearch(HttpServletRequest req, HttpServletResponse resp, Connection connection, String requester,
							  String gender, String range, String src, String dest) throws IOException {
		//req/gender/range/src/dest
		try {
			long req_id;
			int zip_range, src_zip, dest_zip;
			try {
				req_id = Long.parseLong(requester);
				src_zip = Integer.parseInt(src);
				dest_zip = Integer.parseInt(dest);
				zip_range = Integer.parseInt(range);
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				resp.getWriter().print(e.getMessage());
				return;
			}
			String update_sql;
			if (gender.equals(Constants.NO_GENDER_PREF)) {
				update_sql = "Select user_id, name, src_zip, dest_zip, gender FROM Profile where src_zip BETWEEN ? and ? and " +
						"dest_zip BETWEEN ? and ? and user_id != ? " +
						"and visible = ? ORDER BY user_id DESC";
			}
			else {
				update_sql = "Select user_id, name, src_zip, dest_zip, gender FROM Profile where src_zip BETWEEN ? and ? and " +
						"dest_zip BETWEEN ? and ? and user_id != ? " +
						"and visible = ? and gender = ? ORDER BY user_id DESC";
			}
			PreparedStatement stmt = connection.prepareStatement(update_sql);
			ResultSet rs = getResultSet(connection, stmt, zip_range, src_zip, dest_zip, req_id, gender);
			resp.getWriter().print(getSuggestions.getJSONArr(rs));
		}
		catch (SQLException |JSONException e) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static ResultSet getResultSet(Connection connection, PreparedStatement stmt, int zip_range, int src_zip, int dest_zip, long req_id, String gender)
			throws SQLException {
		stmt.setInt(1, src_zip - zip_range);
		stmt.setInt(2, src_zip + zip_range);
		stmt.setInt(3, dest_zip - zip_range);
		stmt.setInt(4, dest_zip + zip_range);
		stmt.setLong(5, req_id);
		stmt.setBoolean(6, true);
		if (!gender.equals(Constants.NO_GENDER_PREF))
			stmt.setString(7, gender);
		return stmt.executeQuery();
	}
}
