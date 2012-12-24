package cz.mtrakal.inpda_sem.controller;

import java.sql.SQLException;

import cz.mtrakal.inpda_sem.model.FilmotekaModel;

/**
 * @author Matěj Trakal
 * 
 */
public class Filmoteka {
	Integer filmotekaId;
	Integer filmId;
	String umisteni;
	Integer kvalitaId;
	Boolean updated = false;

	public Filmoteka() {
		// TODO Auto-generated constructor stub
	}

	public Filmoteka(Integer filmotekaId, Integer filmId, String umisteni, Integer kvalitaId) {
		super();
		this.filmotekaId = filmotekaId;
		this.filmId = filmId;
		this.umisteni = umisteni;
		this.kvalitaId = kvalitaId;
	}

	public Integer getFilmotekaId() {
		return filmotekaId;
	}

	public void setFilmotekaId(Integer filmotekaId) {
		if (this.filmotekaId != filmotekaId && this.filmotekaId != null) {
			updated = true;
		}
		this.filmotekaId = filmotekaId;
	}

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(Integer filmId) {
		this.filmId = filmId;
	}

	public String getUmisteni() {
		return umisteni;
	}

	public void setUmisteni(String umisteni) {
		this.umisteni = umisteni;
	}

	public Integer getKvalitaId() {
		return kvalitaId;
	}

	public void setKvalitaId(Integer kvalitaId) {
		this.kvalitaId = kvalitaId;
	}

	public void storeToDB() throws SQLException {
		FilmotekaModel model = new FilmotekaModel();
		if (updated) {
			model.update(this);
		} else {
			model.insert(this);
		}
	}

	public void deleteFromDB() throws SQLException {
		FilmotekaModel model = new FilmotekaModel();
		model.delete(this);
	}

	public static void deleteFromDB(Integer filmId) throws SQLException {
		FilmotekaModel model = new FilmotekaModel();
		model.delete(filmId);
	}
}
