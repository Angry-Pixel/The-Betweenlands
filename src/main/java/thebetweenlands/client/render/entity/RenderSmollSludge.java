package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSludge;
import thebetweenlands.client.render.model.entity.ModelSmollSludge;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;

@SideOnly(Side.CLIENT)
public class RenderSmollSludge extends RenderLiving<EntitySmollSludge> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/smoll_sludge.png");

	public RenderSmollSludge(RenderManager renderManager) {
		super(renderManager, new ModelSmollSludge(), 0.0F);
	}

	@Override
	public void doRender(EntitySmollSludge entity, double x, double y, double z, float entityYaw, float partialTicks) {

	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySmollSludge entity) {
		return TEXTURE;
	}
}
