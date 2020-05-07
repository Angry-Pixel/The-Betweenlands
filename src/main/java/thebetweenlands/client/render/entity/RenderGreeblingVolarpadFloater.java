package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelGreeblingVolarpadFloater;
import thebetweenlands.common.entity.mobs.EntityGreeblingVolarpadFloater;
import thebetweenlands.common.registries.BlockRegistry;

@SideOnly(Side.CLIENT)
public class RenderGreeblingVolarpadFloater extends Render<EntityGreeblingVolarpadFloater> {
	public final ResourceLocation GREEBLING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");
	private final ModelGreeblingVolarpadFloater GREEBLING_MODEL = new ModelGreeblingVolarpadFloater();

	public RenderGreeblingVolarpadFloater(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityGreeblingVolarpadFloater entity, double x, double y, double z, float yaw, float partialTickTime) {
		renderParachuteDrop(entity, x, y, z, yaw, partialTickTime);
	}

	public void renderParachuteDrop(EntityGreeblingVolarpadFloater entity, double x, double y, double z, float yaw, float partialTickTime) {
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(GREEBLING_TEXTURE);

		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.smoothedAngle(partialTickTime), 0, 1, 0);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0625D, 1.5D, -0.125D);
		GlStateManager.scale(-1D, -1D, 1D);
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GREEBLING_MODEL.render();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.125D, 1.125D, -0.35D);
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.rotate(180F, 0F, 1F, 0F);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(BlockRegistry.VOLARPAD.getDefaultState(), 1.0F);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGreeblingVolarpadFloater entity) {
		return null;
	}
}