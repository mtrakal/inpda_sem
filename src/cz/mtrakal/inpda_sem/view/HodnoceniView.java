package cz.mtrakal.inpda_sem.view;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bibounde.vprotovis.PieChartComponent;
import com.bibounde.vprotovis.chart.pie.PieLabelFormatter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import cz.mtrakal.inpda_sem.ApplicationHolder;
import cz.mtrakal.inpda_sem.FilmotekaApplication;
import cz.mtrakal.inpda_sem.controller.Film;
import cz.mtrakal.inpda_sem.controller.Hodnoceni;
import cz.mtrakal.inpda_sem.controller.Uzivatel;
import cz.mtrakal.inpda_sem.model.FilmModel;
import cz.mtrakal.inpda_sem.model.HodnoceniModel;
import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.model.UzivateleModel;
import cz.mtrakal.inpda_sem.view.modal.HodnoceniModal;
import cz.mtrakal.inpda_sem.view.modal.UzivatelModal;

/**
 * @author Matěj Trakal
 * 
 */
public class HodnoceniView extends VerticalLayout implements Property.ValueChangeListener, ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 393460268038117157L;
	FilmotekaApplication app = null;
	List<Hodnoceni> listPrvku;
	Map<Integer, Film> mapaFilmu;
	Table table = new Table();
	Item selectedItem;
	HorizontalLayout lo;

	HodnoceniModal modalNew;
	HodnoceniModal modalEdit;
	Button smazButton = smazButton();

	public HodnoceniView() {
		try {
			modalNew = new HodnoceniModal("Přidání hodnocení", "../runo/icons/16/document.png", null,
					"Otevře okno pro přidání nového hodnocení");
			modalEdit = new HodnoceniModal("null", null);
		} catch (ParseException e) {
			app.getMainWindow().showNotification("Nepovedlo se naformátovat datum v konsruktoru", "<br/>" + e.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		app = ApplicationHolder.getApplication();

		setMargin(true, true, true, true);
		addComponent(new Label("<h2>Hodnocení filmů</h2>", Label.CONTENT_XHTML));
		addComponent(createToolbar());

		try {
			listPrvku = new HodnoceniModel().getHodnoceni();
			List<Film> listFilmu = new FilmModel().getFilmy();
			mapaFilmu = new HashMap<Integer, Film>();
			for (Film item : listFilmu) {
				mapaFilmu.put(item.getFilmId(), item);
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
			if (app.getUzivatel().getPrava() == 1) {
				lo.removeComponent(modalEdit);
				lo.removeComponent(smazButton);
				selectedItem = table.getItem(table.getValue());
				try {
					modalEdit = new HodnoceniModal("Upravit hodnocení", "../runo/icons/16/document-web.png", selectedItem,
							"Otevře okno pro úpravu uživatele");
				} catch (ParseException e) {
					app.getMainWindow().showNotification("Nepovedlo se naformátovat datum", "<br/>" + e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					e.printStackTrace();
				}
				modalEdit.setImmediate(true);
				lo.addComponent(modalEdit);
				lo.addComponent(smazButton = smazButton());
			}
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
		table.addContainerProperty("Film ID", Integer.class, null);
		table.addContainerProperty("Název filmu", String.class, null);
		table.addContainerProperty("Datum hodnocení", String.class, null);
		table.addContainerProperty("Hvězdy", Integer.class, null);
		table.addContainerProperty("Popis", String.class, null);

		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.addListener((Property.ValueChangeListener) this);

		Integer i = 0;
		for (Hodnoceni item : listPrvku) {
			table.addItem(
					new Object[] { item.getFilmId(), mapaFilmu.get(item.getFilmId()).getNazevCz(), item.getDatumHodnoceni(),
							item.getHvezdy(), item.getPopis() }, i); // item.getFilmId()
																		// + "!"
																		// +
																		// item.getDatumHodnoceni()
			i++;
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
					try {
						Hodnoceni.deleteFromDB((Integer) selectedItem.getItemProperty("Film ID").getValue(), new SimpleDateFormat(
								"yyyy-MM-dd", Locale.ENGLISH).parse((String) selectedItem.getItemProperty("Datum hodnocení").getValue()));
					} catch (ParseException e) {
						app.getMainWindow().showNotification("Nepovedlo se naformátovat datum v Smaž button", "<br/>" + e.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
						e.printStackTrace();
					}
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
		// double total = 0;

		Map<String, Float> grafMapa;
		try {
			grafMapa = new HodnoceniModel().getProcentaHodnoceni();
			for (Map.Entry<String, Float> item : grafMapa.entrySet()) {
				String hvezdy = null;
				switch (item.getKey()) {
				case "1":
					hvezdy = "*";
					break;
				case "2":
					hvezdy = "**";
					break;
				case "3":
					hvezdy = "***";
					break;
				case "4":
					hvezdy = "****";
					break;
				case "5":
					hvezdy = "*****";
					break;
				}

				graf.addSerie(hvezdy, item.getValue(), false);
				// total += item.getValue();
			}

		} catch (SQLException e) {
			app.getMainWindow().showNotification("Nepovedlo se vytvořit graf.", "<br/>" + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		// final double totalFinal = total;
		final double totalFinal = 100;
		graf.setChartWidth(450d);
		graf.setChartHeight(300d);

		graf.setMarginLeft(40d);

		graf.setMarginTop(40d);

		graf.setMarginRight(40d);

		graf.setMarginBottom(40d);
		graf.setCaption("Hodnocení filmů v procentech");

		graf.setLegendVisible(true);
		graf.setLegendAreaWidth(150d);

		graf.setTooltipEnabled(true);

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
