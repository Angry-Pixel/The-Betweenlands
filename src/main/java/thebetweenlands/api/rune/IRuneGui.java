package thebetweenlands.api.rune;

import java.util.Collection;

public interface IRuneGui {
	public void init(IRuneContainer container, int width, int height);

	public IRuneContainer getContainer();

	public void close();

	public void update();

	public void drawBackground(int mouseX, int mouseY);

	public void drawForeground(int mouseX, int mouseY);

	public void drawToken(IGuiRuneToken token, int centerX, int centerY, RuneMenuDrawingContext.Token context);
	
	public void drawTokenTooltip(IGuiRuneToken token, int centerX, int centerY, int mouseX, int mouseY, RuneMenuDrawingContext.Tooltip context);
	
	public void drawTokenConnection(IGuiRuneToken token, int targetX, int targetY, RuneMenuDrawingContext.Connection context);

	public boolean onKeyTyped(char typedChar, int keyCode, boolean handled);

	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton, boolean handled);

	public boolean onMouseReleased(int mouseX, int mouseY, int state, boolean handled);
	
	public void onMouseInput(int mouseX, int mouseY);

	public void onParentSizeSet(int w, int h);
	
	public boolean onStartTokenLinking(IGuiRuneToken token, int mouseX, int mouseY);

	public boolean onStartTokenUnlinking(IGuiRuneToken token, int mouseX, int mouseY);
	
	public IGuiRuneToken getInputToken(int tokenIndex);
	
	public Collection<? extends IGuiRuneToken> getInputTokens();
	
	public IGuiRuneToken getOutputToken(int tokenIndex);
	
	public Collection<? extends IGuiRuneToken> getOutputTokens();

	public int getMinX();

	public int getMinY();

	public int getMaxX();

	public int getMaxY();
}
