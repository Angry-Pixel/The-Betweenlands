package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.inventory.container.ContainerAnimator;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;

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
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_ANIMATOR);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		if (tile.isCrystalInslot()) {
			//Life crystal bar
			int lifeCrystalCount = (int)(40 - (float)tile.lifeCrystalLife / 3.0f);
			this.drawTexturedModalRect(k + 39, l + 8 + lifeCrystalCount, 175, 2 + lifeCrystalCount, 6, 42);

			if(tile.isValidFocalItem()) {
				//Required life crystal bar
				int requiredLifeCrystal = tile.requiredLifeCount / 3;
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 0.1F, 0.1F, 0.35F + (float)(Math.cos((this.updateTicks + partialTickTime) / 10.0F)+1.0F)/2.0F*0.65F);
				this.drawTexturedModalRect(k + 39, l + 8 + lifeCrystalCount, 175, 2 + lifeCrystalCount, 6, requiredLifeCrystal);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableBlend();
			}
		}

		if (tile.isRunning()) {
			//Fuel bar
			int fuelBurnProgress = tile.fuelBurnProgress;
			drawTexturedModalRect(k + 129, l + 8 + fuelBurnProgress, 175, 2 + fuelBurnProgress, 6, 42);

			double relTotalProgress = (tile.fuelConsumed + (tile.fuelBurnProgress / 42.0D)) / (double)tile.requiredFuelCount;

			if(relTotalProgress <= 0.66D) {
				int barWidth = (int)(relTotalProgress / 0.66D * 32);
				drawTexturedModalRect(k + 51, l + 65, 182, 18, barWidth, 2);
				drawTexturedModalRect(k + 123 - barWidth, l + 65, 254 - barWidth, 18, barWidth, 2);
			}
			if(relTotalProgress > 0.66D && relTotalProgress <= 1.0D) {
				int barHeight = (int)((relTotalProgress - 0.66D) / 0.4D * 19);
				drawTexturedModalRect(k + 51, l + 65 - barHeight, 182, 18 - barHeight, 72, 2 + barHeight);
			}
		}
		if (tile.getStackInSlot(1).isEmpty())
			renderSlot(new ItemStack(ItemRegistry.LIFE_CRYSTAL, 1, 0), k + 34, l + 57);
		if (tile.getStackInSlot(2).isEmpty())
			renderSlot(ItemMisc.EnumItemMisc.SULFUR.create(1), k + 124, l + 57);
	}

	private void renderSlot(ItemStack stack, int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GL14.glBlendColor(0, 0, 0, 0.35f);
		GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA); //ugly hack
		GlStateManager.pushMatrix();
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.popMatrix();
		GL14.glBlendColor(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA); //ugly
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		boolean shouldClose = false;
		ItemStack input = tile.getStackInSlot(0);
		if (!input.isEmpty()) {
			IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(input);
			if(recipe != null)
				shouldClose = recipe.getCloseOnFinish(input);
		}
		if (tile.itemAnimated && shouldClose)
			playerSent.closeScreen();
		this.updateTicks++;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
