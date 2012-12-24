package cz.mtrakal.inpda_sem.view;

import java.sql.SQLException;
import java.util.*;

import com.bibounde.vprotovis.PieChartComponent;
import com.bibounde.vprotovis.chart.pie.PieLabelFormatter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import cz.mtrakal.inpda_sem.ApplicationHolder;
import cz.mtrakal.inpda_sem.FilmotekaApplication;
import cz.mtrakal.inpda_sem.controller.Film;
import cz.mtrakal.inpda_sem.controller.Filmoteka;
import cz.mtrakal.inpda_sem.controller.Kvalita;
import cz.mtrakal.inpda_sem.model.FilmModel;
import cz.mtrakal.inpda_sem.model.FilmotekaModel;
import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.view.modal.FilmotekaModal;

/**
 * @author Matěj Trakal
 * 
 */
public class FilmotekaView extends VerticalLayout implements Property.ValueChangeListener, ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8936385943039558677L;
	FilmotekaApplication app = null;
	List<Filmoteka> listPrvku;
	Map<Integer, Film> mapaFilmu;
	Map<Integer, Kvalita> mapaKvality;
	Table table = new Table();
	Item selectedItem;
	HorizontalLayout lo;

	FilmotekaModal modalNew = new FilmotekaModal("Přidání filmu", "../runo/icons/16/document.png", null,
			"Otevře okno pro přidání nového filmu");
	FilmotekaModal modalEdit = new FilmotekaModal("null", null);
	Button smazButton = smazButton();

	public FilmotekaView() {
		app = ApplicationHolder.getApplication();

		setMargin(true, true, true, true);
		addComponent(new Label("<h2>Seznam filmů</h2>", Label.CONTENT_XHTML));
		addComponent(createToolbar());

		try {
			listPrvku = new FilmotekaModel().getFilmoteka();

			List<Film> listFilmu = new FilmModel().getFilmy();
			mapaFilmu = new HashMap<Integer, Film>();
			for (Film item : listFilmu) {
				mapaFilmu.put(item.getFilmId(), item);
			}

			List<Kvalita> listKvalita = new KvalitaModel().getKvalita();
			mapaKvality = new HashMap<Integer, Kvalita>();
			for (Kvalita item : listKvalita) {
				mapaKvality.put(item.getKvalitaId(), item);
			}

			generujTabulku();

		} catch (SQLException e) {
			app.getMainWindow().showNotification("Nepovedlo se načíst záznamy z databáze", "<br/>" + e.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		if (property == table) {
			lo.removeComponent(modalEdit);
			lo.removeComponent(smazButton);
			selectedItem = table.getItem(table.getValue());
			modalEdit = new FilmotekaModal("Upravit film", "../runo/icons/16/document-web.png", selectedItem,
					"Otevře okno pro úpravu filmu");
			modalEdit.setImmediate(true);
			lo.addComponent(modalEdit);
			lo.addComponent(smazButton = smazButton());
		}
	}

	private HorizontalLayout createToolbar() {
		lo = new HorizontalLayout();
		lo.addComponent(modalNew);
		lo.setMargin(true);
		lo.setSpacing(true);
		lo.setStyleName("toolbar");
		lo.setWidth("100%");
		return lo;
	}

	private void generujTabulku() {
		table.setSizeFull();
		table.addContainerProperty("Filmotéka ID", Integer.class, null);
		table.addContainerProperty("Film ID", Integer.class, null);
		table.addContainerProperty("Název filmu", String.class, null);
		table.addContainerProperty("Umístění filmu", String.class, null);
		table.addContainerProperty("Kvalita ID", Integer.class, null);
		table.addContainerProperty("Kvalita", String.class, null);

		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.addListener((Property.ValueChangeListener) this);

		for (Filmoteka item : listPrvku) {
			table.addItem(
					new Object[] { item.getFilmotekaId(), item.getFilmId(), mapaFilmu.get(item.getFilmId()).getNazevCz(),
							item.getUmisteni(), item.getKvalitaId(), mapaKvality.get(item.getKvalitaId()).getKvalita() },
					item.getFilmotekaId());
		}
		table.setPageLength(listPrvku.size());
		addComponent(table);
		addComponent(kresliGraf());
	}

	private Button smazButton() {
		Button b = new Button("Smaž");
		b.setDescription("Smaže vybraný prvek z databáze.");
		b.setIcon(new ThemeResource("../runo/icons/16/document-delete.png"));
		b.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					Filmoteka.deleteFromDB((Integer) selectedItem.getItemProperty("Filmotéka ID").getValue());
					getWindow().showNotification("Smazání proběhlo úspěšně.", Notification.TYPE_HUMANIZED_MESSAGE);
					app.windowClose(null);
				} catch (SQLException e) {
					getWindow().showNotification("Nepovedlo se smazat záznam z databáze", "<br/>" + e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		return b;
	}

	private PieChartComponent kresliGraf() {
		PieChartComponent graf = new PieChartComponent();
		double total = 0;

		Map<String, Float> grafMapa;
		try {
			grafMapa = new FilmotekaModel().getProcentaFilmoteka();
			for (Map.Entry<String, Float> item : grafMapa.entrySet()) {
				graf.addSerie(item.getKey(), item.getValue(), false);
				total += item.getValue();
			}

		} catch (SQLException e) {
			app.getMainWindow().showNotification("Nepovedlo se vytvořit graf.", "<br/>" + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		 final double totalFinal = total;
		graf.setChartWidth(450d);
		graf.setChartHeight(300d);

		graf.setMarginLeft(40d);

		graf.setMarginTop(40d);

		graf.setMarginRight(40d);

		graf.setMarginBottom(40d);

		graf.setLegendVisible(true);
		graf.setLegendAreaWidth(150d);

		graf.setTooltipEnabled(true);
		graf.setCaption("Počet filmů v dané kvalitě");		

		graf.setLabelVisible(true);
		PieLabelFormatter labelFormatter = new PieLabelFormatter() {
			private static final long serialVersionUID = 1L;

			public boolean isVisible(double labelValue) {
				return 0.05d <= labelValue / totalFinal;
			}

			public String format(double labelValue) {
				int percent = Double.valueOf(labelValue / totalFinal * 100).intValue();
				return percent + "%";
			}
		};
		graf.setLabelColor("#FFFFFF");
		return graf;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
}
