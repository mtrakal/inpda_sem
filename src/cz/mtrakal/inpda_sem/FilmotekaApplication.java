package cz.mtrakal.inpda_sem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;

import cz.mtrakal.inpda_sem.controller.Uzivatel;
import cz.mtrakal.inpda_sem.model.UzivateleModel;
import cz.mtrakal.inpda_sem.view.FilmView;
import cz.mtrakal.inpda_sem.view.FilmotekaView;
import cz.mtrakal.inpda_sem.view.HodnoceniView;
import cz.mtrakal.inpda_sem.view.KvalitaView;
import cz.mtrakal.inpda_sem.view.UzivateleView;

/**
 * @author Matěj Trakal
 * 
 */
public class FilmotekaApplication extends Application implements ClickListener, ApplicationContext.TransactionListener,
		Window.CloseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HorizontalSplitPanel horizontalSplit;
	Window mainWindow;
	VerticalLayout layout;
	String otevreneOkno;

	@Override
	public void init() {
		ApplicationHolder.setApplication(this);
		if (getContext() != null) {
			getContext().addTransactionListener(this);
		}
		// FIXME: do finální verze to přepsat...
//		buildMainLayout();
		setMainWindow(new LoginWindow());
	}

	public void authenticate(String email, String heslo) throws Exception {
		UzivateleModel um = new UzivateleModel();

		List<Uzivatel> uzivatele = um.getUzivatele(email, heslo);

		if (uzivatele.size() != 1) {
			throw new Exception("Neúspěšné přihlášení z důvodu neověření proti databázi.");
		}

		if (uzivatele.get(0).getEmail().equals(email) && uzivatele.get(0).getHeslo().equals(heslo)) {
			buildMainLayout();
			return;
		}
	}

	private void buildMainLayout() {
		mainWindow = new Window("Filmotéka");
		mainWindow.setSizeFull();
		setMainWindow(mainWindow);
		setTheme("inpda_semtheme");
		layout = new VerticalLayout();
		layout.setSizeFull();
		// layout.setMargin(true, true, true, true);
		// layout.addComponent(createToolbar());

		layout.addComponent(createToolbar());

		horizontalSplit = new HorizontalSplitPanel();
		horizontalSplit.setSizeFull();
		horizontalSplit.setSplitPosition(240, Sizeable.UNITS_PIXELS);
		// horizontalSplit.setLocked(true);
		layout.addComponent(horizontalSplit);
		layout.setExpandRatio(horizontalSplit, 1);

		horizontalSplit.setFirstComponent(createMainMenu());

		getMainWindow().setContent(layout);
	}

	private Layout createToolbar() {
		HorizontalLayout infoBar = new HorizontalLayout();
		infoBar.setHeight("50px");
		infoBar.setWidth("100%");
		Label lblAppTitle = new Label("Filmotéka");
		lblAppTitle.setSizeFull();
		lblAppTitle.setStyleName("v-label-app-title");
		infoBar.addComponent(lblAppTitle);
		return infoBar;
	}

	private VerticalLayout createMainMenu() {

		VerticalLayout retVal = new VerticalLayout();
		retVal.setMargin(true, true, true, true);

		Button filmotekaButton = new Button("Filmotéka");
		filmotekaButton.setWidth(200, Button.UNITS_PIXELS);
		filmotekaButton.setImmediate(true);
		filmotekaButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				horizontalSplit.setSecondComponent(new FilmotekaView());
				otevreneOkno = "filmoteka";
			}
		});
		retVal.addComponent(filmotekaButton);

		Button filmButton = new Button("Seznam filmů");
		filmButton.setWidth(200, Button.UNITS_PIXELS);
		filmButton.setImmediate(true);
		filmButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				horizontalSplit.setSecondComponent(new FilmView());
				otevreneOkno = "film";
			}
		});
		retVal.addComponent(filmButton);

		Button hodnoceniButton = new Button("Hodnocení");
		hodnoceniButton.setWidth(200, Button.UNITS_PIXELS);
		hodnoceniButton.setImmediate(true);
		hodnoceniButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				horizontalSplit.setSecondComponent(new HodnoceniView());
				otevreneOkno = "hodnoceni";
			}
		});
		retVal.addComponent(hodnoceniButton);

		Button uzivateleButton = new Button("Uživatelé");
		uzivateleButton.setWidth(200, Button.UNITS_PIXELS);
		uzivateleButton.setImmediate(true);
		uzivateleButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				horizontalSplit.setSecondComponent(new UzivateleView());
				otevreneOkno = "uzivatele";
			}
		});
		retVal.addComponent(uzivateleButton);

		Button kvalitaButton = new Button("Kvalita filmů");
		kvalitaButton.setWidth(200, Button.UNITS_PIXELS);
		kvalitaButton.setImmediate(true);
		kvalitaButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				horizontalSplit.setSecondComponent(new KvalitaView());
				otevreneOkno = "kvalita";
			}
		});
		retVal.addComponent(kvalitaButton);

		Button odhlasitButton = new Button("Odhlásit");
		odhlasitButton.setWidth(200, Button.UNITS_PIXELS);
		odhlasitButton.setImmediate(true);
		odhlasitButton.addListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				// horizontalSplit.removeAllComponents();
				// removeWindow(mainWindow);
				// setMainWindow(new LoginWindow());
				// ApplicationHolder.resetApplication();
				ApplicationHolder.getApplication().close();

			}
		});
		retVal.addComponent(odhlasitButton);

		return retVal;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	public static FilmotekaApplication getInstance() {
		return ApplicationHolder.getApplication();
	}

	public void transactionStart(Application application, Object o) {
		if (application == FilmotekaApplication.this) {
			ApplicationHolder.setApplication(this);
		}
	}

	public void transactionEnd(Application application, Object o) {
		if (application == FilmotekaApplication.this) {
			ApplicationHolder.setApplication(null);
			ApplicationHolder.resetApplication();
		}
	}

	@Override
	public void windowClose(CloseEvent e) {
		switch (otevreneOkno) {
		case "filmoteka":
			horizontalSplit.setSecondComponent(new FilmotekaView());
			break;
		case "film":
			horizontalSplit.setSecondComponent(new FilmView());
			break;
		case "hodnoceni":
			horizontalSplit.setSecondComponent(new HodnoceniView());
			break;
		case "kvalita":
			horizontalSplit.setSecondComponent(new KvalitaView());
			break;
		case "uzivatele":
			horizontalSplit.setSecondComponent(new UzivateleView());
			break;
		}
	}

}
