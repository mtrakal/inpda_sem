package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.mtrakal.inpda_sem.controller.Hodnoceni;
import cz.mtrakal.inpda_sem.controller.Uzivatel;

public class HodnoceniModel {
	Connection conn;

	public HodnoceniModel() throws SQLException {
		conn = new ConnectionModel().getConnection();
	}

	public List<Hodnoceni> getHodnoceni() throws SQLException {
		String query = "select film_id, to_char(datum_hodnoceni, 'YYYY-MM-DD'), hvezdy, popis from hodnoceni";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Hodnoceni> hodnoceni = new ArrayList<Hodnoceni>();

		while (rset.next()) {
			hodnoceni.add(new Hodnoceni(rset.getInt(1), rset.getDate(2), rset.getInt(3), rset.getString(4)));
		}
		return hodnoceni;
	}

	@Override
	protected void finalize() throws Throwable {
		// conn.close();
		conn = null;
		super.finalize();
	}

	public void update(Hodnoceni data) throws SQLException {
		String query = "update hodnoceni set hvezdy=" + data.getHvezdy() + ", popis='" + data.getPopis() + "' where film_id="
				+ data.getFilmId() + " and to_char(datum_hodnoceni, 'YYYY-MM-DD')='"
				+ String.format("%1$tY-%1$tm-%1$td", data.getDatumHodnoceni()) + "'";
		System.out.println(query);
		System.out.println(query);
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void insert(Hodnoceni data) throws SQLException {
		String query = "insert into hodnoceni (film_id, datum_hodnoceni, hvezdy, popis) values (" + data.getFilmId() + ", TO_DATE('"
				+ String.format("%1$tY-%1$tm-%1$td", data.getDatumHodnoceni()) + "', 'YYYY-MM-DD'), " + data.getHvezdy() + ", '"
				+ data.getPopis() + "')";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void delete(Hodnoceni data) throws SQLException {
		delete(data.getFilmId(), data.getDatumHodnoceni());
	}

	public void delete(Integer filmId, Date datum) throws SQLException {
		String query = "delete from hodnoceni where film_id=" + filmId + " and TO_CHAR(datum_hodnoceni, 'YYYY-MM-DD')='"
				+ String.format("%1$tY-%1$tm-%1$td", datum) + "'";
		System.err.println(query);
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public Map<String, Float> getProcentaHodnoceni() throws SQLException {
		String query = "select hodnoceni.hvezdy, trunc( ratio_to_report(count(hodnoceni.hvezdy)) over() * 100, 2) as procenta from hodnoceni group by hodnoceni.hvezdy";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Map<String, Float> procenta = new HashMap<String, Float>();

		while (rset.next()) {
			procenta.put(rset.getString(1), rset.getFloat(2));
		}
		return procenta;
	}
}