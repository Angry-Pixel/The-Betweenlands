package thebetweenlands.client.gui;

import org.lwjgl.glfw.GLFW;

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
		GlStateManager.color4f(1, 1, 1, 1);
		this.mc.textureManager.bindTexture(GUI_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.textFieldName = new GuiTextField(0, this.fontRenderer, 22, 17, 136, 20);
		this.textFieldName.setMaxStringLength(20);
		this.textFieldName.setFocused(false);
		this.textFieldName.setTextColor(5635925);
		this.buttons.clear();
		int xOffSet = (width - xSize) / 2;
		int yOffSet = (height - ySize) / 2;
		this.buttons.add(new GuiItemNamingButton(1, xOffSet, yOffSet - 18, 46, 18, I18n.format("container.lurker_skin_pouch.naming.save")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				if (StringUtils.isNullOrEmpty(GuiItemNaming.this.textFieldName.getText())) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageItemNaming("", GuiItemNaming.this.hand));
				} else {
					TheBetweenlands.networkWrapper.sendToServer(new MessageItemNaming(GuiItemNaming.this.textFieldName.getText(), GuiItemNaming.this.hand));
				}

				GuiItemNaming.this.player.closeScreen();
			}
		});
	}

	@Override
	public void tick() {
		this.textFieldName.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		this.textFieldName.drawTextField(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if(key == GLFW.GLFW_KEY_ESCAPE) {
			this.player.closeScreen();
			return true;
		} else {
			if(!this.textFieldName.keyPressed(key, scanCode, modifiers)) {
				return super.keyPressed(key, scanCode, modifiers);
			}
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double i, double j, int k) {
		return this.textFieldName.mouseClicked(22, 17, k);
	}
}