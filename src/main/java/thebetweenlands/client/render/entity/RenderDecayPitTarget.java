package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitPlug;
import thebetweenlands.client.render.model.entity.ModelDecayPitTarget;
import thebetweenlands.client.render.model.entity.ModelShieldCog;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityDecayPitTargetPart;
import thebetweenlands.common.lib.ModInfo;
@SideOnly(Side.CLIENT)
public class RenderDecayPitTarget extends Render<EntityDecayPitTarget> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_plug.png");
	private final ModelDecayPitPlug PLUG_MODEL = new ModelDecayPitPlug();
	public static final ResourceLocation COG_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/barrishee.png");
	private final ModelShieldCog COG_MODEL = new ModelShieldCog();
	private final ModelDecayPitTarget TARGET_MODEL = new ModelDecayPitTarget();
	public static final ResourceLocation TARGET_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_target.png");

	public RenderDecayPitTarget(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float scroll = entity.animationTicksChainPrev * 0.0078125F + (entity.animationTicksChain * 0.0078125F - entity.animationTicksChainPrev * 0.0078125F) * partialTicks;
		double offsetY = entity.height;
		double smoothedMainX = entity.prevPosX + (entity.posX - entity.prevPosX ) * partialTicks;
		double smoothedMainY = entity.prevPosY + (entity.posY - entity.prevPosY ) * partialTicks;
		double smoothedMainZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ ) * partialTicks;

		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		PLUG_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	

		for (EntityDecayPitTargetPart part : entity.shield_array) {
			float floatate = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;
			double smoothedX = part.prevPosX  + (part.posX - part.prevPosX ) * partialTicks;
			double smoothedY = part.prevPosY  + (part.posY - part.prevPosY ) * partialTicks;
			double smoothedZ = part.prevPosZ  + (part.posZ - part.prevPosZ ) * partialTicks;
			if (part != entity.target && part != entity.bottom)
				renderCogShield(part, x + smoothedX - smoothedMainX, y + smoothedY - smoothedMainY, z + smoothedZ - smoothedMainZ, floatate);
		}
		
		bindTexture(TARGET_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 4.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		TARGET_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	
	}

	private void renderCogShield(EntityDecayPitTargetPart entity, double x, double y, double z, float angle) {
		GlStateManager.pushMatrix();
		bindTexture(COG_TEXTURE);
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		COG_MODEL.render(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitTarget entity) {
		return null;
	}
}