package thebetweenlands.api.rune;

public interface IGuiRuneToken {
	public boolean isInside(int centerX, int centerY, int mouseX, int mouseY);

	public int getTokenIndex();

	public int getCenterX();

	public int getCenterY();
	
	public boolean isInteractable();
}
