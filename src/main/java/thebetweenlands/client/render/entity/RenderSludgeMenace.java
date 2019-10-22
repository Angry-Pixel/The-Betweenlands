package thebetweenlands.client.render.entity;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace.Bulge;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.util.RenderUtils;

@SideOnly(Side.CLIENT)
public class RenderSludgeMenace extends RenderWallLivingRoot implements IMultipartDummyRendererDelegate<EntitySludgeMenace> {
	public static final ResourceLocation HULL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sludge_menace_hull.png");

	public RenderSludgeMenace(RenderManager renderManager) {
		super(renderManager, HULL_TEXTURE);
	}

	@Override
	public void doRender(EntityWallLivingRoot entity, double x, double y, double z, float entityYaw, float partialTicks) {
		EntitySludgeMenace sludge = (EntitySludgeMenace) entity;

		int frameCounter = RenderUtils.getFrameCounter();

		if(sludge.renderedFrame != frameCounter) {
			sludge.renderedFrame = frameCounter;

			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	protected float getUvScale(EntityWallLivingRoot entity, float partialTicks) {
		return 1;
	}

	@Override
	protected float calculateHullContraction(EntityWallLivingRoot entity, int i, float armSize, float partialTicks) {
		List<Bulge> bulges = ((EntitySludgeMenace) entity).getBulges(partialTicks);

		float bulgeSize = 0.0f;

		if(bulges != null) {
			for(Bulge bulge : bulges) {
				float bulgePos = bulge.renderPosition * entity.armSegments.size();
				bulgeSize += (float) Math.cos(Math.min(Math.abs(bulgePos - i) / bulge.type.length, Math.PI / 2)) * bulge.renderSize;
			}
		}

		float baseSize = (1 - i / (float)(entity.armSegments.size() - 1)) * (armSize -  0.2f) + 0.2f;
		float animation = ((float)Math.sin(-(entity.ticksExisted + partialTicks) * 0.2f + i) + 0.5f) * 0.5f * 0.2f + 0.8f;

		return Math.max(Math.min(bulgeSize, 1.2f), baseSize * animation);
	}

	@Override
	protected void renderBodyHull(EntityWallLivingRoot entity, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);

		//GlStateManager.enableCull();
		//GlStateManager.cullFace(CullFace.FRONT); //oops hull seems to be inside-out

		super.renderBodyHull(entity, partialTicks);

		//GlStateManager.disableCull();
		//GlStateManager.cullFace(CullFace.BACK);

		GlStateManager.disableBlend();
	}

	@Override
	public void setRenderFromMultipart(EntityMultipartDummy dummy) {

	}
}
