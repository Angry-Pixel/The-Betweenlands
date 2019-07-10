package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSludgeWallJet;
import thebetweenlands.common.entity.EntityTriggeredSludgeWallJet;
import thebetweenlands.common.lib.ModInfo;

public class RenderTriggeredSludgeWallJet extends RenderLiving<EntityTriggeredSludgeWallJet> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/sludge_wall_jet.png");

	public RenderTriggeredSludgeWallJet(RenderManager manager) {
		super(manager, new ModelSludgeWallJet(), 0.5F);
	}

    @Override
    protected void preRenderCallback(EntityTriggeredSludgeWallJet wall_jet, float partialTickTime) {
    	float angle = wall_jet.animationTicksPrev + (wall_jet.animationTicks - wall_jet.animationTicksPrev) * partialTickTime;
        GlStateManager.rotate(angle, 0F, 1F, 0F);
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityTriggeredSludgeWallJet entity) {
		return TEXTURE;
	}
}