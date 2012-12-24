package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.mtrakal.inpda_sem.controller.Kvalita;
import cz.mtrakal.inpda_sem.controller.Uzivatel;

/**
 * @author Matěj Trakal
 * 
 */
public class KvalitaModel {
	Connection conn;

	public KvalitaModel() throws SQLException {
		conn = new ConnectionModel().getConnection();
	}

	public List<Kvalita> getKvalita() throws SQLException {
		String query = "select kvalita_id, kvalita from kvalita";

		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		List<Kvalita> kvalita = new ArrayList<Kvalita>();

		while (rset.next()) {
			kvalita.add(new Kvalita(rset.getInt(1), rset.getString(2)));
		}
		return kvalita;
	}

	public Kvalita getKvalitaById(Integer kvalitaId) throws SQLException {
		String query = "select kvalita_id, kvalita from kvalita where kvalita_id='" + kvalitaId + "'";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Kvalita kvalita = null;

		while (rset.next()) {
			kvalita = new Kvalita(rset.getInt(1), rset.getString(2));
		}
		return kvalita;
	}

	public Map<String, Float> getProcentaKvalita() throws SQLException {
		String query = "select kvalita.kvalita, trunc( ratio_to_report(count(filmoteka.kvalita_id)) over() * 100, 2) as procenta from filmoteka left join kvalita on kvalita.kvalita_id = filmoteka.kvalita_id group by filmoteka.kvalita_id, kvalita.kvalita";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		Map<String, Float> procenta = new HashMap<String, Float>();

		while (rset.next()) {
			procenta.put(rset.getString(1), rset.getFloat(2));
		}
		return procenta;
	}

	@Override
	protected void finalize() throws Throwable {
		// conn.close();
		conn = null;
		super.finalize();
	}

	public void update(Kvalita kvalita) throws SQLException {
		String query = "update kvalita set kvalita='" + kvalita.getKvalita() + "' where kvalita_id='" + kvalita.getKvalitaId() + "'";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void insert(Kvalita kvalita) throws SQLException {
		String query = "insert into kvalita (kvalita_id, kvalita) values (" + kvalita.getKvalitaId() + ", '" + kvalita.getKvalita() + "')";
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}

	public void delete(Kvalita kvalita) throws SQLException {
		delete(kvalita.getKvalitaId());
	}

	public void delete(Integer kvalitaId) throws SQLException {
		String query = "delete from kvalita where kvalita_id=" + kvalitaId;
		Statement stmt = conn.createStatement();
		stmt.executeQuery(query);
	}
}
