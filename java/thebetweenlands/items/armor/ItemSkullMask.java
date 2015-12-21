package thebetweenlands.items.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterials;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Bart on 11-9-2015.
 */
public class ItemSkullMask extends ItemArmor implements IManualEntryItem {
	private static final ResourceLocation SKULL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/skullMask.png");

	public ItemSkullMask() {
		super(BLMaterials.armorBone, 2, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "thebetweenlands:textures/armour/skullMask.png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHelmetOverlay(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, net.minecraft.client.gui.ScaledResolution resolution,
		float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glColor3f(1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(SKULL_TEXTURE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float width = resolution.getScaledWidth();
		float height = resolution.getScaledHeight();
		float offset = 0.5F - width / height / 2;
		tessellator.addVertexWithUV(0, height, -90, offset, 1);
		tessellator.addVertexWithUV(width, height, -90, 1 - offset, 1);
		tessellator.addVertexWithUV(width, 0, -90, 1 - offset, 0);
		tessellator.addVertexWithUV(0, 0, -90, offset, 0);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor3f(1, 1, 1);
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == ItemGeneric.EnumItemGeneric.SLIMY_BONE.id;
	}

	@Override
	public String manualName(int meta) {
		return "skullMask";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}
}
