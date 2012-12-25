package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.mtrakal.inpda_sem.controller.Uzivatel;

public class UzivateleModel {
	Connection conn = null;

	public UzivateleModel() throws SQLException {
		conn = new ConnectionModel().getConnection();
	}

	public List<Uzivatel> getUzivatele() throws SQLException {
		String query = "select uzivatel_id, email, heslo, prava from uzivatele";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Uzivatel> uzivatele = new ArrayList<Uzivatel>();

		while (rset.next()) {
			uzivatele.add(new Uzivatel(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getInt(4)));
		}
		return uzivatele;
	}

	public List<Uzivatel> getUzivatele(String email, String heslo) throws SQLException {
		String query = "select uzivatel_id, email, heslo, prava from uzivatele where email='" + email + "' and heslo='" + heslo + "'";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Uzivatel> uzivatele = new ArrayList<Uzivatel>();

		while (rset.next()) {
			uzivatele.add(new Uzivatel(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getInt(4)));
		}
		stmt.close();
		return uzivatele;
	}

	@Override
	protected void finalize() throws Throwable {
		// conn.close();
		conn = null;
		super.finalize();
	}

	public void update(Uzivatel uzivatel) throws SQLException {
		String query = "update uzivatele set email='" + uzivatel.getEmail() + "', heslo='" + uzivatel.getHeslo() + "', prava="
				+ uzivatel.getPrava() + " where uzivatel_id='" + uzivatel.getUzivatelId() + "'";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void insert(Uzivatel uzivatel) throws SQLException {
		String query = "insert into uzivatele (uzivatel_id, email, heslo, prava) values (" + uzivatel.getUzivatelId() + ", '"
				+ uzivatel.getEmail() + "', '" + uzivatel.getHeslo() + "', prava=" + uzivatel.getPrava() + ")";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void delete(Uzivatel uzivatel) throws SQLException {
		delete(uzivatel.getUzivatelId());
	}

	public void delete(Integer uzivatelId) throws SQLException {
		String query = "delete from uzivatele where uzivatel_id=" + uzivatelId;
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}
}
