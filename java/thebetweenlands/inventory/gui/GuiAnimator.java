package thebetweenlands.inventory.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.recipes.misc.AnimatorRecipe;
import thebetweenlands.tileentities.TileEntityAnimator;

@SideOnly(Side.CLIENT)
public class GuiAnimator extends GuiContainer {
	private final ResourceLocation GUI_ANIMATOR = new ResourceLocation("thebetweenlands:textures/gui/animator.png");
	private final TileEntityAnimator tile;
	private EntityPlayer playerSent;
	private int updateTicks = 0;

	public GuiAnimator(EntityPlayer player, TileEntityAnimator tile) {
		super(new ContainerAnimator(player.inventory, tile));
		this.tile = tile;
		allowUserInput = false;
		xSize = 174;
		ySize = 164;
		playerSent = player;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_ANIMATOR);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		if (tile.isCrystalInslot()) {
			//Life crystal bar
			int lifeCrystalCount = 40 - tile.lifeCrystalLife / 3;
			this.drawTexturedModalRect(k + 39, l + 8 + lifeCrystalCount, 175, 2 + lifeCrystalCount, 6, 42);

			if(tile.isValidFocalItem()) {
				//Required life crystal bar
				int requiredLifeCrystal = tile.requiredLifeCount / 3;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 0.1F, 0.1F, 0.35F + (float)(Math.cos((this.updateTicks + partialTickTime) / 10.0F)+1.0F)/2.0F*0.65F);
				this.drawTexturedModalRect(k + 39, l + 8 + lifeCrystalCount, 175, 2 + lifeCrystalCount, 6, requiredLifeCrystal);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
		if (tile.isSlotInUse(0) && tile.isCrystalInslot() && tile.isSulfurInslot() && tile.fuelConsumed < tile.requiredFuelCount && tile.lifeCrystalLife >= tile.requiredLifeCount && tile.isValidFocalItem()) {
			//Fuel bar
			int fuelBurnProgress = tile.fuelBurnProgress;
			drawTexturedModalRect(k + 129, l + 8 + fuelBurnProgress, 175, 2 + fuelBurnProgress, 6, 42);

			double relTotalProgress = (tile.fuelConsumed + (tile.fuelBurnProgress / 42.0D)) / (double)tile.requiredFuelCount;
			if(relTotalProgress <= 0.5D) {
				int barWidth = (int)(relTotalProgress * 32);
				drawTexturedModalRect(k + 51, l + 65, 182, 18, barWidth * 2, 2);
				drawTexturedModalRect(k + 123 - barWidth * 2, l + 65, 254 - barWidth * 2, 18, barWidth * 2, 2);
			} 
			if(relTotalProgress > 0.5D && relTotalProgress <= 1.0D) {
				int barHeight = (int)(relTotalProgress * 32);
				drawTexturedModalRect(k + 51, l + 65 - barHeight + 16, 182, 18 - barHeight + 16, 72, 2 + barHeight - 16);
			}
		}
		if (tile.getStackInSlot(1) == null)
			renderSlot(ItemGeneric.createStack(BLItemRegistry.lifeCrystal, 1, 0).getIconIndex(), 34, 57);
		if (tile.getStackInSlot(2) == null)
			renderSlot(ItemGeneric.createStack(EnumItemGeneric.SULFUR).getIconIndex(), 124, 57);
	}

	private void renderSlot(IIcon icon, int iconX, int iconY) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f, 0.2f);
		mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		drawTexturedModelRectFromIcon(guiLeft + iconX, guiTop + iconY, icon, 16, 16);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		boolean shouldClose = false;
		ItemStack input = tile.getStackInSlot(0);
		if (input != null) {
			AnimatorRecipe recipe = AnimatorRecipe.getRecipe(input);
			if(recipe != null)
				shouldClose = recipe.getCloseOnFinish();
		}
		if (tile.itemAnimated && shouldClose)
			playerSent.closeScreen();
		this.updateTicks++;
	}
}
