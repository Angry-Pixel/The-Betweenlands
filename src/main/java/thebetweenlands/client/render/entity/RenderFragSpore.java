package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelFragSpore;
import thebetweenlands.common.entity.EntityFragSpore;

@SideOnly(Side.CLIENT)
public class RenderFragSpore extends Render<EntityFragSpore> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/frag_spore.png");
	private final ModelFragSpore model = new ModelFragSpore();

	public RenderFragSpore(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

	@Override
	public void doRender(EntityFragSpore entity, double x, double y, double z, float yaw, float partialTickTime) {
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1D, z);
		GlStateManager.scale(-0.75D, -0.75D, 0.75D);
		GlStateManager.rotate(22.5F * entity.ticksExisted - 1 + (entity.ticksExisted - entity.ticksExisted - 1) * partialTickTime, 0, 1, 0);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);
		model.render(0.0625F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFragSpore entity) {
		return TEXTURE;
	}
}
