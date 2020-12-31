package thebetweenlands.client.render.model.entity;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.entity.RenderBoulderSprite;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.lib.ModInfo;

/**
 * BLBoulderSprite - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelBoulderSprite extends MowzieModelBase {
	public static class StalactitesModelRenderer extends MowzieModelRenderer {
		public static final ResourceLocation SPRITE_BOTTOM = new ResourceLocation(ModInfo.ID, "blocks/stalactite_bottom");
		public static final ResourceLocation SPRITE_MID = new ResourceLocation(ModInfo.ID, "blocks/stalactite_middle");

		private List<SpikeRenderer> stalactites;
		private ResourceLocation texture;
		private ResourceLocation prevTexture;

		public StalactitesModelRenderer(ModelBase modelBase) {
			super(modelBase);
		}

		public void setStalactites(@Nullable ResourceLocation texture, @Nullable List<SpikeRenderer> stalactites, @Nullable ResourceLocation prevTexture) {
			this.texture = texture;
			this.stalactites = stalactites;
			this.prevTexture = prevTexture;
		}

		@Override
		public void render(float scale) {
			this.renderStalactites();
		}

		@Override
		public void renderWithRotation(float scale) {
			this.renderStalactites();
		}

		private void renderStalactites() {
			if(this.stalactites != null && !this.stalactites.isEmpty()) {
				Minecraft mc = Minecraft.getMinecraft();
				TextureManager textureManager = mc.getTextureManager();

				if(this.texture != null) {
					textureManager.bindTexture(this.texture);
				}

				GlStateManager.pushMatrix();
				GlStateManager.scale(-1, -1, 1);

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();

				buffer.begin(GL11.GL_QUADS, this.stalactites.get(0).getFormat());

				for(SpikeRenderer spike : this.stalactites) {
					spike.upload(buffer);
				}

				tessellator.draw();

				GlStateManager.popMatrix();

				if(this.prevTexture != null) {
					textureManager.bindTexture(this.prevTexture);
				}
			}
		}
	}

	public MowzieModelRenderer block_main;
	public MowzieModelRenderer brow1;
	public MowzieModelRenderer nosebridge;
	public MowzieModelRenderer mosstache_left1;
	public MowzieModelRenderer mosstache_right1;
	public MowzieModelRenderer lebushybeard;
	public MowzieModelRenderer leg_left;
	public MowzieModelRenderer leg_right;
	public MowzieModelRenderer brow_left;
	public MowzieModelRenderer brow_right;
	public MowzieModelRenderer nosetip;
	public MowzieModelRenderer nosewings;
	public MowzieModelRenderer mosstache_left2;
	public MowzieModelRenderer mossbush_left1;
	public MowzieModelRenderer mosstache_right2;
	public MowzieModelRenderer mossbush_right1;
	public MowzieModelRenderer lebushybeard_sideleft;
	public MowzieModelRenderer lebushybeard_sideright;
	public MowzieModelRenderer actualbush;
	public MowzieModelRenderer extrabush;

	public StalactitesModelRenderer stalactites;

	public ModelBoulderSprite() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.mosstache_left1 = new MowzieModelRenderer(this, 0, 61);
		this.mosstache_left1.setRotationPoint(0.0F, 6.0F, -6.5F);
		this.mosstache_left1.addBox(0.0F, -3.0F, -3.0F, 4, 3, 3, 0.0F);
		this.setRotateAngle(mosstache_left1, 0.0F, 0.0F, 0.091106186954104F);
		this.mossbush_left1 = new MowzieModelRenderer(this, 36, 61);
		this.mossbush_left1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.mossbush_left1.addBox(-0.5F, -3.5F, -3.5F, 5, 4, 4, 0.0F);
		this.nosewings = new MowzieModelRenderer(this, 34, 50);
		this.nosewings.setRotationPoint(0.0F, -1.0F, 1.0F);
		this.nosewings.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 4, 0.0F);
		this.lebushybeard = new MowzieModelRenderer(this, 0, 81);
		this.lebushybeard.setRotationPoint(0.0F, 7.5F, -6.5F);
		this.lebushybeard.addBox(-8.5F, -4.0F, -3.0F, 17, 4, 4, 0.0F);
		this.setRotateAngle(lebushybeard, -0.091106186954104F, 0.0F, 0.0F);
		this.mossbush_right1 = new MowzieModelRenderer(this, 36, 71);
		this.mossbush_right1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.mossbush_right1.addBox(-4.5F, -3.5F, -3.5F, 5, 4, 4, 0.0F);
		this.leg_right = new MowzieModelRenderer(this, 84, 0);
		this.leg_right.setRotationPoint(-4.0F, 7.5F, 2.5F);
		this.leg_right.addBox(-3.0F, 0.0F, -6.0F, 6, 4, 6, 0.0F);
		this.setRotateAngle(leg_right, 0.045553093477052F, 0.0F, 0.0F);
		this.block_main = new MowzieModelRenderer(this, 0, 0);
		this.block_main.setRotationPoint(0.0F, 12.6F, 1.8F);
		this.block_main.addBox(-8.0F, -8.5F, -6.5F, 16, 16, 13, 0.0F);
		this.setRotateAngle(block_main, -0.045553093477052F, 0.0F, 0.0F);
		this.nosetip = new MowzieModelRenderer(this, 17, 50);
		this.nosetip.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.nosetip.addBox(-2.01F, 0.0F, 0.0F, 4, 3, 4, 0.0F);
		this.setRotateAngle(nosetip, 0.18203784098300857F, 0.0F, 0.0F);
		this.mosstache_right1 = new MowzieModelRenderer(this, 0, 71);
		this.mosstache_right1.setRotationPoint(0.0F, 6.0F, -6.5F);
		this.mosstache_right1.addBox(-4.0F, -3.0F, -3.0F, 4, 3, 3, 0.0F);
		this.setRotateAngle(mosstache_right1, 0.0F, 0.0F, -0.091106186954104F);
		this.leg_left = new MowzieModelRenderer(this, 59, 0);
		this.leg_left.setRotationPoint(4.0F, 7.5F, 2.5F);
		this.leg_left.addBox(-3.0F, 0.0F, -6.0F, 6, 4, 6, 0.0F);
		this.setRotateAngle(leg_left, 0.045553093477052F, 0.0F, 0.0F);
		this.brow_right = new MowzieModelRenderer(this, 31, 38);
		this.brow_right.setRotationPoint(-1.0F, 2.0F, -2.0F);
		this.brow_right.addBox(-8.0F, 0.0F, -1.0F, 8, 4, 7, 0.0F);
		this.setRotateAngle(brow_right, 0.0F, 0.0F, 0.045553093477052F);
		this.actualbush = new MowzieModelRenderer(this, 0, 91);
		this.actualbush.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.actualbush.addBox(-7.5F, 0.0F, 0.0F, 15, 4, 8, 0.0F);
		this.setRotateAngle(actualbush, 0.136659280431156F, 0.0F, 0.0F);
		this.lebushybeard_sideleft = new MowzieModelRenderer(this, 43, 81);
		this.lebushybeard_sideleft.setRotationPoint(8.5F, 0.0F, 1.0F);
		this.lebushybeard_sideleft.addBox(-3.0F, -4.0F, 0.0F, 3, 4, 5, 0.0F);
		this.setRotateAngle(lebushybeard_sideleft, 0.091106186954104F, 0.0F, 0.0F);
		this.extrabush = new MowzieModelRenderer(this, 47, 91);
		this.extrabush.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.extrabush.addBox(-2.5F, -2.5F, -0.4F, 5, 5, 1, 0.0F);
		this.setRotateAngle(extrabush, 0.136659280431156F, 0.0F, 0.0F);
		this.nosebridge = new MowzieModelRenderer(this, 0, 50);
		this.nosebridge.setRotationPoint(0.0F, 0.5F, -6.5F);
		this.nosebridge.addBox(-2.0F, -6.0F, -4.0F, 4, 6, 4, 0.0F);
		this.setRotateAngle(nosebridge, -0.091106186954104F, 0.0F, 0.0F);
		this.brow_left = new MowzieModelRenderer(this, 0, 38);
		this.brow_left.setRotationPoint(1.0F, 2.0F, -2.0F);
		this.brow_left.addBox(0.0F, 0.0F, -1.0F, 8, 4, 7, 0.0F);
		this.setRotateAngle(brow_left, 0.0F, 0.0F, -0.045553093477052F);
		this.mosstache_left2 = new MowzieModelRenderer(this, 15, 61);
		this.mosstache_left2.setRotationPoint(4.0F, 0.0F, 0.0F);
		this.mosstache_left2.addBox(0.0F, -3.0F, -2.99F, 5, 3, 5, 0.0F);
		this.setRotateAngle(mosstache_left2, 0.0F, 0.0F, -0.27314402793711257F);
		this.mosstache_right2 = new MowzieModelRenderer(this, 15, 71);
		this.mosstache_right2.setRotationPoint(-4.0F, 0.0F, 0.0F);
		this.mosstache_right2.addBox(-5.0F, -3.0F, -3.0F, 5, 3, 5, 0.0F);
		this.setRotateAngle(mosstache_right2, 0.0F, 0.0F, 0.27314402793711257F);
		this.lebushybeard_sideright = new MowzieModelRenderer(this, 60, 81);
		this.lebushybeard_sideright.setRotationPoint(-8.5F, 0.0F, 1.0F);
		this.lebushybeard_sideright.addBox(0.0F, -4.0F, 0.0F, 3, 4, 5, 0.0F);
		this.setRotateAngle(lebushybeard_sideright, 0.091106186954104F, 0.0F, 0.0F);
		this.brow1 = new MowzieModelRenderer(this, 0, 31);
		this.brow1.setRotationPoint(0.0F, -8.5F, -6.5F);
		this.brow1.addBox(-8.0F, 0.0F, -2.0F, 16, 4, 2, 0.0F);
		this.block_main.addChild(this.mosstache_left1);
		this.mosstache_left1.addChild(this.mossbush_left1);
		this.nosetip.addChild(this.nosewings);
		this.block_main.addChild(this.lebushybeard);
		this.mosstache_right1.addChild(this.mossbush_right1);
		this.block_main.addChild(this.leg_right);
		this.nosebridge.addChild(this.nosetip);
		this.block_main.addChild(this.mosstache_right1);
		this.block_main.addChild(this.leg_left);
		this.brow1.addChild(this.brow_right);
		this.lebushybeard.addChild(this.actualbush);
		this.lebushybeard.addChild(this.lebushybeard_sideleft);
		this.lebushybeard.addChild(this.extrabush);
		this.block_main.addChild(this.nosebridge);
		this.brow1.addChild(this.brow_left);
		this.mosstache_left1.addChild(this.mosstache_left2);
		this.mosstache_right1.addChild(this.mosstache_right2);
		this.lebushybeard.addChild(this.lebushybeard_sideright);
		this.block_main.addChild(this.brow1);

		this.stalactites = new StalactitesModelRenderer(this);
		this.block_main.addChild(stalactites);

		setInitPose();
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityBoulderSprite sprite = (EntityBoulderSprite) entity;

		sprite.initStalactiteModels();
		this.stalactites.setStalactites(TextureMap.LOCATION_BLOCKS_TEXTURE, sprite.stalactites, RenderBoulderSprite.TEXTURE);

		this.block_main.render(f5);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAngle,
			float partialTickTime) {
		this.setToInitPose();

		EntityBoulderSprite entity = (EntityBoulderSprite) entitylivingbaseIn;

		float globalSpeed = 2.0f;
		float globalDegree = 0.8F;
		float legDegree = 0.8f;

		walk(leg_left, 0.5F * globalSpeed, 0.8F * legDegree, false, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);
		walk(leg_right, 0.5F * globalSpeed, 0.8F * legDegree, true, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);

		bob(this.block_main, 0.5F * globalSpeed, 2, true, limbSwing, limbSwingAngle);
		swing(this.block_main, globalSpeed * 0.5f, globalDegree * 0.6f, false, 2.8f, 0.0f, limbSwing + 1.5F, limbSwingAngle);
		flap(this.block_main, globalSpeed * 0.5f, globalDegree * 0.3f, false, 2.8f, 0.0f, limbSwing - 1.25F, limbSwingAngle);
		walk(this.block_main, globalSpeed * 0.5f, globalDegree * 0.15f, false, 0.2f, 0.0f, limbSwing * 1.1F, limbSwingAngle);

		float rollAnimationWeight = entity.getRollAnimationWeight(partialTickTime);
		float bobWeight = rollAnimationWeight * (1.0F - entity.getRollAnimationInAirWeight(partialTickTime));
		float rollAnimation = 0.415F + entity.getRollAnimation(partialTickTime);

		this.block_main.offsetY = (-(float)Math.abs(Math.sin((rollAnimation + 0.6F) * Math.PI)) * 0.8F + 0.3F) * bobWeight;
		float rollRotX = (float)((rollAnimation + ((Math.sin((rollAnimation + 0.5F) * Math.PI * 2) + 1) * 0.05F + 0.5F) * 1.1F) * Math.PI * 2) % ((float)Math.PI * 2);
		this.block_main.rotateAngleX = rollRotX - 0.045553093477052F * (1 - bobWeight);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
