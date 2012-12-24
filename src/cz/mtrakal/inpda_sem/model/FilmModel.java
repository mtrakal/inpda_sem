package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.mtrakal.inpda_sem.controller.Film;
import cz.mtrakal.inpda_sem.controller.Kvalita;

/**
 * @author Matěj Trakal
 * 
 */
public class FilmModel {
	Connection conn;

	public FilmModel() throws SQLException {
		conn = new ConnectionModel().getConnection();
	}

	public List<Film> getFilmy() throws SQLException {
		String query = "select distinct film_id, nazev_cz, nazev_en, link_csfd, rok_vydani, delka, popis from film order by nazev_cz";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Film> film = new ArrayList<Film>();

		while (rset.next()) {
			film.add(new Film(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4), rset.getInt(5), rset.getInt(6), rset
					.getString(7)));
		}
		return film;
	}

	public Film getFilmById(Integer filmId) throws SQLException {
		String query = "select film_id, nazev_cz, nazev_en, link_csfd, rok_vydani, delka, popis from film where film_id='" + filmId + "'";
		if (conn == null) {
			throw new SQLException("Chyba spojení");
		}
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Film film = null;

		while (rset.next()) {
			film = new Film(rset.getInt(1), rset.getString(2), rset.getString(3), rset.getString(4), rset.getInt(5), rset.getInt(6),
					rset.getString(7));
		}
		return film;
	}

	public void update(Film film) throws SQLException {
		String query = "update film set nazev_cz='" + film.getNazevCz() + "', nazev_en='" + film.getNazevEn() + "', link_csfd='"
				+ film.getLinkCsfd() + "', rok_vydani=" + film.getRokVydani() + ", delka=" + film.getDelka() + ", popis='"
				+ film.getPopis() + "' where film_id=" + film.getFilmId();
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void insert(Film film) throws SQLException {
		String query = "insert into film (film_id, nazev_cz, nazev_en, link_csfd, rok_vydani, delka, popis) values (" + film.getFilmId()
				+ ", '" + film.getNazevCz() + "', '" + film.getNazevEn() + "', '" + film.getLinkCsfd() + "', " + film.getRokVydani()
				+ ", " + film.getDelka() + ", '" + film.getPopis() + "')";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void delete(Film film) throws SQLException {
		delete(film.getFilmId());
	}

	public void delete(Integer filmId) throws SQLException {
		String query = "delete from film where film_id=" + filmId;
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}
}
