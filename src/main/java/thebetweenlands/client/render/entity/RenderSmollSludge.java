package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSmollSludge;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.world.event.EventSpoopy;

@SideOnly(Side.CLIENT)
public class RenderSmollSludge extends RenderSludge {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/smoll_sludge.png");

	public RenderSmollSludge(RenderManager renderManager) {
		super(renderManager, new ModelSmollSludge());
	}
	
	@Override
	protected void preRenderCallback(EntitySludge entity, float partialTicks) {
		float squishFactor = entity.getSquishFactor(partialTicks) / 1.5F;
		float scale = 1.0F / (squishFactor + 1.0F);

		GlStateManager.translate(0, (1.0F - entity.scale.getAnimationProgressSin(partialTicks)) * 2.5F, 0);
		GlStateManager.scale(scale, 1.0F / scale, scale);

		if (EventSpoopy.isSpoooopy(entity.world)) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.185F, -0.065F, 0.185F);
			GlStateManager.scale(-scale * 0.35F, -1.0F / scale * 0.35F, scale * 0.35F);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.LIT_PUMPKIN.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludge entity) {
		return TEXTURE;
	}
}
