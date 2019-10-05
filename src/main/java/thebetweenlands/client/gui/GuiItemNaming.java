package thebetweenlands.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.ContainerPouch;
import thebetweenlands.common.network.serverbound.MessageItemNaming;


public class GuiItemNaming extends GuiContainer {
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/item_renaming.png");

	private GuiTextField textFieldName;
	private EntityPlayer player;
	private EnumHand hand;

	public GuiItemNaming(EntityPlayer player, EnumHand hand) {
		super(new ContainerPouch(player, player.inventory, null));
		this.xSize = 181;
		this.ySize = 55;
		this.player = player;
		this.hand = hand;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.drawDefaultBackground();
		GlStateManager.color(1, 1, 1, 1);
		this.mc.renderEngine.bindTexture(GUI_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.textFieldName = new GuiTextField(0, this.fontRenderer, 22, 17, 136, 20);
		this.textFieldName.setMaxStringLength(20);
		this.textFieldName.setFocused(false);
		this.textFieldName.setTextColor(5635925);
		this.buttonList.clear();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		this.buttonList.add(new GuiItemNamingButton(1, xOffSet, yOffSet - 18, 46, 18, I18n.format("container.bl.item_renaming.save")));
	}

	@Override
	public void updateScreen() {
		this.textFieldName.updateCursorCounter();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.textFieldName.drawTextBox();
	}

	@Override
	protected void keyTyped(char key, int keycode) throws IOException {
		this.textFieldName.textboxKeyTyped(key, keycode);
		if(keycode == Keyboard.KEY_ESCAPE) {
			this.player.closeScreen();
		} else if (!(keycode != Keyboard.KEY_NONE && this.textFieldName.isFocused())) {
			super.keyTyped(key, keycode);
		}
	}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		this.textFieldName.mouseClicked(22, 17, k);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof GuiItemNamingButton) {
			if (button.id == 1) {
				if (StringUtils.isNullOrEmpty(this.textFieldName.getText())) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageItemNaming("", this.hand));
				} else {
					TheBetweenlands.networkWrapper.sendToServer(new MessageItemNaming(this.textFieldName.getText(), this.hand));
				}

				this.player.closeScreen();
			}
		}
	}
}