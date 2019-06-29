package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import thebetweenlands.client.render.block.VertexLighterFlatNoOffsets;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityBeamOrigin;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.RotationMatrix;
import thebetweenlands.util.Stencil;

public class RenderBeamOrigin extends TileEntitySpecialRenderer<TileEntityBeamOrigin> {
	private static final VertexLighterFlatNoOffsets FLAT_LIGHTER = new VertexLighterFlatNoOffsets(Minecraft.getMinecraft().getBlockColors());

	@Override
	public void render(TileEntityBeamOrigin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		LightingUtil.INSTANCE.setLighting(255);

		GlStateManager.disableLighting();

		RotationMatrix m = new RotationMatrix();

		m.setRotations(0, (System.currentTimeMillis() % 100000) / 5000.0F/*0.55f*/, 0);

		double h = 1.5D;
		double w = 0.8D;

		Vec3d v0 = new Vec3d(-w, 0, 0).add(w, 1, 0);
		Vec3d v1 = new Vec3d(0, h, w).add(w, 1, 0);
		Vec3d v2 = new Vec3d(0, h, -w).add(w, 1, 0);

		Vec3d mid = Vec3d.ZERO;

		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1, mid), m.transformVec(v2, mid), partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(-w*2, 0, 0), mid), m.transformVec(v2.add(0, 0, w*2), mid), partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(-w*2, 0, -w*2), mid), m.transformVec(v2.add(-w*2, 0, w*2), mid), partialTicks);
		this.renderMirrorTri(te, m.transformVec(v0, mid), m.transformVec(v1.add(0, 0, -w*2), mid), m.transformVec(v2.add(-w*2, 0, 0), mid), partialTicks);

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

	protected void renderMirrorTri(TileEntityBeamOrigin te, Vec3d v0, Vec3d v1, Vec3d v2, float partialTicks) {
		Vec3d normal = v1.subtract(v0).crossProduct(v2.subtract(v0)).normalize();

		Runnable mirrorRenderer = () -> {
			GlStateManager.glBegin(GL11.GL_TRIANGLES);
			GlStateManager.glVertex3f((float)v0.x, (float)v0.y, (float)v0.z);
			GlStateManager.glVertex3f((float)v1.x, (float)v1.y, (float)v1.z);
			GlStateManager.glVertex3f((float)v2.x, (float)v2.y, (float)v2.z);
			GlStateManager.glEnd();
		};

		this.renderMirror(te, v0, normal, mirrorRenderer, partialTicks);
	}

	protected void renderMirror(TileEntityBeamOrigin te, Vec3d planePos, Vec3d planeNormal, Runnable mirrorRenderer, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(0, 0, 0, 1);

		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();

		mirrorRenderer.run();

		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		try(Stencil stencil1 = Stencil.reserve(fbo); Stencil stencil2 = Stencil.reserve(fbo)) {
			if(stencil1.valid() && stencil2.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil1.clear(false);
				stencil2.clear(false);

				stencil1.func(GL11.GL_ALWAYS, true);
				stencil1.op(GL11.GL_REPLACE);

				GlStateManager.cullFace(CullFace.FRONT);

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

				stencil2.func(GL11.GL_ALWAYS, true);
				stencil2.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

				GlStateManager.colorMask(false, false, false, false);
				this.renderMirrorWorld(te, partialTicks);
				GlStateManager.colorMask(true, true, true, true);

				GlStateManager.popMatrix();

				stencil1.func(GL11.GL_EQUAL, true);
				stencil1.op(GL11.GL_KEEP);

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

				//TODO Debug
				//GL11.glDisable(GL11.GL_STENCIL_TEST);

				this.renderMirrorWorld(te, partialTicks);

				GlStateManager.popMatrix();

				GlStateManager.cullFace(CullFace.BACK);
			}

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

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

		GlStateManager.pushMatrix();
		//this.renderMirrorWorld(te, partialTicks);
		GlStateManager.popMatrix();
	}

	protected void renderMirrorWorld(TileEntityBeamOrigin te, float partialTicks) {
		Vec3d firePos = new Vec3d(0, 0.5D, 0);
		Vec3d brazierPos = new Vec3d(0, -1.5D, 0);

		IBlockState stateFire = Blocks.FIRE.getDefaultState();
		IBlockState stateBrazier = BlockRegistry.MUD_TOWER_BRAZIER.getDefaultState();

		IBakedModel modelFire = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateFire);
		IBakedModel modelBrazier = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateBrazier);

		GlStateManager.pushMatrix();

		for(EntityPlayer entity : te.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(te.getPos()).grow(20))) {
			Render<EntityPlayer> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			if(renderer != null) {
				GlStateManager.pushMatrix();

				double ex = (entity.posX - (te.getPos().getX() + 0.5D));
				double ey = (entity.posY - (te.getPos().getY() + 1.5D));
				double ez = (entity.posZ - (te.getPos().getZ() + 0.5D));

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

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));

		//TODO Create buffer once

		vertexBuffer.setTranslation(-0.5D + firePos.x - 4, -0.5D + firePos.y, -0.5D + firePos.z - 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + brazierPos.x - 4, -0.5D + brazierPos.y, -0.5D + brazierPos.z - 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + firePos.x - 4, -0.5D + firePos.y, -0.5D + firePos.z + 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + brazierPos.x - 4, -0.5D + brazierPos.y, -0.5D + brazierPos.z + 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + firePos.x + 4, -0.5D + firePos.y, -0.5D + firePos.z + 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + brazierPos.x + 4, -0.5D + brazierPos.y, -0.5D + brazierPos.z + 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + firePos.x+ 4, -0.5D + firePos.y, -0.5D + firePos.z - 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(-0.5D + brazierPos.x + 4, -0.5D + brazierPos.y, -0.5D + brazierPos.z - 4);
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		vertexBuffer.setTranslation(0, 0, 0);
		tessellator.draw();

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
