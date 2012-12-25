package cz.mtrakal.inpda_sem.view;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.bibounde.vprotovis.PieChartComponent;
import com.bibounde.vprotovis.chart.pie.PieLabelFormatter;
import com.bibounde.vprotovis.chart.pie.PieTooltipFormatter;

import com.invient.vaadin.charts.*;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig.PieConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieDataLabel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import cz.mtrakal.inpda_sem.ApplicationHolder;
import cz.mtrakal.inpda_sem.FilmotekaApplication;
import cz.mtrakal.inpda_sem.controller.Kvalita;
import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.view.modal.KvalitaModal;

/**
 * @author Matěj Trakal
 * 
 */
public class KvalitaView extends VerticalLayout implements Property.ValueChangeListener, ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053328570409746004L;
	FilmotekaApplication app = null;
	List<Kvalita> kvalita;
	Table table = new Table();
	Item selectedItem;
	HorizontalLayout lo;

	KvalitaModal modalNew = new KvalitaModal("Přidání kvality filmu", "../runo/icons/16/document.png", null,
			"Otevře okno pro přidání nového přepravce");
	KvalitaModal modalEdit = new KvalitaModal("null", null);
	Button smazButton = smazButton();

	public KvalitaView() {
		app = ApplicationHolder.getApplication();

		setMargin(true, true, true, true);
		addComponent(new Label("<h2>Kvalita filmů</h2>", Label.CONTENT_XHTML));
		addComponent(createToolbar());

		try {
			kvalita = new KvalitaModel().getKvalita();
			generujTabulku();
			addComponent(kresliGraf());

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
				modalEdit = new KvalitaModal("Upravit kvalitu filmu", "../runo/icons/16/document-web.png", selectedItem,
						"Otevře okno pro přidání nového přepravce");
				modalEdit.setImmediate(true);
				lo.addComponent(modalEdit);
				lo.addComponent(smazButton = smazButton());
			}
		}
	}

	private HorizontalLayout createToolbar() {
		lo = new HorizontalLayout();
		if (app.getUzivatel().getPrava() == 1) {
			lo.addComponent(modalNew);
		}
		// lo.addComponent(modalEdit);
		lo.setMargin(true);
		lo.setSpacing(true);
		lo.setStyleName("toolbar");
		lo.setWidth("100%");
		return lo;
	}

	private void generujTabulku() {
		table.setSizeFull();
		table.addContainerProperty("Kvalita ID", Integer.class, null);
		table.addContainerProperty("Kvalita", String.class, null);

		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.addListener((Property.ValueChangeListener) this);

		for (Kvalita item : kvalita) {
			table.addItem(new Object[] { item.getKvalitaId(), item.getKvalita() }, item.getKvalitaId());
		}
		table.setPageLength(kvalita.size());
		addComponent(table);
	}

	private Button smazButton() {
		Button b = new Button("Smaž");
		b.setDescription("Smaže vybraný prvek z databáze.");
		b.setIcon(new ThemeResource("../runo/icons/16/document-delete.png"));
		b.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					Kvalita.deleteFromDB((Integer) selectedItem.getItemProperty("Kvalita ID").getValue());
					getWindow().showNotification("Smazání proběhlo úspěšně.", Notification.TYPE_HUMANIZED_MESSAGE);
					app.windowClose(null);
					// TODO refreshnout tabulku
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
			grafMapa = new KvalitaModel().getProcentaKvalita();
			for (Map.Entry<String, Float> item : grafMapa.entrySet()) {
				graf.addSerie(item.getKey(), item.getValue(), false);
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

		graf.setLegendVisible(true);
		graf.setLegendAreaWidth(150d);

		graf.setTooltipEnabled(true);
		graf.setCaption("Filmy v danné kvalitě v procentech");

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
