package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.mtrakal.inpda_sem.controller.Filmoteka;

public class FilmotekaModel {
	Connection conn;

	public FilmotekaModel() throws SQLException {
		conn = new ConnectionModel().getConnection();
	}

	public List<Filmoteka> getFilmoteka() throws SQLException {
		String query = "select distinct filmoteka_id, film_id, umisteni, kvalita_id from filmoteka";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Filmoteka> film = new ArrayList<Filmoteka>();

		while (rset.next()) {
			film.add(new Filmoteka(rset.getInt(1), rset.getInt(2), rset.getString(3), rset.getInt(4)));
		}
		return film;
	}

	public Filmoteka getFilmotekaById(Integer filmotekaId) throws SQLException {
		String query = "select filmoteka_id, film_id, umisteni, kvalita_id from filmoteka where filmoteka_id='" + filmotekaId + "'";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Filmoteka film = null;

		while (rset.next()) {
			film = new Filmoteka(rset.getInt(1), rset.getInt(2), rset.getString(3), rset.getInt(4));
		}
		return film;
	}
	
	public Map<String, Float> getProcentaFilmoteka() throws SQLException {
		String query = "select kvalita.kvalita, count(filmoteka.kvalita_id) pocet from filmoteka left join kvalita on kvalita.kvalita_id = filmoteka.kvalita_id  group by filmoteka.kvalita_id, kvalita.kvalita";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Map<String, Float> procenta = new HashMap<String, Float>();

		while (rset.next()) {
			procenta.put(rset.getString(1), rset.getFloat(2));
		}
		return procenta;
	}

	public void update(Filmoteka filmoteka) throws SQLException {
		String query = "update filmoteka set film_id=" + filmoteka.getFilmId() + ", umisteni='" + filmoteka.getUmisteni()
				+ "', kvalita_id=" + filmoteka.getKvalitaId() + " where filmoteka_id=" + filmoteka.getFilmotekaId();
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void insert(Filmoteka filmoteka) throws SQLException {
		String query = "insert into filmoteka (filmoteka_id, film_id, umisteni, kvalita_id) values (" + filmoteka.getFilmotekaId()
				+ ", " + filmoteka.getFilmId()+ ", '" + filmoteka.getUmisteni()+ "', " + filmoteka.getKvalitaId()+ ")";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void delete(Filmoteka film) throws SQLException {
		delete(film.getFilmId());
	}

	public void delete(Integer filmotekaId) throws SQLException {
		String query = "delete from filmoteka where filmoteka_id=" + filmotekaId;
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}
}
