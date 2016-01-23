package thebetweenlands.manual.widgets.text.rewrite;

public interface ITextContainerFont {
	public double getWidth(String text);
	public double getHeight(String text);
	public void render(String text, Object... args);
}