package cz.mtrakal.inpda_sem;

public class ApplicationHolder {
	
	private static ThreadLocal<FilmotekaApplication> app = new ThreadLocal<FilmotekaApplication>();
	
	public static void setApplication(FilmotekaApplication application) {
		app.set(application);
	}
	
	public static void resetApplication() {
		app.remove();
	}
	
	public static FilmotekaApplication getApplication() {
		return app.get();
	}
}
