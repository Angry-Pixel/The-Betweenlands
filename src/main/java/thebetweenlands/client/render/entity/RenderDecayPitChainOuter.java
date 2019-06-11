package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelDecayPitChain;
import thebetweenlands.common.entity.EntityDecayPitChainOuter;
import thebetweenlands.common.lib.ModInfo;

public class RenderDecayPitChainOuter extends Render<EntityDecayPitChainOuter> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_chain.png");
	private final ModelDecayPitChain CHAIN_MODEL = new ModelDecayPitChain();

	public RenderDecayPitChainOuter(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitChainOuter entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float scroll = entity.animationTicksPrev * 0.0625F + (entity.animationTicks * 0.0625F - entity.animationTicksPrev * 0.0625F) * partialTicks;
		bindTexture(TEXTURE);

		if(entity.isMoving()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, (entity.isRaising() ? y + 0.5F + scroll : y + 5.5F - scroll) , z);
			GlStateManager.scale(-1F, -1F, 1F);
			GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
			CHAIN_MODEL.render(entity, 0.0625F);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F + (entity.isRaising() ? scroll : - scroll) , z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
		CHAIN_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 2.5F + (entity.isRaising() ? scroll : - scroll) , z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
		CHAIN_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 3.5F + (entity.isRaising() ? scroll : - scroll) , z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
		CHAIN_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 4.5F + (entity.isRaising() ? scroll : - scroll) , z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
		CHAIN_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitChainOuter entity) {
		return TEXTURE;
	}

}