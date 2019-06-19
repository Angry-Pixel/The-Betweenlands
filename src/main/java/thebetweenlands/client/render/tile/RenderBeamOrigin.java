package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import thebetweenlands.client.render.block.VertexLighterFlatNoOffsets;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityBeamOrigin;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.RotationMatrix;

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
		GlStateManager.disableTexture2D();

		GlStateManager.color(0, 0, 0, 1);

		RotationMatrix m = new RotationMatrix();
		
		m.setRotations(0, (System.currentTimeMillis() % 100000) / 2000.0F, 0);
		
		Vec3d v0 = new Vec3d(-4, 0, 0);
		Vec3d v1 = new Vec3d(0, 2, 2);
		Vec3d v2 = new Vec3d(0, 2, -2);
		
		Vec3d mid = v0.add(v1).add(v2).scale(1 / 3);
		
		v0 = m.transformVec(v0, mid);
		v1 = m.transformVec(v1, mid);
		v2 = m.transformVec(v2, mid);

		Vec3d normal = v0.subtract(v1).crossProduct(v0.subtract(v2)).normalize();

		v0 = v0.add(normal.scale(2));
		v1 = v1.add(normal.scale(2));
		v2 = v2.add(normal.scale(2));
		
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();
		GlStateManager.glBegin(GL11.GL_TRIANGLES);
		GlStateManager.glVertex3f((float)v0.x, (float)v0.y, (float)v0.z);
		GlStateManager.glVertex3f((float)v1.x, (float)v1.y, (float)v1.z);
		GlStateManager.glVertex3f((float)v2.x, (float)v2.y, (float)v2.z);
		GlStateManager.glEnd();
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
		
		boolean useStencil = false;
		int stencilBit = MinecraftForgeClient.reserveStencilBit();
		int stencilMask = 1 << stencilBit;

		if(stencilBit >= 0) {
			useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
		}
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		Vec3d firePos = new Vec3d(0.5D, 0.5D, -0.5D);
		Vec3d brazierPos = new Vec3d(0.5D, -1.5D, -0.5D);
		
		IBlockState stateFire = Blocks.FIRE.getDefaultState();
		IBlockState stateBrazier = BlockRegistry.MUD_TOWER_BRAZIER.getDefaultState();
		
		IBakedModel modelFire = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateFire);
		IBakedModel modelBrazier = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(stateBrazier);
		
		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);

			//Clear our stencil bit to 0
			GL11.glStencilMask(stencilMask);
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilMask(~0);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

			GlStateManager.cullFace(CullFace.FRONT);
			GlStateManager.depthMask(false);
			GlStateManager.glBegin(GL11.GL_TRIANGLES);
			GlStateManager.glVertex3f((float)v0.x, (float)v0.y, (float)v0.z);
			GlStateManager.glVertex3f((float)v1.x, (float)v1.y, (float)v1.z);
			GlStateManager.glVertex3f((float)v2.x, (float)v2.y, (float)v2.z);
			GlStateManager.glEnd();
			GlStateManager.depthMask(true);
			GlStateManager.cullFace(CullFace.BACK);

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			
			GlStateManager.enableTexture2D();

			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();

			if (Minecraft.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
			} else {
				GlStateManager.shadeModel(GL11.GL_FLAT);
			}

			GlStateManager.pushMatrix();

			//GlStateManager.depthFunc(GL11.GL_GREATER);
			
			GlStateManager.cullFace(CullFace.FRONT);
			
			reflectTransform(v0, normal, firePos);

			GlStateManager.translate(-0.5D, -0.5D, -0.5D);

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));
			ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

			tessellator.draw();
			
			//GlStateManager.depthFunc(GL11.GL_LEQUAL);
			
			GlStateManager.cullFace(CullFace.BACK);
			
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();

			//GlStateManager.depthFunc(GL11.GL_GREATER);
			
			GlStateManager.cullFace(CullFace.FRONT);
			
			reflectTransform(v0, normal, brazierPos);

			GlStateManager.translate(-0.5D, -0.5D, -0.5D);

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));
			ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

			tessellator.draw();
			
			//GlStateManager.depthFunc(GL11.GL_LEQUAL);
			
			GlStateManager.cullFace(CullFace.BACK);
			
			GlStateManager.popMatrix();
		}

		if(stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}
		if(useStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
		
		GlStateManager.enableTexture2D();
		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(firePos.x - 0.5D, firePos.y - 0.5D, firePos.z - 0.5D);

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelFire, stateFire, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		tessellator.draw();
		
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(brazierPos.x - 0.5D, brazierPos.y - 0.5D, brazierPos.z - 0.5D);

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));
		ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), modelBrazier, stateBrazier, te.getPos(), vertexBuffer, false, MathHelper.getPositionRandom(te.getPos()));

		tessellator.draw();

		GlStateManager.popMatrix();

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
	
	public static void reflectTransform(Vec3d planePos, Vec3d planeNormal, Vec3d objectPos) {
		float yaw = -(float)Math.toDegrees(Math.atan2(planeNormal.z, planeNormal.x));
		float pitch = (float)Math.toDegrees(Math.atan2(Math.sqrt(planeNormal.x*planeNormal.x + planeNormal.z*planeNormal.z), -planeNormal.y)) + 180;

		double d = planePos.subtract(objectPos).dotProduct(planeNormal) / planeNormal.dotProduct(planeNormal);

		GlStateManager.translate(objectPos.x , objectPos.y, objectPos.z);

		GlStateManager.rotate(yaw, 0, 1, 0);
		GlStateManager.rotate(pitch, 0, 0, 1);
		
		GlStateManager.scale(1, -1, 1);

		GlStateManager.translate(0, -d * 2, 0);

		GlStateManager.rotate(-pitch, 0, 0, 1);
		GlStateManager.rotate(-yaw, 0, 1, 0);
	}
}
