package thebetweenlands.common.item.armor;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemBLArmor extends ItemArmor implements IAnimatorRepairable {
	protected final String armorTextures[];
	protected final String gemArmorTextures[][] = new String[CircleGemType.values().length][2];
	protected final String armorName;

	public ItemBLArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String armorName) {
		super(materialIn, renderIndexIn, equipmentSlotIn);

		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.armorName = armorName;

		this.armorTextures = new String[] { ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png", ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png" };

		CircleGemHelper.addGemPropertyOverrides(this);
	}

	/**
	 * Adds an armor texture override for the specified gem
	 * @param type
	 * @param topHalf
	 * @param bottomHalf
	 * @return
	 */
	public ItemBLArmor setGemArmorTextureOverride(CircleGemType type, String armorName) {
		this.gemArmorTextures[type.ordinal()][0] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
		this.gemArmorTextures[type.ordinal()][1] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";
		return this;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		String texture1 = this.armorTextures[0];
		String texture2 = this.armorTextures[1];

		CircleGemType gem = CircleGemHelper.getGem(stack);

		if(this.gemArmorTextures[gem.ordinal()][0] != null) {
			texture1 = this.gemArmorTextures[gem.ordinal()][0];
		}
		if(this.gemArmorTextures[gem.ordinal()][1] != null) {
			texture2 = this.gemArmorTextures[gem.ordinal()][1];
		}

		if(slot == EntityEquipmentSlot.LEGS) {
			return texture2;
		} else {
			return texture1;
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.getArmorMaterial());
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.getArmorMaterial());
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.getArmorMaterial());
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.getArmorMaterial());
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	protected ResourceLocation getOverlayTexture(ItemStack stack, EntityPlayer player, float partialTicks) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, EntityPlayer player, float partialTicks, boolean left) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks) {
		ResourceLocation overlay = this.getOverlayTexture(stack, player, partialTicks);
		if(overlay != null) {
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.disableDepth();
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

			renderRepeatingOverlay((float)resolution.getScaledWidth_double(), (float)resolution.getScaledHeight_double(), overlay, this.getOverlaySideTexture(stack, player, partialTicks, true), this.getOverlaySideTexture(stack, player, partialTicks, false));

			GlStateManager.depthMask(true);
			GlStateManager.enableDepth();
			GlStateManager.enableAlpha();
			GlStateManager.color(1, 1, 1, 1);
		}
	}

	public static void renderRepeatingOverlay(float width, float height, ResourceLocation overlay, @Nullable ResourceLocation sideOverlayLeft, @Nullable ResourceLocation sideOverlayRight) {
		if(overlay != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(overlay);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexBuffer = tessellator.getBuffer();

			if(sideOverlayLeft != null && sideOverlayRight != null) {
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(width / 2 - height / 2, height, -90).tex(0, 1).endVertex();
				vertexBuffer.pos(width / 2 + height / 2, height, -90).tex(1, 1).endVertex();
				vertexBuffer.pos(width / 2 + height / 2, 0, -90).tex(1, 0).endVertex();
				vertexBuffer.pos(width / 2 - height / 2, 0, -90).tex(0, 0).endVertex();
				tessellator.draw();

				float texWidth = (width / 2 - height / 2) / height;

				Minecraft.getMinecraft().getTextureManager().bindTexture(sideOverlayLeft);

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(0, height, -90).tex(1 - texWidth, 1).endVertex();
				vertexBuffer.pos(width / 2 - height / 2, height, -90).tex(1, 1).endVertex();
				vertexBuffer.pos(width / 2 - height / 2, 0, -90).tex(1, 0).endVertex();
				vertexBuffer.pos(0, 0, -90).tex(1 - texWidth, 0).endVertex();
				tessellator.draw();

				Minecraft.getMinecraft().getTextureManager().bindTexture(sideOverlayRight);

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(width / 2 + height / 2, height, -90).tex(0, 1).endVertex();
				vertexBuffer.pos(width, height, -90).tex(texWidth, 1).endVertex();
				vertexBuffer.pos(width, 0, -90).tex(texWidth, 0).endVertex();
				vertexBuffer.pos(width / 2 + height / 2, 0, -90).tex(0, 0).endVertex();
				tessellator.draw();
			} else {
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				float offset = 0.5F - width / height / 2;
				vertexBuffer.pos(0, height, -90).tex(offset, 1).endVertex();
				vertexBuffer.pos(width, height, -90).tex(1 - offset, 1).endVertex();
				vertexBuffer.pos(width, 0, -90).tex(1 - offset, 0).endVertex();
				vertexBuffer.pos(0, 0, -90).tex(offset, 0).endVertex();
				tessellator.draw();
			}
		}
	}
}
