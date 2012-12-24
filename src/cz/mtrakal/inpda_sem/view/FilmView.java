package cz.mtrakal.inpda_sem.view;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cz.mtrakal.inpda_sem.controller.Hodnoceni;
import cz.mtrakal.inpda_sem.controller.Uzivatel;
import cz.mtrakal.inpda_sem.model.FilmModel;
import cz.mtrakal.inpda_sem.model.HodnoceniModel;
import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.view.modal.FilmModal;
import cz.mtrakal.inpda_sem.view.modal.HodnoceniModal;

/**
 * @author Matěj Trakal
 * 
 */
public class FilmView extends VerticalLayout implements Property.ValueChangeListener, ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7453558809403772645L;

	FilmotekaApplication app = null;
	List<Film> listPrvku;
	Table table = new Table();
	Item selectedItem;
	HorizontalLayout lo;

	FilmModal modalNew = new FilmModal("Přidání filmu", "../runo/icons/16/document.png", null, "Otevře okno pro přidání nového filmu");
	FilmModal modalEdit = new FilmModal("null", null);
	Button smazButton = smazButton();

	public FilmView() {
		app = ApplicationHolder.getApplication();

		setMargin(true, true, true, true);
		addComponent(new Label("<h2>Seznam filmů</h2>", Label.CONTENT_XHTML));
		addComponent(createToolbar());

		try {
			listPrvku = new FilmModel().getFilmy();
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
			modalEdit = new FilmModal("Upravit film", "../runo/icons/16/document-web.png", selectedItem, "Otevře okno pro úpravu filmu");
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
		table.addContainerProperty("Film ID", Integer.class, null);
		table.addContainerProperty("Název filmu CZ", String.class, null);
		table.addContainerProperty("Název filmu EN", String.class, null);
		table.addContainerProperty("Odkaz na CSFD", Link.class, null);
		table.addContainerProperty("Rok vydání", Integer.class, null);
		table.addContainerProperty("Délka", Integer.class, null);
		table.addContainerProperty("Popis", String.class, null);

		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.addListener((Property.ValueChangeListener) this);

		for (Film item : listPrvku) {
			table.addItem(
					new Object[] { item.getFilmId(), item.getNazevCz(), item.getNazevEn(),
							new Link(item.getLinkCsfd(), new ExternalResource(item.getLinkCsfd())), item.getRokVydani(), item.getDelka(),
							item.getPopis() }, item.getFilmId());
		}
		table.setPageLength(listPrvku.size());
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
					Film.deleteFromDB((Integer) selectedItem.getItemProperty("Film ID").getValue());
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
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
}
