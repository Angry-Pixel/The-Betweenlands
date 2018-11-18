package thebetweenlands.api.rune.gui;

import java.util.Collection;

public interface IRuneGui {
	public void init(IRuneContainerContext context, IRuneContainer container, int width, int height);

	public IRuneContainerContext getContext();
	
	public IRuneContainer getContainer();

	public void close();

	public void update();

	public void drawBackground(int mouseX, int mouseY);

	public void drawForeground(int mouseX, int mouseY);

	public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, boolean linked);

	public boolean onKeyTyped(char typedChar, int keyCode, boolean handled);

	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton, boolean handled);

	public boolean onMouseReleased(int mouseX, int mouseY, int state, boolean handled);
	
	public void onMouseInput();

	public void onParentSizeSet(int w, int h);
	
	public boolean onStartMarkLinking(IGuiRuneMark mark, int mouseX, int mouseY);

	public Collection<? extends IGuiRuneMark> getInteractableMarks();

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
