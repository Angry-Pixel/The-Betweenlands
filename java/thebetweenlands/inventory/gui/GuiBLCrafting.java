package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerBLCraftingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBLCrafting extends GuiContainer {

	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

	public GuiBLCrafting(InventoryPlayer playerInventory, World world, int x, int y, int z) {
		super(new ContainerBLCraftingTable(playerInventory, world, x, y, z));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 28, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(craftingTableGuiTextures);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
}