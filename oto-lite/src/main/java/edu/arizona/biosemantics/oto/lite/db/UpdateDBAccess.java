package edu.arizona.biosemantics.oto.lite.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.common.security.Encryptor;

public class UpdateDBAccess extends AbstractDBAccess {
	private static final Logger LOGGER = Logger
			.getLogger(GeneralDBAccess.class);

	private static UpdateDBAccess instance;

	private UpdateDBAccess() {

	}

	public static UpdateDBAccess getInstance() {
		if (instance == null) {
			instance = new UpdateDBAccess();
		}
		return instance;
	}

	/**
	 * add secret to table [uploads]
	 * 
	 * @throws SQLException
	 */
	public void updateDB_secret() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			conn.setAutoCommit(false);
			String sql = "show columns from uploads where Field = 'secret';";
			rset = stmt.executeQuery(sql);
			if (!rset.next()) {
				// add field
				sql = "alter table uploads add secret varchar(50);";
				stmt.executeUpdate(sql);

				ArrayList<String> uploads = new ArrayList<String>();
				sql = "select uploadID from uploads";
				rset = stmt.executeQuery(sql);
				while (rset.next()) {
					uploads.add(rset.getString(1));
				}

				for (String uploadId : uploads) {
					String secret = Encryptor.getInstance().encrypt(uploadId);
					// since this key will be used in URL, cannot have special characters
					// replace special characters with 0
					secret = secret.replaceAll("[^0-9A-Za-z]", "0");
					sql = "update uploads set secret = '" + secret
							+ "' where uploadID = " + uploadId;
					stmt.executeUpdate(sql);
				}
			}
			conn.commit();
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in GeneralDBAccess: getGlossaryType " + exe);
		} finally {
			closeConnection(stmt, rset, conn);
		}
	}
}
