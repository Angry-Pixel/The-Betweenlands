package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace;
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
		return super.calculateHullContraction(entity, i, armSize, partialTicks) * (((float)Math.sin(-(entity.ticksExisted + partialTicks) * 0.2f + i) + 0.5f) * 0.5f * 0.2f + 0.8f);
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
