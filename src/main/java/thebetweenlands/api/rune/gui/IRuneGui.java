package thebetweenlands.api.rune.gui;

import java.util.Collection;

public interface IRuneGui {
	public void init(IRuneContainerContext context, IRuneContainer container);

	public IRuneContainer getContainer();
	
	public void close();

	public void update();

	public void draw(int mouseX, int mouseY);

	public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, boolean linked);

	public boolean onKeyTyped(char typedChar, int keyCode);

	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton);

	public boolean onMouseReleased(int mouseX, int mouseY, int state);

	public void onParentResized(int w, int h);

	public Collection<IGuiRuneMark> getInteractableMarks();

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
