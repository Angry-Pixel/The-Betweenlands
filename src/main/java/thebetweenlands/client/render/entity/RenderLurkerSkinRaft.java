package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelLurkerSkinShield;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderLurkerSkinRaft extends Render<EntityBoat> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/lurker_skin_raft.png");

	protected ModelLurkerSkinShield model = new ModelLurkerSkinShield();

	public RenderLurkerSkinRaft(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityBoat entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.pushMatrix();
		this.setupTranslation(x, y, z);
		this.setupRotation(entity, yaw, partialTicks);
		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		this.model.renderAsRaft(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, yaw, partialTicks);
	}

	public void setupRotation(EntityBoat entity, float yaw, float partialTicks) {
		GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
		float wobbleTimer = (float)entity.getTimeSinceHit() - partialTicks;
		float wobbleStrength = entity.getDamageTaken() - partialTicks;

		if (wobbleStrength < 0.0F) {
			wobbleStrength = 0.0F;
		}

		if (wobbleTimer > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(wobbleTimer) * wobbleTimer * wobbleStrength / 10.0F * (float)entity.getForwardDirection(), 1.0F, 0.0F, 0.0F);
		}

		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
	}

	public void setupTranslation(double x, double y, double z) {
		GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoat entity) {
		return TEXTURE;
	}
}