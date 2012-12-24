package cz.mtrakal.inpda_sem.view.modal;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import cz.mtrakal.inpda_sem.ApplicationHolder;
import cz.mtrakal.inpda_sem.FilmotekaApplication;
import cz.mtrakal.inpda_sem.controller.Hodnoceni;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Matěj Trakal
 * 
 */
public class HodnoceniModal extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3911973045746398306L;
	FilmotekaApplication app = ApplicationHolder.getApplication();
	Window subwindow;
	Boolean novyPrvek = true;
	Hodnoceni prvek;
	DateField df;

	public HodnoceniModal(String popisek, String ikona) throws ParseException {
		this(popisek, ikona, null);
	}

	public HodnoceniModal(String popisek, String ikona, Item prvek) throws ParseException {
		this(popisek, ikona, prvek, null);
	}

	public HodnoceniModal(String popisekTlacitka, String ikona, Item prvek, String tooltip) throws ParseException {
		if (prvek == null) {
			// nový prvek do DB
			this.prvek = new Hodnoceni();
		} else {
			// Editace prvku
			novyPrvek = false;
			this.prvek = new Hodnoceni((Integer) prvek.getItemProperty("Film ID").getValue(), new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH).parse((String) prvek.getItemProperty("Datum hodnocení").getValue()), (Integer) prvek.getItemProperty(
					"Hvězdy").getValue(), (String) prvek.getItemProperty("Popis").getValue());
		}

		BeanItem<Hodnoceni> pojoItem = new BeanItem<Hodnoceni>(this.prvek);
		subwindow = new Window((novyPrvek ? "Přidání" : "Editace") + " kvality filmu");
		subwindow.setModal(true); // nastaví okno na modální

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		// final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined(); // automatické přizpůsobení velikosti prvkům

		final Form form = new Form();
		form.setWriteThrough(false); // we want explicit 'apply'
		form.setInvalidCommitted(false); // no invalid values in datamodel
		// FieldFactory for customizing the fields and adding validators
		form.setFormFieldFactory(new ValidateFieldFactory());
		form.setItemDataSource(pojoItem); // bind to POJO via BeanItem

		// Determines which properties are shown, and in which order:
		form.setVisibleItemProperties(Arrays.asList(new String[] { "filmId", "hvezdy", "popis" }));

		df = new DateField();
		// df.setValue(f.getValue());
		df.setDateFormat("yyyy-MM-dd");
		df.setWidth("12em");
		df.setCaption("Datum");
		df.setRequired(true);
		df.setValue(this.prvek.getDatumHodnoceni());
		if (!novyPrvek) {
			df.setEnabled(false);
		}
		form.addField(df, df);

		Button cancel = new Button("Zruš", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				form.discard();
				(subwindow.getParent()).removeWindow(subwindow);
			}
		});
		cancel.setIcon(new ThemeResource("../runo/icons/16/cancel.png"));

		Button save = new Button("Ulož", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				form.commit();
				try {
					ulozPrvek();
					getWindow().showNotification("Ukládání proběhlo úspěšně.", Notification.TYPE_HUMANIZED_MESSAGE);
					(subwindow.getParent()).removeWindow(subwindow);
					app.windowClose(null);
				} catch (SQLException e) {
					// showPojoState();
					getWindow().showNotification("Nepovedlo se uložit záznam do databáze", "<br/>" + e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		save.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("300px");
		hl.addComponent(cancel);
		hl.setComponentAlignment(cancel, Alignment.BOTTOM_LEFT);
		hl.addComponent(save);
		hl.setComponentAlignment(save, Alignment.BOTTOM_RIGHT);

		layout.addComponent(form);
		layout.addComponent(hl);

		Button open = new Button(popisekTlacitka, new Button.ClickListener() {
			// inline click-listener
			@Override
			public void buttonClick(ClickEvent event) {
				if (subwindow.getParent() != null) {
					// window is already showing
					getWindow().showNotification("Již otevřené");
				} else {
					// Open the subwindow by adding it to the parent
					// window
					// addComponent(layout);
					getWindow().addWindow(subwindow);
				}
				subwindow.center(); // vycentruje okno na střed monitoru
			}
		});
		if (ikona != null) {
			open.setIcon(new ThemeResource(ikona));
		}
		if (tooltip != null) {
			open.setDescription(tooltip);
		}
		addComponent(open);
	}

	private void ulozPrvek() throws SQLException {
		this.prvek.setDatumHodnocei((Date) df.getValue());
		prvek.storeToDB();
	}

	// private void showPojoState() {
	// Window.Notification n = new Window.Notification("POJO state",
	// Window.Notification.TYPE_TRAY_NOTIFICATION);
	// n.setPosition(Window.Notification.POSITION_CENTERED);
	// n.setDescription("ID: " + prvek.getKvalitaId() + "<br/>Kvalita: " +
	// prvek.getKvalita());
	// getWindow().showNotification(n);
	// }

	// validace textfieldů
	private class ValidateFieldFactory extends DefaultFieldFactory {

		public ValidateFieldFactory() {
		}

		@Override
		public Field createField(Item item, Object propertyId, Component uiContext) {
			Field f = super.createField(item, propertyId, uiContext);

			if ("filmId".equals(propertyId)) {
				TextField tf = (TextField) f;
				if (!novyPrvek) {
					tf.setEnabled(false);
				}
				// TextField tf = new TextField();
				tf.setNullRepresentation("");
				tf.setRequired(true);
				// tf.setNullSettingAllowed(true);
				tf.setRequiredError("Vyplň ID");
				tf.setWidth("12em");
				return tf;
			} else if ("hvezdy".equals(propertyId)) {
				// TextField tf = new TextField();
				TextField tf = (TextField) f;
				tf.setNullRepresentation("");
				tf.setRequired(true);
				tf.setRequiredError("Vyplň počet hvězdiček (číselně)");
				// tf.setNullSettingAllowed(true);
				tf.setWidth("12em");
				return tf;
			} else if ("popis".equals(propertyId)) {
				TextField tf = (TextField) f;
				// TextField tf = new TextField();
				tf.setNullRepresentation("");
				tf.setRequired(true);
				tf.setRequiredError("Vyplň popis filmu");
				// tf.setNullSettingAllowed(true);
				tf.setWidth("12em");
				return tf;
			}
			return null;

			// return f;
		}
	}
}
