package cz.mtrakal.inpda_sem.controller;

import java.sql.SQLException;

import cz.mtrakal.inpda_sem.model.FilmModel;

/**
 * @author Matěj Trakal
 * 
 */
public class Film {
	Integer filmId;
	String nazevCz;
	String nazevEn;
	String linkCsfd;
	Integer rokVydani;
	Integer delka;
	String popis;
	Boolean updated = false;
	
	public Film() {
		// TODO Auto-generated constructor stub
	}

	public Film(Integer filmId, String nazevCz, String nazevEn, String linkCsfd, Integer rokVydani, Integer delka, String popis) {
		super();
		this.filmId = filmId;
		this.nazevCz = nazevCz;
		this.nazevEn = nazevEn;
		this.linkCsfd = linkCsfd;
		this.rokVydani = rokVydani;
		this.delka = delka;
		this.popis = popis;
	}

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(Integer filmId) {
		if (this.filmId != filmId && this.filmId != null) {
			updated = true;
		}
		this.filmId = filmId;
	}

	public String getNazevCz() {
		return nazevCz;
	}

	public void setNazevCz(String nazevCz) {
		this.nazevCz = nazevCz;
	}

	public String getNazevEn() {
		return nazevEn;
	}

	public void setNazevEn(String nazevEn) {
		this.nazevEn = nazevEn;
	}

	public String getLinkCsfd() {
		return linkCsfd;
	}

	public void setLinkCsfd(String linkCsfd) {
		this.linkCsfd = linkCsfd;
	}

	public Integer getRokVydani() {
		return rokVydani;
	}

	public void setRokVydani(Integer rokVydani) {
		this.rokVydani = rokVydani;
	}

	public Integer getDelka() {
		return delka;
	}

	public void setDelka(Integer delka) {
		this.delka = delka;
	}

	public String getPopis() {
		return popis;
	}

	public void setPopis(String popis) {
		this.popis = popis;
	}

	public void storeToDB() throws SQLException {
		FilmModel model = new FilmModel();
		if (updated) {
			model.update(this);
		} else {
			model.insert(this);
		}
	}
	public void deleteFromDB() throws SQLException {
		FilmModel model = new FilmModel();
		model.delete(this);
	}

	public static void deleteFromDB(Integer filmId) throws SQLException {
		FilmModel model = new FilmModel();
		model.delete(filmId);
	}
}
