package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelEmberling;
import thebetweenlands.common.entity.mobs.EntityEmberling;

@SideOnly(Side.CLIENT)
public class RenderEmberling extends RenderLiving<EntityEmberling> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/emberling.png");
	public static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("thebetweenlands:textures/entity/emberling_sleeping.png");

	public RenderEmberling(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEmberling(), 0.5F);
        addLayer(new LayerOverlay<EntityEmberling>(this, new ResourceLocation("thebetweenlands:textures/entity/emberling_glow.png")) {
        	@Override
        	public void doRenderLayer(EntityEmberling entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        		if(!entity.isSitting()) {
        			super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        		}
        	}
        }.setGlow(true));
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityEmberling entity) {
		if(entity.isSitting())
			return TEXTURE_SLEEPING;
		return TEXTURE;
	}
}