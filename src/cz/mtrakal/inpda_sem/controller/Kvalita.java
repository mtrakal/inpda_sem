package cz.mtrakal.inpda_sem.controller;

import java.sql.SQLException;
import com.invient.vaadin.charts.InvientChartsConfig;
import cz.mtrakal.inpda_sem.model.KvalitaModel;

/**
 * @author Matěj Trakal
 * 
 */
public class Kvalita {
	Integer kvalitaId;
	String kvalita;
	Boolean updated = false;

	public Kvalita(Integer kvalitaId, String kvalita) {
		super();
		this.kvalitaId = kvalitaId;
		this.kvalita = kvalita;
	}

	public Kvalita() {
		// TODO Auto-generated constructor stub
	}

	public Integer getKvalitaId() {
		return kvalitaId;
	}

	public void setKvalitaId(Integer kvalitaId) {
		if (this.kvalitaId != kvalitaId && this.kvalitaId != null) {
			updated = true;
		}
		this.kvalitaId = kvalitaId;
	}

	public String getKvalita() {
		return kvalita;
	}

	public void setKvalita(String kvalita) {
		this.kvalita = kvalita;
	}

	public void storeToDB() throws SQLException {
		KvalitaModel model = new KvalitaModel();
		if (updated) {
			model.update(this);
		} else {
			model.insert(this);
		}
	}

	public void deleteFromDB() throws SQLException {
		KvalitaModel model = new KvalitaModel();
		model.delete(this);
	}

	public static void deleteFromDB(Integer kvalitaId) throws SQLException {
		KvalitaModel model = new KvalitaModel();
		model.delete(kvalitaId);
	}
}
