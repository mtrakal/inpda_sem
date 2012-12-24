package cz.mtrakal.inpda_sem;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

public class LoginWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4184911870351139566L;
	private final VerticalLayout layout = new VerticalLayout();
	private final Button btnLogin = new Button("Příhlásit se");
	private final TextField login = new TextField("Email");
	private final PasswordField password = new PasswordField("Heslo");
	private final int DELKA_HESLA = 3;

	public LoginWindow() {
		super("Požadována autentizace!");
		setName("login");
		setContent(layout);
		initUI();
	}

	private void initUI() {

		login.setInputPrompt("Email");
		login.setRequired(true);
		login.setRequiredError("Vyplňte email, který slouží jako login.");
		login.addValidator(new EmailValidator("Pole musí obsahovat platný email."));

		password.setInputPrompt("Heslo");
		password.setRequired(true);
		password.setRequiredError("Vyplňte heslo, musí obsahovat alespoň " + DELKA_HESLA + " znaky.");
		password.addValidator(new StringLengthValidator("Heslo neobsahuje alespoň " + DELKA_HESLA + " znaky.", DELKA_HESLA, -1, false));

		btnLogin.setIcon(new ThemeResource("../runo/icons/16/user.png"));

		final Panel loginPanel = new Panel("Prosím přihlašte se.");
		layout.addComponent(loginPanel);
		loginPanel.setWidth("250px");
		loginPanel.setHeight("150px");

		final FormLayout loginForm = new FormLayout();
		loginForm.setMargin(true);
		loginForm.setStyleName("loginForm");

		loginForm.addComponent(login);
		loginForm.addComponent(password);
		loginForm.addComponent(btnLogin);

		loginForm.setImmediate(true);

		loginPanel.setContent(loginForm);
		layout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		layout.setSizeFull();

		btnLogin.addListener(new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -666172780089383306L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (password.isValid() && login.isValid()) {
					try {
						FilmotekaApplication.getInstance().authenticate((String) login.getValue(), (String) password.getValue());
						open(new ExternalResource(FilmotekaApplication.getInstance().getURL()));
						showNotification("Přihlášení proběhlo úspěšně", Notification.TYPE_HUMANIZED_MESSAGE);
					} catch (Exception e) {
						showNotification(e.getMessage(), Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
	}
}
