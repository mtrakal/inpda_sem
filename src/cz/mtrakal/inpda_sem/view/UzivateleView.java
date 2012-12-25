package cz.mtrakal.inpda_sem.view;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig.PieConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieDataLabel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import cz.mtrakal.inpda_sem.ApplicationHolder;
import cz.mtrakal.inpda_sem.FilmotekaApplication;
import cz.mtrakal.inpda_sem.controller.Kvalita;
import cz.mtrakal.inpda_sem.controller.Uzivatel;
import cz.mtrakal.inpda_sem.model.KvalitaModel;
import cz.mtrakal.inpda_sem.model.UzivateleModel;
import cz.mtrakal.inpda_sem.view.modal.KvalitaModal;
import cz.mtrakal.inpda_sem.view.modal.UzivatelModal;

/**
 * @author Matěj Trakal
 * 
 */
public class UzivateleView extends VerticalLayout implements Property.ValueChangeListener, ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8044408487463005587L;
	FilmotekaApplication app = null;
	List<Uzivatel> listPrvku;
	Table table = new Table();
	Item selectedItem;
	HorizontalLayout lo;

	UzivatelModal modalNew = new UzivatelModal("Přidání uživatele", "../runo/icons/16/document.png", null,
			"Otevře okno pro přidání nového uživatele");
	UzivatelModal modalEdit = new UzivatelModal("null", null);
	Button smazButton = smazButton();

	public UzivateleView() {
		app = ApplicationHolder.getApplication();

		setMargin(true, true, true, true);
		addComponent(new Label("<h2>Uživatelé</h2>", Label.CONTENT_XHTML));
		addComponent(createToolbar());

		try {
			listPrvku = new UzivateleModel().getUzivatele();
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
				modalEdit = new UzivatelModal("Upravit uživatele", "../runo/icons/16/document-web.png", selectedItem,
						"Otevře okno pro úpravu uživatele");
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
		table.addContainerProperty("Uživatel ID", Integer.class, null);
		table.addContainerProperty("Email", String.class, null);
		table.addContainerProperty("Heslo", String.class, null);
		table.addContainerProperty("Práva", Integer.class, null);

		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.addListener((Property.ValueChangeListener) this);

		for (Uzivatel item : listPrvku) {
			table.addItem(new Object[] { item.getUzivatelId(), item.getEmail(), item.getHeslo(), item.getPrava() }, item.getUzivatelId());
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
					Uzivatel.deleteFromDB((Integer) selectedItem.getItemProperty("Uživatel ID").getValue());
					getWindow().showNotification("Smazání proběhlo úspěšně.", Notification.TYPE_HUMANIZED_MESSAGE);
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
