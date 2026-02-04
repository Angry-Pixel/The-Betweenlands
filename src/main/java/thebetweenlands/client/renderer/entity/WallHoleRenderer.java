package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.client.model.entity.WallHoleModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.wall.AbstractWallCreature;

import javax.annotation.Nullable;

//TODO finish
public abstract class WallHoleRenderer<T extends AbstractWallCreature, M extends MowzieModelBase<T>> extends WallFaceRenderer<T, M> {

	private static final ResourceLocation WALL_TEXTURE_OVERLAY = TheBetweenlands.prefix("textures/entity/wall_hole_overlay.png");
	private static final ResourceLocation WALL_TEXTURE = TheBetweenlands.prefix("textures/block/mud_bricks.png");

	private final ResourceLocation modelTexture;

	//private final TexturedWallHoleModel texturedHoleModel;
	private final WallHoleModel<?> modelNormal;
	private final M entityModel;

	private boolean renderWall = false;
	private int renderPass = 0;

	public WallHoleRenderer(EntityRendererProvider.Context context, M model, ResourceLocation modelTexture) {
		super(context, model, 0.0F);

		this.modelTexture = modelTexture;
		this.entityModel = model;
		//this.texturedHoleModel = new TexturedWallHoleModel();
		this.modelNormal = new WallHoleModel<>(context.bakeLayer(BLModelLayers.WALL_HOLE));
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//		try (Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget())) {
//			if (stencil.isValid()) {
//				GL11.glEnable(GL11.GL_STENCIL_TEST);
//
//				stencil.clear(false);
//
//				stencil.func(GL11.GL_ALWAYS, true);
//				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);
//
//				RenderSystem.depthMask(false);
//				RenderSystem.colorMask(false, false, false, false);
//
//				RenderSystem.disableBlend();
//
//				//Polygon offset required so that there's no z fighting with the window and background wall
//				RenderSystem.enablePolygonOffset();
//				RenderSystem.polygonOffset(-5.0F, -5.0F);
//
//				//Render window through which the hole will be visible
//				this.texturedHoleModel.frontPiece1.visible = false;
//				this.texturedHoleModel.window.visible = true;
//				this.texturedHoleModel.setWindowZOffsetPercent(-0.001F);
//				this.renderPass = 0;
//				super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//				this.texturedHoleModel.frontPiece1.visible = true;
//				this.texturedHoleModel.window.visible = false;
//
//				RenderSystem.disablePolygonOffset();
//
//				RenderSystem.enableBlend();
//
//				RenderSystem.depthMask(true);
//				RenderSystem.colorMask(true, true, true, true);
//
//				stencil.func(GL11.GL_EQUAL, true);
//				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
//			}
//
//			//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
//			RenderSystem.depthFunc(GL11.GL_GEQUAL);
//			RenderSystem.colorMask(false, false, false, false);
//
//			this.renderPass = 1;
//			super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//
//			RenderSystem.colorMask(true, true, true, true);
//			RenderSystem.depthFunc(GL11.GL_LEQUAL);
//
//			GL11.glDisable(GL11.GL_STENCIL_TEST);
//		}

		//Render visible pass
		this.renderPass = 2;

//		this.texturedHoleModel.window.visible = true;
//		this.texturedHoleModel.setWindowZOffsetPercent(this.getHoleDepthPercent(entity, partialTicks));
//
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//
//		this.texturedHoleModel.window.visible = false;
	}

	@Override
	protected void scale(T entity, PoseStack stack, float partialTick) {
		super.scale(entity, stack, partialTick);
		stack.translate(0.0D, 0.55D, 0.0D);
	}

	@Nullable
	protected abstract TextureAtlasSprite getWallSprite(T entity);

	protected abstract float getHoleDepthPercent(T entity, float partialTicks);

	protected abstract float getMainModelVisibilityPercent(T entity, float partialTicks);

	@Override
	protected void renderModel(T entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state) {
		packedLight = LevelRenderer.getLightColor(entity.level(), entity.blockPosition().relative(entity.getFacing()));
		//this.model = this.texturedHoleModel;
		this.renderWall = true;

		TextureAtlasSprite wallSprite = this.getWallSprite(entity);

		if (wallSprite != null) {
			stack.pushPose();
			stack.translate(wallSprite.getU0(), wallSprite.getV0(), 0);
			stack.scale(wallSprite.getU1() - wallSprite.getU0(), wallSprite.getV1() - wallSprite.getV0(), 1);
		}

		stack.translate(0.0D, 0.0D, 0.3D);
		this.renderEntityModel(entity, stack, buffer, packedLight, overlay, color, state, true);

		if (wallSprite != null) {
			stack.popPose();
		}

		this.model = this.entityModel;
		this.renderWall = false;

		if (this.renderPass == 2 && this.getMainModelVisibilityPercent(entity, state.partialTick()) > 0.001F) {
			stack.pushPose();
			stack.translate(0, -0.55D, 0.0D);
			this.applyDeathAndEasterEggRotations(entity, stack, state.partialTick());
			this.renderEntityModel(entity, stack, buffer, packedLight, overlay, color, state, false);
			stack.popPose();
		}
	}

	protected void renderEntityModel(T entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state, boolean overlays) {
		var type = this.getRenderType(entity, state.visible(), state.translucent(), state.glowing());
		if (type != null) {
			if (overlays) {
				this.modelNormal.renderToBuffer(stack, buffer.getBuffer(this.modelNormal.renderType(this.getTextureLocation(entity))), packedLight, overlay, color);

				//render window
				stack.pushPose();
				stack.translate(0, 0, 0.0001F + this.modelNormal.getWindowZOffsetPercent() * 0.61F);
				float brightness = 1.0F - this.modelNormal.getWindowZOffsetPercent();
				int windowColor = FastColor.ARGB32.colorFromFloat(1.0F, brightness, brightness, brightness);
				this.modelNormal.window.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), packedLight, overlay, windowColor);
				stack.popPose();

				this.modelNormal.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(WALL_TEXTURE_OVERLAY)), packedLight, overlay);

				int damage = Mth.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
				this.renderBreakingOverlay(this.modelNormal, damage, stack, packedLight, overlay);
			} else {
				this.model.renderToBuffer(stack, buffer.getBuffer(type), packedLight, overlay, color);
			}
		}
	}

	protected void applyDeathAndEasterEggRotations(T entity, PoseStack stack, float partialTick) {
		float yOffset = 1.1F;

		stack.translate(0, yOffset, 0);

		if (entity.deathTime > 0) {
			float deathPercent = ((float) entity.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
			deathPercent = Mth.sqrt(deathPercent);

			if (deathPercent > 1.0F) {
				deathPercent = 1.0F;
			}

			stack.mulPose(Axis.ZP.rotationDegrees(deathPercent * this.getFlipDegrees(entity) * this.getMainModelVisibilityPercent(entity, partialTick)));
		} else {
			if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
				stack.translate(0.0F, (entity.getBbHeight() + 0.1F) / entity.getScale(), 0.0F);
				stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}

		stack.translate(0, -yOffset, 0);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		if (this.renderWall) {
			if (this.getWallSprite(entity) != null) {
				return InventoryMenu.BLOCK_ATLAS;
			} else {
				return WALL_TEXTURE;
			}
		} else {
			return this.modelTexture;
		}
	}
}
