package thebetweenlands.client.handler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;

public class OverlayHandler {
	private OverlayHandler() {}

	private static final ResourceLocation TAR_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/tar.png");
	private static final ResourceLocation STAGNANT_WATER_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/stagnant_water.png");

	@SubscribeEvent
	public static void onRenderOverlay(RenderBlockOverlayEvent event) {
		if(event.getOverlayType() == OverlayType.WATER) {
			Minecraft mc = Minecraft.getMinecraft();
			IBlockState state = mc.world.getBlockState(new BlockPos(mc.player));
			EntityPlayer player = mc.player;

			for (int i = 0; i < 8; ++i) {
				double x = player.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * player.width * 0.8F);
				double y = player.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double z = player.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * player.width * 0.8F);
				BlockPos checkPos = new BlockPos(x, y + (double)player.getEyeHeight(), z);
				IBlockState checkState = mc.world.getBlockState(checkPos);
				if (checkState.getMaterial() == BLMaterialRegistry.TAR || 
						checkState.getMaterial() == Material.WATER) {
					state = checkState;
				}
			}

			if(state.getBlock() == BlockRegistry.TAR) {
				event.setCanceled(true);

				float brightness = mc.player.getBrightness();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				renderOverlay(TAR_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if(state.getBlock() == BlockRegistry.STAGNANT_WATER) {
				event.setCanceled(true);

				float brightness = mc.player.getBrightness();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				renderOverlay(STAGNANT_WATER_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			}
		}
	}

	private static void renderOverlay(ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();
		float xOffset = -Minecraft.getMinecraft().player.rotationYaw / 64.0F;
		float yOffset = Minecraft.getMinecraft().player.rotationPitch / 64.0F;
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex((double)(4.0F + xOffset), (double)(4.0F + yOffset)).endVertex();
		vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex((double)(0.0F + xOffset), (double)(4.0F + yOffset)).endVertex();
		vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex((double)(0.0F + xOffset), (double)(0.0F + yOffset)).endVertex();
		vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex((double)(4.0F + xOffset), (double)(0.0F + yOffset)).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
	}
}
