package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitPlug;
import thebetweenlands.client.render.model.entity.ModelDecayPitTarget;
import thebetweenlands.client.render.model.entity.ModelDecayPitTargetShield;
import thebetweenlands.client.render.particle.entity.ParticleBeam;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityDecayPitTargetPart;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderDecayPitTarget extends Render<EntityDecayPitTarget> {
	private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/chain_beam.png");
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_plug.png");
	private static final ModelDecayPitPlug PLUG_MODEL = new ModelDecayPitPlug();
	
	public static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_target_shield.png");
	private static final ModelDecayPitTargetShield SHIELD_MODEL = new ModelDecayPitTargetShield();
	
	public static final ResourceLocation TARGET_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_target.png");
	private static final ModelDecayPitTarget TARGET_MODEL = new ModelDecayPitTarget();
	
	public RenderDecayPitTarget(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.renderBeams(entity, x, y, z, entityYaw, partialTicks, true);
		
		double smoothedMainX = entity.prevPosX + (entity.posX - entity.prevPosX ) * partialTicks;
		double smoothedMainY = entity.prevPosY + (entity.posY - entity.prevPosY ) * partialTicks;
		double smoothedMainZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ ) * partialTicks;

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		
		GlStateManager.disableCull();
		
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		PLUG_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	
		
		EntityDecayPitTargetPart hitPart = null;
		
		if(this.getRenderManager().isDebugBoundingBox()) {
			hitPart = entity.rayTraceShields(Minecraft.getMinecraft().player.getPositionEyes(partialTicks), Minecraft.getMinecraft().player.getLook(partialTicks));
		}
		
		for (EntityDecayPitTargetPart part : entity.parts) {
			float floatate = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;
			double smoothedX = part.prevPosX  + (part.posX - part.prevPosX ) * partialTicks;
			double smoothedY = part.prevPosY  + (part.posY - part.prevPosY ) * partialTicks;
			double smoothedZ = part.prevPosZ  + (part.posZ - part.prevPosZ ) * partialTicks;
			if (part != entity.target_north  && part != entity.target_east && part != entity.target_south && part != entity.target_west && part != entity.bottom) {
				if(hitPart == part) {
					GlStateManager.color(1, 0, 0, 1);
				}
				
				renderCogShield(part, x + smoothedX - smoothedMainX, y + smoothedY - smoothedMainY, z + smoothedZ - smoothedMainZ, floatate);
				
				GlStateManager.color(1, 1, 1, 1);
			}
		}
		
		bindTexture(TARGET_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 4.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		TARGET_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	
	}
	
	@Override
	public void renderMultipass(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.renderBeams(entity, x, y, z, entityYaw, partialTicks, false);
	}
	
	private void renderBeams(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks, boolean innerBeams) {
		if(entity.attackDamageTicks > 0) {
			float interpAttackDamageTicks = entity.attackDamageTicks - partialTicks;
			
			float beamAlpha = interpAttackDamageTicks > 38 ? 1 - (interpAttackDamageTicks - 38) / 2.0f : interpAttackDamageTicks / 38.0f;
			
			double yStart = entity.height - 1D;
			
			for(int i = 0; i < 4; i++) {
				double diffX2 = i == 0 ? 1.7D : i == 1 ? -1.7D : 0;
				double diffY2 = 0;
				double diffZ2 = i == 2 ? 1.7D : i == 3 ? -1.7D : 0;
				
				double diffX = i == 0 ? 12 : i == 1 ? -12 : 0;
				double diffY = 2.5D + entity.getProgress() * entity.MOVE_UNIT - yStart;
				double diffZ = i == 2 ? 12 : i == 3 ? -12 : 0;
				
				LightingUtil.INSTANCE.setLighting(255);
	
				GlStateManager.disableLighting();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
				GlStateManager.disableCull();
	
				this.bindTexture(BEAM_TEXTURE);
	
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + yStart, z);
	
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
	
				GlStateManager.depthMask(false);
				
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	
				if(innerBeams) {
					ParticleBeam.buildBeam(diffX2, diffY2, diffZ2, new Vec3d(-diffX2, -diffY2, -diffZ2), 0.4F, 0, 4F,
							ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
							(vx, vy, vz, u, v) -> {
								buffer.pos(vx, vy, vz).tex(u + (entity.ticksExisted + partialTicks) * 0.15F, v).color(255, 255, 255, (int)(beamAlpha * 255)).endVertex();
							});
				} else {
					ParticleBeam.buildBeam(diffX, diffY, diffZ, new Vec3d(-(diffX - diffX2), -(diffY - diffY2), -(diffZ - diffZ2)), 0.4F, 0, 4F,
							ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
							(vx, vy, vz, u, v) -> {
								buffer.pos(vx, vy, vz).tex(u + (entity.ticksExisted + partialTicks) * 0.15F, v).color(255, 255, 255, (int)(beamAlpha * 255)).endVertex();
							});
				}
				
				tessellator.draw();
				
				GlStateManager.alphaFunc(GL11.GL_GREATER, Math.max(0, 0.5f + (beamAlpha - 1) * 0.5f));
				
				GlStateManager.depthMask(true);
				GlStateManager.colorMask(false, false, false, false);
				
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	
				if(innerBeams) {
					ParticleBeam.buildBeam(diffX2, diffY2, diffZ2, new Vec3d(-diffX2, -diffY2, -diffZ2), 0.4F, 0, 4F,
							ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
							(vx, vy, vz, u, v) -> {
								buffer.pos(vx, vy, vz).tex(u + (entity.ticksExisted + partialTicks) * 0.15F, v).color(255, 255, 255, (int)(beamAlpha * 255)).endVertex();
							});
				} else {
					ParticleBeam.buildBeam(diffX, diffY, diffZ, new Vec3d(-(diffX - diffX2), -(diffY - diffY2), -(diffZ - diffZ2)), 0.4F, 0, 4F,
							ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
							(vx, vy, vz, u, v) -> {
								buffer.pos(vx, vy, vz).tex(u + (entity.ticksExisted + partialTicks) * 0.15F, v).color(255, 255, 255, (int)(beamAlpha * 255)).endVertex();
							});
				}
	
				tessellator.draw();
				
				GlStateManager.colorMask(true, true, true, true);
	
				GlStateManager.popMatrix();
	
				GlStateManager.enableCull();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
				GlStateManager.enableLighting();
				
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1, 1, 1, 1);
	
				LightingUtil.INSTANCE.revert();
			}
		}
	}

	private void renderCogShield(EntityDecayPitTargetPart entity, double x, double y, double z, float angle) {
		GlStateManager.pushMatrix();
		bindTexture(SHIELD_TEXTURE);
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		SHIELD_MODEL.render(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitTarget entity) {
		return null;
	}
	
	@Override
	public boolean isMultipass() {
		return true;
	}
}