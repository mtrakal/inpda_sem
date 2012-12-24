package cz.mtrakal.inpda_sem.controller;

import java.sql.SQLException;

import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.model.UzivateleModel;

/**
 * @author Matěj Trakal
 * 
 */
public class Uzivatel {
	Integer uzivatelId;
	String email;
	String heslo;
	Boolean updated = false;
	
	public Uzivatel(Integer idUzivatele, String email, String heslo) {
		super();
		this.uzivatelId = idUzivatele;
		this.email = email;
		this.heslo = heslo;
	}

	public Uzivatel() {
		// TODO Auto-generated constructor stub
	}

	public Integer getUzivatelId() {
		return uzivatelId;
	}

	public void setUzivatelId(Integer uzivatelId) {
		if (this.uzivatelId != uzivatelId && this.uzivatelId != null) {
			updated = true;
		}
		this.uzivatelId = uzivatelId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHeslo() {
		return heslo;
	}

	public void setHeslo(String heslo) {
		this.heslo = heslo;
	}
	public void storeToDB() throws SQLException {
		UzivateleModel model = new UzivateleModel();
		if (updated) {
			model.update(this);
		} else {
			model.insert(this);
		}
	}

	public void deleteFromDB() throws SQLException {
		UzivateleModel model = new UzivateleModel();
		model.delete(this);
	}

	public static void deleteFromDB(Integer kvalitaId) throws SQLException {
		UzivateleModel model = new UzivateleModel();
		model.delete(kvalitaId);
	}
}
