package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.block.IsolatedBlockModelRenderer;
import thebetweenlands.client.render.block.VertexBatchRenderer;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityBeamOrigin;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.RotationMatrix;
import thebetweenlands.util.Stencil;

public class RenderBeamOrigin extends TileEntitySpecialRenderer<TileEntityBeamOrigin> {
	private static final IsolatedBlockModelRenderer BLOCK_RENDERER = new IsolatedBlockModelRenderer();

	private VertexBatchRenderer blocksRenderer;

	@Override
	public void render(TileEntityBeamOrigin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5F, y - 1.5F, z + 0.5F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		LightingUtil.INSTANCE.setLighting(255);

		GlStateManager.disableLighting();

		float visibility = te.prevVisibility + (te.visibility - te.prevVisibility) * partialTicks;
		
		RotationMatrix m = new RotationMatrix();
		m.setRotations(0, te.prevRotation + (te.rotation - te.prevRotation) * partialTicks, 0);

		double h = 1.65D;
		double w = 0.45D;

		Vec3d v0 = new Vec3d(-w, 0, 0).add(w, 1, 0);
		Vec3d v1 = new Vec3d(0, h, w).add(w, 1, 0);
		Vec3d v2 = new Vec3d(0, h, -w).add(w, 1, 0);

		Vec3d mid = Vec3d.ZERO;

		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1, mid), m.transformVec(v2, mid), visibility, partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(-w*2, 0, 0), mid), m.transformVec(v2.add(0, 0, w*2), mid), visibility, partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(-w*2, 0, -w*2), mid), m.transformVec(v2.add(-w*2, 0, w*2), mid), visibility, partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(0, 0, -w*2), mid), m.transformVec(v2.add(-w*2, 0, 0), mid), visibility, partialTicks);

		Vec3d p0 = m.transformVec(v1, mid);
		Vec3d p1 = m.transformVec(v1.add(-w*2, 0, 0), mid);
		Vec3d p2 = m.transformVec(v1.add(-w*2, 0, -w*2), mid);
		Vec3d p3 = m.transformVec(v1.add(0, 0, -w*2), mid);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		GlStateManager.disableTexture2D();
		GlStateManager.color(visibility, visibility, visibility, 0.75f + 0.25f * (1 - visibility));

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		bufferBuilder.pos(p3.x, p3.y, p3.z).endVertex();
		bufferBuilder.pos(p2.x, p2.y, p2.z).endVertex();
		bufferBuilder.pos(p1.x, p1.y, p1.z).endVertex();
		bufferBuilder.pos(p0.x, p0.y, p0.z).endVertex();

		tessellator.draw();
		
		GlStateManager.color(0, 0, 0, 0.9f * (1 - visibility));

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		bufferBuilder.pos(p3.x, p3.y, p3.z).endVertex();
		bufferBuilder.pos(p2.x, p2.y, p2.z).endVertex();
		bufferBuilder.pos(p1.x, p1.y, p1.z).endVertex();
		bufferBuilder.pos(p0.x, p0.y, p0.z).endVertex();

		tessellator.draw();

		this.bindTexture(new ResourceLocation(ModInfo.ID, "textures/tiles/prism_mirror.png"));

		GlStateManager.enableTexture2D();
		GlStateManager.color(visibility, visibility, visibility, visibility);

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		bufferBuilder.pos(p3.x, p3.y, p3.z).tex(0.25, 0.25).endVertex();
		bufferBuilder.pos(p2.x, p2.y, p2.z).tex(0.25, 0.75).endVertex();
		bufferBuilder.pos(p1.x, p1.y, p1.z).tex(0.75, 0.75).endVertex();
		bufferBuilder.pos(p0.x, p0.y, p0.z).tex(0.25, 0.75).endVertex();

		tessellator.draw();

		RenderHelper.enableStandardItemLighting();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.enableLighting();

		LightingUtil.INSTANCE.revert();

		GlStateManager.disableRescaleNormal();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityBeamOrigin te) {
		return true;
	}

	protected void renderMirrorTri(TileEntityBeamOrigin te, Vec3d v0, Vec3d v1, Vec3d v2, float visibility, float partialTicks) {
		Vec3d normal = v1.subtract(v0).crossProduct(v2.subtract(v0)).normalize();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		Runnable mirrorRenderer = () -> {
			bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

			bufferBuilder.pos(v0.x, v0.y, v0.z).endVertex();
			bufferBuilder.pos(v1.x, v1.y, v1.z).endVertex();
			bufferBuilder.pos(v2.x, v2.y, v2.z).endVertex();

			tessellator.draw();
		};

		GlStateManager.enableBlend();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.disableTexture2D();

		GlStateManager.color(visibility * 0.65f, visibility * 0.65f, visibility * 0.65f, 0.75f + 0.25f * (1 - visibility));

		bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

		bufferBuilder.pos(v2.x, v2.y, v2.z).endVertex();
		bufferBuilder.pos(v1.x, v1.y, v1.z).endVertex();
		bufferBuilder.pos(v0.x, v0.y, v0.z).endVertex();

		tessellator.draw();

		this.renderMirror(te, v0, normal, mirrorRenderer, partialTicks);

		GlStateManager.enableBlend();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		GlStateManager.disableTexture2D();

		GlStateManager.color(visibility * visibility, visibility * visibility, visibility * visibility, 0.25f);

		bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

		bufferBuilder.pos(v2.x, v2.y, v2.z).endVertex();
		bufferBuilder.pos(v1.x, v1.y, v1.z).endVertex();
		bufferBuilder.pos(v0.x, v0.y, v0.z).endVertex();

		tessellator.draw();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.color(0, 0, 0, 0.5f * (1 - visibility));

		bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

		bufferBuilder.pos(v2.x, v2.y, v2.z).endVertex();
		bufferBuilder.pos(v1.x, v1.y, v1.z).endVertex();
		bufferBuilder.pos(v0.x, v0.y, v0.z).endVertex();

		tessellator.draw();
		
		GlStateManager.enableTexture2D();
		GlStateManager.color(visibility, visibility, visibility, visibility);

		this.bindTexture(new ResourceLocation(ModInfo.ID, "textures/tiles/prism_mirror.png"));

		bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

		bufferBuilder.pos(v2.x, v2.y, v2.z).tex(0.25, 0).endVertex();
		bufferBuilder.pos(v1.x, v1.y, v1.z).tex(0.75, 0).endVertex();
		bufferBuilder.pos(v0.x, v0.y, v0.z).tex(0.5, 1).endVertex();

		tessellator.draw();
	}

	protected void renderMirror(TileEntityBeamOrigin te, Vec3d planePos, Vec3d planeNormal, Runnable mirrorRenderer, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		try(Stencil stencil1 = Stencil.reserve(fbo); Stencil stencil2 = Stencil.reserve(fbo)) {
			if(stencil1.valid() && stencil2.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil1.clear(false);
				stencil2.clear(false);

				stencil1.func(GL11.GL_ALWAYS, true);
				stencil1.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

				GlStateManager.cullFace(CullFace.FRONT);

				//Render mirror to stencil1
				{
					GlStateManager.disableTexture2D();
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

					mirrorRenderer.run();

					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
					GlStateManager.colorMask(true, true, true, true);
					GlStateManager.enableTexture2D();
				}

				GlStateManager.pushMatrix();
				mirrorTransform(planePos, planeNormal);

				GlStateManager.enableTexture2D();

				GlStateManager.depthFunc(GL11.GL_GEQUAL);

				//Only render if stencil1 bit is set and then only
				//set stencil bit 2 (when something is rendered on that pixel and z test passes)
				GL11.glStencilMask(stencil2.mask);
				GL11.glStencilFunc(GL11.GL_EQUAL, stencil1.mask | stencil2.mask, stencil1.mask);
				GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

				//Render mirror world to stencil bit 2 only where stencil bit 1 is set
				GlStateManager.colorMask(false, false, false, false);
				this.renderMirrorWorld(te, partialTicks);
				GlStateManager.colorMask(true, true, true, true);

				GlStateManager.popMatrix();

				stencil1.func(GL11.GL_EQUAL, true);
				stencil1.op(GL11.GL_KEEP);

				//"Reset" depth buffer on mirror by using glPolygonOffset
				GlStateManager.enablePolygonOffset();
				GlStateManager.doPolygonOffset(1, 100000000);
				{
					GlStateManager.disableTexture2D();
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

					mirrorRenderer.run();

					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
					GlStateManager.colorMask(true, true, true, true);
					GlStateManager.enableTexture2D();
				}
				GlStateManager.disablePolygonOffset();

				GlStateManager.pushMatrix();
				mirrorTransform(planePos, planeNormal);

				GlStateManager.depthFunc(GL11.GL_LEQUAL);

				GL11.glStencilMask(stencil1.mask | stencil2.mask);
				GL11.glStencilFunc(GL11.GL_EQUAL, stencil1.mask | stencil2.mask, stencil1.mask | stencil2.mask);
				GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

				//Disable stencil test to debug mirrored world
				//GL11.glDisable(GL11.GL_STENCIL_TEST);

				//Render mirror world only where stencil1 and stencil2 bits are set.
				//The previous preparations have the effect that the mirror world is only rendered
				//when *behind* the mirror surface and not in front
				this.renderMirrorWorld(te, partialTicks);

				GlStateManager.popMatrix();

				GlStateManager.cullFace(CullFace.BACK);
			}

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		//Render mirror to depth buffer
		{
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

			mirrorRenderer.run();

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
		}

		//Render mirror world in real world for debugging
		//GlStateManager.pushMatrix();
		//this.renderMirrorWorld(te, partialTicks);
		//GlStateManager.popMatrix();
	}

	protected void renderMirrorWorld(TileEntityBeamOrigin te, float partialTicks) {
		GlStateManager.pushMatrix();

		for(EntityPlayer entity : te.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(te.getPos()).grow(20))) {
			Render<EntityPlayer> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			if(renderer != null) {
				GlStateManager.pushMatrix();

				double ex = (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks - (te.getPos().getX() + 0.5D));
				double ey = (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks - (te.getPos().getY() - 1.5D));
				double ez = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks - (te.getPos().getZ() + 0.5D));

				renderer.doRender(entity, ex, ey, ez, entity.rotationYaw, partialTicks);
				GlStateManager.popMatrix();
			}
		}

		GlStateManager.popMatrix();

		GlStateManager.enableTexture2D();

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		boolean needsRebuild = this.blocksRenderer == null;

		if(needsRebuild) {
			if(this.blocksRenderer == null) {
				this.blocksRenderer = new VertexBatchRenderer(DefaultVertexFormats.BLOCK, OpenGlHelper.useVbo());
			}

			Vec3d firePos = new Vec3d(0, 1.0D, 0);
			Vec3d brazierPos = new Vec3d(0, -1.0D, 0);

			IBlockState stateFire = Blocks.FIRE.getDefaultState();
			IBlockState stateBrazier = BlockRegistry.MUD_TOWER_BRAZIER.getDefaultState();

			IBakedModel modelFire = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateFire);
			IBakedModel modelBrazier = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateBrazier);
			Tessellator tessellator = Tessellator.getInstance();

			BufferBuilder bufferBuilder = tessellator.getBuffer();

			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			BLOCK_RENDERER.setLighting((state, facing) -> 220);

			bufferBuilder.setTranslation(-0.5D + firePos.x - 3, -0.5D + firePos.y, -0.5D + firePos.z - 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelFire, stateFire, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + brazierPos.x - 3, -0.5D + brazierPos.y, -0.5D + brazierPos.z - 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelBrazier, stateBrazier, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + firePos.x - 3, -0.5D + firePos.y, -0.5D + firePos.z + 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelFire, stateFire, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + brazierPos.x - 3, -0.5D + brazierPos.y, -0.5D + brazierPos.z + 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelBrazier, stateBrazier, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + firePos.x + 3, -0.5D + firePos.y, -0.5D + firePos.z + 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelFire, stateFire, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + brazierPos.x + 3, -0.5D + brazierPos.y, -0.5D + brazierPos.z + 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelBrazier, stateBrazier, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + firePos.x + 3, -0.5D + firePos.y, -0.5D + firePos.z - 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelFire, stateFire, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(-0.5D + brazierPos.x + 3, -0.5D + brazierPos.y, -0.5D + brazierPos.z - 3);
			BLOCK_RENDERER.renderModel(te.getPos(), modelBrazier, stateBrazier, MathHelper.getPositionRandom(te.getPos()), bufferBuilder);

			bufferBuilder.setTranslation(0, 0, 0);

			this.blocksRenderer.compile(bufferBuilder);
		}

		this.blocksRenderer.render();

		GlStateManager.shadeModel(GL11.GL_FLAT);
	}

	public static void mirrorTransform(Vec3d planePos, Vec3d planeNormal) {
		GlStateManager.translate(planePos.x, planePos.y, planePos.z);

		scaleTransform(planeNormal, -1);

		GlStateManager.translate(-planePos.x, -planePos.y, -planePos.z);
	}

	public static void scaleTransform(Vec3d axis, float scale) {
		float yaw = -(float)Math.toDegrees(Math.atan2(axis.z, axis.x));
		float pitch = (float)Math.toDegrees(Math.atan2(Math.sqrt(axis.x*axis.x + axis.z*axis.z), -axis.y)) + 180;

		GlStateManager.rotate(yaw, 0, 1, 0);
		GlStateManager.rotate(pitch, 0, 0, 1);

		GlStateManager.scale(1, scale, 1);

		GlStateManager.rotate(-pitch, 0, 0, 1);
		GlStateManager.rotate(-yaw, 0, 1, 0);
	}
}
