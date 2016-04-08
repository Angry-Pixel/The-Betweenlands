package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.inventory.container.ContainerLurkerSkinPouch;
import thebetweenlands.network.message.MessagePouchNaming;

public class GuiPouchNaming extends GuiContainer {

	private static final ResourceLocation GUI_POUCH_NAMING = new ResourceLocation("thebetweenlands:textures/gui/lurkerPouchNamingGui.png");
	private GuiTextField textFieldName;
	private EntityPlayer playerSent;

	public GuiPouchNaming(EntityPlayer player) {
		super(new ContainerLurkerSkinPouch(player, player.inventory, null));
		xSize = 177;
		ySize = 55;
		playerSent = player;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1, 1, 1, 1);
		super.mc.renderEngine.bindTexture(GUI_POUCH_NAMING);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
		textFieldName = new GuiTextField(fontRendererObj, 20, 17, 136, 20);
		textFieldName.setMaxStringLength(20);
		textFieldName.setFocused(false);
		textFieldName.setTextColor(5635925);
		buttonList.clear();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		buttonList.add(new GuiPouchNamingButton(0, xOffSet, yOffSet - 16, 32, 16, "Save"));
	}

	@Override
	public void updateScreen() {
		textFieldName.updateCursorCounter();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		textFieldName.drawTextBox();
	}

	@Override
	protected void keyTyped(char key, int keycode) {
		textFieldName.textboxKeyTyped(key, keycode);
		if (!(keycode != Keyboard.KEY_NONE && textFieldName.isFocused()))
			super.keyTyped(key, keycode);
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		textFieldName.mouseClicked(20, 17, k);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton instanceof GuiPouchNamingButton)
			if (guibutton.id == 0) {
				if (StringUtils.isNullOrEmpty(textFieldName.getText()))
					TheBetweenlands.networkWrapper.sendToServer(new MessagePouchNaming(playerSent, StatCollector.translateToLocal("container.lurkerSkinPouch")));
				else
					TheBetweenlands.networkWrapper.sendToServer(new MessagePouchNaming(playerSent, textFieldName.getText()));
				playerSent.closeScreen();
			}
	}
}