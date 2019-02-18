package thebetweenlands.common.item.armor;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemBLArmor extends ItemArmor implements IAnimatorRepairable {
	protected final String armorTexture1, armorTexture2;
	protected final String gemArmorTextures[][] = new String[CircleGemType.values().length][2];
	protected final String armorName;

	public ItemBLArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String armorName) {
		super(materialIn, renderIndexIn, equipmentSlotIn);

		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.armorName = armorName;

		this.armorTexture1 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
		this.armorTexture2 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";

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
		String texture1 = this.armorTexture1;
		String texture2 = this.armorTexture2;

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
	
	@OnlyIn(Dist.CLIENT)
	@Nullable
	protected ResourceLocation getOverlayTexture(ItemStack stack, EntityPlayer player, float partialTicks) {
		return null;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Nullable
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, EntityPlayer player, float partialTicks, boolean left) {
		return null;
	}
	
	@OnlyIn(Dist.CLIENT)
	protected int getOverlayTextureFilter(ItemStack stack, EntityPlayer player, float partialTicks) {
		return GL11.GL_NEAREST;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks) {
		int filter = this.getOverlayTextureFilter(stack, player, partialTicks);
		ResourceLocation overlay = this.getOverlayTexture(stack, player, partialTicks);
		
		if(overlay != null) {
			GlStateManager.disableDepth();
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GlStateManager.disableAlphaTest();
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			GlStateManager.color(1, 1, 1);
			
			Minecraft.getInstance().getTextureManager().bindTexture(overlay);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			
			float width = resolution.getScaledWidth();
			float height = resolution.getScaledHeight();
			
			ResourceLocation sideOverlayLeft = this.getOverlaySideTexture(stack, player, partialTicks, false);
			ResourceLocation sideOverlayRight = this.getOverlaySideTexture(stack, player, partialTicks, false);
			
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
				
				Minecraft.getInstance().getTextureManager().bindTexture(sideOverlayLeft);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
				
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(0, height, -90).tex(1 - texWidth, 1).endVertex();
				vertexBuffer.pos(width / 2 - height / 2, height, -90).tex(1, 1).endVertex();
				vertexBuffer.pos(width / 2 - height / 2, 0, -90).tex(1, 0).endVertex();
				vertexBuffer.pos(0, 0, -90).tex(1 - texWidth, 0).endVertex();
				tessellator.draw();
				
				Minecraft.getInstance().getTextureManager().bindTexture(sideOverlayRight);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
				
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
			
			GlStateManager.depthMask(true);
			GlStateManager.enableDepth();
			GlStateManager.enableAlphaTest();
			GlStateManager.color(1, 1, 1);
		}
	}
}
