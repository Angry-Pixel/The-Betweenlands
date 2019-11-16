package thebetweenlands.api.rune;

import java.util.Collection;

public interface IRuneGui {
	public void init(IRuneContainer container, int width, int height);

	public IRuneContainer getContainer();

	public void close();

	public void update();

	public void drawBackground(int mouseX, int mouseY);

	public void drawForeground(int mouseX, int mouseY);

	public void drawMark(IGuiRuneMark mark, int centerX, int centerY, RuneMenuDrawingContext.Mark context);
	
	public void drawMarkTooltip(IGuiRuneMark mark, int centerX, int centerY, int mouseX, int mouseY, RuneMenuDrawingContext.Tooltip context);
	
	public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, RuneMenuDrawingContext.Connection context);

	public boolean onKeyTyped(char typedChar, int keyCode, boolean handled);

	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton, boolean handled);

	public boolean onMouseReleased(int mouseX, int mouseY, int state, boolean handled);
	
	public void onMouseInput();

	public void onParentSizeSet(int w, int h);
	
	public boolean onStartMarkLinking(IGuiRuneMark mark, int mouseX, int mouseY);

	public boolean onStartMarkUnlinking(IGuiRuneMark mark, int mouseX, int mouseY);
	
	public IGuiRuneMark getInputMark(int markIndex);
	
	public Collection<? extends IGuiRuneMark> getInputMarks();
	
	public IGuiRuneMark getOutputMark(int markIndex);
	
	public Collection<? extends IGuiRuneMark> getOutputMarks();

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
