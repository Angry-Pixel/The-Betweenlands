package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitChain;
import thebetweenlands.common.entity.EntityDecayPitChain;
import thebetweenlands.common.lib.ModInfo;
@SideOnly(Side.CLIENT)
public class RenderDecayPitChain extends Render<EntityDecayPitChain> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_chain.png");
	private final ModelDecayPitChain CHAIN_MODEL = new ModelDecayPitChain();

	public RenderDecayPitChain(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitChain entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float scroll = entity.animationTicksPrev * 0.0078125F + (entity.animationTicks * 0.0078125F - entity.animationTicksPrev * 0.0078125F) * partialTicks;
		bindTexture(TEXTURE);

		if(entity.isMoving()) {
			GlStateManager.pushMatrix();
			if(entity.isRaising()) {
				GlStateManager.translate(x, y + 0.5F + scroll, z);
				GlStateManager.scale(-1F, -1F, 1F);
				GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
				CHAIN_MODEL.render(0.0625F);
			}
			if(!entity.isRaising()){
				GlStateManager.translate(x, y + entity.getLength() + 1.5F - scroll , z);
				GlStateManager.scale(-1F, -1F, 1F);
				GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
				CHAIN_MODEL.render(0.0625F);
			}
			GlStateManager.popMatrix();
		}

		for (int len = 1; len <= entity.getLength(); len++) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + len + 0.5F + (entity.isRaising() ? scroll : -scroll), z);
			GlStateManager.scale(-1F, -1F, 1F);
			GlStateManager.rotate(entity.getFacingRender() * 90F, 0F, 1F, 0F);
			CHAIN_MODEL.render(0.0625F);
			GlStateManager.popMatrix();
		}
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitChain entity) {
		return TEXTURE;
	}

}