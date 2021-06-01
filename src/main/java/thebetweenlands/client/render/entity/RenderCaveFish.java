package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelCaveFish;
import thebetweenlands.common.entity.mobs.EntityCaveFish;

@SideOnly(Side.CLIENT)
public class RenderCaveFish extends RenderLiving<EntityCaveFish> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/cave_fish.png");
    public static final ResourceLocation TEXTURE_LEADER = new ResourceLocation("thebetweenlands:textures/entity/cave_fish_leader.png");
    public final static ModelCaveFish CAVE_FISH_MODEL = new ModelCaveFish();

    public RenderCaveFish(RenderManager rendermanagerIn) {
        super(rendermanagerIn, CAVE_FISH_MODEL, 0.5f);
        addLayer(new LayerOverlay<EntityCaveFish>(this, new ResourceLocation("thebetweenlands:textures/entity/cave_fish_leader_glow.png")) {
        	@Override
        	public void doRenderLayer(EntityCaveFish entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        		if(entity.isLeader()) {
        			super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        		}
        	}
        }.setGlow(true));
    }
	
	@Override
	protected void preRenderCallback(EntityCaveFish entitylivingbaseIn, float partialTickTime) {
		if(!entitylivingbaseIn.isLeader()) {
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
		}
		GlStateManager.translate(0, 0.5f, 0);
	}

	@Override
	protected void renderModel(EntityCaveFish entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		GlStateManager.enableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		GlStateManager.disableBlend();
		GlStateManager.disableCull();
	}
	
    @Override
    protected ResourceLocation getEntityTexture(EntityCaveFish entity) {
    	if(entity.isLeader())
    		return TEXTURE_LEADER;
    	return TEXTURE;
    }
}
