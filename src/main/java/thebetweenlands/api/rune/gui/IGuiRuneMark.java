package thebetweenlands.api.rune.gui;

public interface IGuiRuneMark {
	public boolean isInside(int mouseX, int mouseY);

	public int getMarkIndex();

	public int getCenterX();

	public int getCenterY();
}
