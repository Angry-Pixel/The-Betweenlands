package thebetweenlands.api.rune;

public interface IGuiRuneMark {
	public boolean isInside(int centerX, int centerY, int mouseX, int mouseY);

	public int getMarkIndex();

	public int getCenterX();

	public int getCenterY();
	
	public boolean isInteractable();
}
