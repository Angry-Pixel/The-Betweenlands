package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelSludgeBall;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;

@OnlyIn(Dist.CLIENT)
public class RenderSludgeBall extends Render<EntitySludgeBall> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
	private final ModelSludgeBall model = new ModelSludgeBall();

	public RenderSludgeBall(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

	@Override
	public void doRender(EntitySludgeBall entity, double x, double y, double z, float yaw, float partialTickTime) {
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);
		model.render(0.0625F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludgeBall entity) {
		return TEXTURE;
	}
}
