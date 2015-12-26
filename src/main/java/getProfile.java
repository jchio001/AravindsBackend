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
 * Created by jman0_000 on 12/24/2015.
 */
public class getProfile {
	public static void getProfile(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id1, String id2) throws IOException {
		try {
			String select_sql = "Select * from Connections where (requester_id = ? and target_id = ?) or (requester_id = ? and target_id = ?)";
			PreparedStatement stmt = connection.prepareStatement(select_sql);
			Long req_id, target_id;
			try {
				req_id = Long.parseLong(id1);
				target_id = Long.parseLong(id2);
				stmt.setLong(1, req_id);
				stmt.setLong(2, target_id);
				stmt.setLong(3, target_id);
				stmt.setLong(4, req_id);
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}
			resp.getWriter().print("Getting Status!\n");
			String status = getConnStatus(resp, connection, id1, id2);
			resp.getWriter().print("Done!\n");
			resp.getWriter().print("Status: " + status);
		}
		catch (SQLException e) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}

	public static String getConnStatus(HttpServletResponse resp, Connection connection, String id1, String id2) throws SQLException, IOException{
		resp.getWriter().print("Calling getConStatus with id1: " + id1 + ", id2: " + id2 + "\n");
		String select_sql = "Select * from Connections where (requester_id = ? and target_id = ?) or (requester_id = ? and target_id = ?)";
		PreparedStatement stmt = connection.prepareStatement(select_sql);
		Long req_id, target_id;
		try {
			req_id = Long.parseLong(id1);
			target_id = Long.parseLong(id2);
			stmt.setLong(1, req_id);
			stmt.setLong(2, target_id);
			stmt.setLong(3, target_id);
			stmt.setLong(4, req_id);
		}
		catch (NumberFormatException e) {
			resp.setStatus(Constants.BAD_REQUEST);
			return "";
		}

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return getStatusString(req_id, target_id, rs);
		}
		return Constants.REL_UNCONNECTED;
	}

	public static String getStatusString(Long req_id, Long target_id, ResultSet rs) throws SQLException{
		Long connReq = rs.getLong(Constants.REQ_ID);
		Long connTarget = rs.getLong(Constants.TARGET_ID);
		String status = rs.getString(Constants.STATUS);
		if (status.equals(Constants.ACCEPTED))
			return Constants.REL_ACCEPTED;

		if (req_id == connReq) {
			if (status.equals(Constants.PENDING))
				return Constants.REL_PENDING;
			else if (status.equals(Constants.REJECTED))
				return Constants.REL_REJECTED;
			else
				return "";
		}
		else if (req_id == connTarget){
			if (status.equals(Constants.PENDING))
				return Constants.REL_AWAITING_YOUR_APPROVAL;
			else if (status.equals(Constants.REJECTED))
				return Constants.REL_REJECTED_BY_YOU;
			else
				return "";
		}
		else
			return "";
	}
}
