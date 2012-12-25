package cz.mtrakal.inpda_sem.controller;

import java.sql.SQLException;
import java.util.Date;

import cz.mtrakal.inpda_sem.model.HodnoceniModel;
import cz.mtrakal.inpda_sem.model.KvalitaModel;

/**
 * @author Matěj Trakal
 * 
 */
public class Hodnoceni {
	Integer filmId;
	Date datumHodnoceni;
	Integer hvezdy;
	String popis;
	Boolean updated = false;

	public Hodnoceni(Integer filmId, Date datumHodnoceni, Integer hvezdy, String popis) {
		super();
		this.filmId = filmId;
		this.datumHodnoceni = datumHodnoceni;
		this.hvezdy = hvezdy;
		this.popis = popis;
	}

	public Hodnoceni() {
		// TODO Auto-generated constructor stub
	}

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(Integer filmId) {
		this.filmId = filmId;
	}

	public Date getDatumHodnoceni() {
		return datumHodnoceni;
	}

	public void setDatumHodnocei(Date datumHodnoceni) {
		if (this.datumHodnoceni != datumHodnoceni && this.datumHodnoceni != null) {
			updated = true;
		}
		this.datumHodnoceni = datumHodnoceni;
	}

	public Integer getHvezdy() {
		return hvezdy;
	}

	public void setHvezdy(Integer hvezdy) {
		if (this.hvezdy != hvezdy && this.hvezdy != null) {
			updated = true;
		}
		this.hvezdy = hvezdy;
	}

	public String getPopis() {
		return popis;
	}

	public void setPopis(String popis) {
		if (this.popis != popis && this.popis != null) {
			updated = true;
		}
		this.popis = popis;
	}

	public void storeToDB() throws SQLException {
		HodnoceniModel model = new HodnoceniModel();
		if (updated) {
			model.update(this);
		} else {
			model.insert(this);
		}
	}

	public void deleteFromDB() throws SQLException {
		HodnoceniModel model = new HodnoceniModel();
		model.delete(this);
	}

	public static void deleteFromDB(Integer filmId, Date datum) throws SQLException {
		HodnoceniModel model = new HodnoceniModel();
		model.delete(filmId, datum);
	}

}
