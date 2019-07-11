package thebetweenlands.client.render.sky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.sky.IBetweenlandsSky;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.FramebufferStack;
import thebetweenlands.util.Mesh;
import thebetweenlands.util.Mesh.Triangle;
import thebetweenlands.util.Mesh.Triangle.Vertex;
import thebetweenlands.util.Mesh.Triangle.Vertex.Vector3D;

@SideOnly(Side.CLIENT)
public class BLSkyRenderer extends IRenderHandler implements IBetweenlandsSky {
	public static final ResourceLocation SKY_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/sky_texture.png");
	public static final ResourceLocation SKY_SPOOPY_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/spoopy.png");
	public static final ResourceLocation FOG_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/fog_texture.png");

	protected List<AuroraRenderer> auroras = new ArrayList<AuroraRenderer>();

	private static int skyDomeDispList = -1;

	private static Mesh starMesh;

	public static GeometryBuffer clipPlaneBuffer;

	protected int ticks;
	protected boolean spoopy;

	private IRiftRenderer riftRenderer;

	private static RiftRenderer blRiftRenderer;

	public BLSkyRenderer() {
		if(clipPlaneBuffer == null) {
			clipPlaneBuffer = new GeometryBuffer(Minecraft.getMinecraft().getTextureManager(), WorldShader.CLIP_PLANE_DIFFUSE_TEXTURE, WorldShader.CLIP_PLANE_DEPTH_TEXTURE, true);
		}

		if(starMesh == null && ShaderHelper.INSTANCE.canUseShaders()) {
			starMesh = this.createStarMesh();
		}

		if(skyDomeDispList == -1) {
			skyDomeDispList = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(skyDomeDispList, GL11.GL_COMPILE);
			this.renderSkyDome();
			GlStateManager.glEndList();
		}

		if(blRiftRenderer == null) {
			blRiftRenderer = new RiftRenderer(skyDomeDispList);
		}

		this.setRiftRenderer(blRiftRenderer);
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		this.renderSky(partialTicks, world, mc);

		this.riftRenderer.render(partialTicks, world, mc);

		this.renderFog(partialTicks, world, mc);

		this.renderAuroras(partialTicks, world, mc);

		this.resetRenderingStates();
	}

	protected Mesh createStarMesh() {
		List<Triangle> triangles = new ArrayList<Triangle>();

		Random random = new Random(10842L);

		for (int i = 0; i < 1500; ++i) {
			double rx = (double)(random.nextFloat() * 2.0F - 1.0F);
			double ry = (double)(random.nextFloat() * 0.5F - 1.0F);
			double rz = (double)(random.nextFloat() * 2.0F - 1.0F);
			double centerDistance = rx * rx + ry * ry + rz * rz;

			if (centerDistance < 1.0D && centerDistance > 0.01D) {
				centerDistance = 1.0D / Math.sqrt(centerDistance);
				rx *= centerDistance;
				ry *= centerDistance;
				rz *= centerDistance;

				double farX = rx * 100.0D;
				double farY = ry * 100.0D;
				double farZ = rz * 100.0D;
				double xzAngle = Math.atan2(rx, rz);
				double xzRotX = Math.sin(xzAngle);
				double xzRotY = Math.cos(xzAngle);
				double distYAngle = Math.atan2(Math.sqrt(rx * rx + rz * rz), ry);
				double distYRotX = Math.sin(distYAngle);
				double distYRotZ = Math.cos(distYAngle);
				double randAngle = random.nextDouble() * Math.PI * 2.0D;
				double randRotX = Math.sin(randAngle);
				double randRotY = Math.cos(randAngle);

				int color = 0xFFFFFFFF;
				if(random.nextInt(2) == 1) {
					color = 0xFF009900;
				}

				double randSize = (double)(0.15F + random.nextFloat() * 0.1F);
				Vertex v1 = this.getQuadPoint(0, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v2 = this.getQuadPoint(1, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v3 = this.getQuadPoint(2, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v4 = this.getQuadPoint(3, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Triangle t1 = new Triangle(v1, v2, v3);
				Triangle t2 = new Triangle(v3, v4, v1);
				triangles.add(t1);
				triangles.add(t2);
			}
		}

		return new Mesh(triangles);
	}

	protected Vertex getQuadPoint(int vertex, double randSize, double randRotX, double randRotY, 
			double distYRotX, double distYRotZ, double xzRotX, double xzRotY, double farX, double farY, double farZ,
			int color) {
		double randRotYMultiplier = 0.0D;
		double vertX = (double)((vertex & 2) - 1) * randSize;
		double vertZ = (double)((vertex + 1 & 2) - 1) * randSize;
		double rotVertX = vertX * randRotY - vertZ * randRotX;
		double rotVertZ = vertZ * randRotY + vertX * randRotX;
		double rotVertX2 = rotVertX * distYRotX + randRotYMultiplier * distYRotZ;
		double rotVertZ2 = randRotYMultiplier * distYRotX - rotVertX * distYRotZ;
		vertX = rotVertZ2 * xzRotX - rotVertZ * xzRotY;
		vertZ = rotVertZ * xzRotX + rotVertZ2 * xzRotY;
		return new Vertex(farX + vertX, farY + rotVertX2, farZ + vertZ, new Vector3D(0, -1, 0), color);
	}

	protected void renderSky(float partialTicks, WorldClient world, Minecraft mc) {
		Vec3d skyColor = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
		float skyR = (float)skyColor.x;
		float skyG = (float)skyColor.y;
		float skyB = (float)skyColor.z;

		float anaglyphR = 0.0F;
		float anaglyphG = 0.0F;
		float anaglyphB = 0.0F;

		if (mc.gameSettings.anaglyph) {
			anaglyphR = (skyR * 30.0F + skyG * 59.0F + skyB * 11.0F) / 100.0F;
			anaglyphG = (skyR * 30.0F + skyG * 70.0F) / 100.0F;
			anaglyphB = (skyR * 30.0F + skyB * 70.0F) / 100.0F;
			skyR = anaglyphR;
			skyG = anaglyphG;
			skyB = anaglyphB;
		}

		float invRainStrength = 1.0F - world.getRainStrength(partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		boolean useShaderSky = ShaderHelper.INSTANCE.isWorldShaderActive() && ShaderHelper.INSTANCE.getWorldShader() != null && ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture() >= 0;

		float starBrightness = (world.getStarBrightness(partialTicks) + 0.5F) * invRainStrength * invRainStrength * invRainStrength;
		float fade = 1.0F;
		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(mc.world);
		if(provider != null) {
			fade = provider.getEnvironmentEventRegistry().denseFog.getFade(partialTicks) * 0.95F + 0.05F;
		}
		starBrightness *= fade;
		if (starBrightness > 0.0F && !useShaderSky && starMesh != null) {
			GL14.glBlendColor(0, 0, 0, (starBrightness - 0.22F) * 3.5F);
			GlStateManager.blendFunc(SourceFactor.CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
			GlStateManager.pushMatrix();
			starMesh.render();
			GlStateManager.popMatrix();
			GL14.glBlendColor(1, 1, 1, 1);
		}

		GlStateManager.popMatrix();

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();

		if (world.provider.isSkyColored()) {
			GlStateManager.color(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		} else {
			GlStateManager.color(skyR, skyG, skyB, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		}

		GlStateManager.enableTexture2D();

		if(useShaderSky) {
			//Render shader sky dome
			GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture());
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.depthMask(false);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
			GlStateManager.callList(skyDomeDispList);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();

			//Render sky clip plane
			this.renderFlatSky(partialTicks, world, mc, true, false);
		} else {
			if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
				//Render fancy non-shader sky dome
				mc.renderEngine.bindTexture(SKY_TEXTURE);
				GlStateManager.disableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.depthMask(false);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.callList(skyDomeDispList);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
			} else {
				//Render flat sky
				this.renderFlatSky(partialTicks, world, mc, false, false);
			}
		}
		
		if(this.spoopy) {
			if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
				mc.renderEngine.bindTexture(SKY_SPOOPY_TEXTURE);
				
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.5D, -0.5D, 1);
				GlStateManager.scale(2.0D, 2.0D, 0.0D);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				
				GlStateManager.disableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.depthMask(false);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.callList(skyDomeDispList);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			} else {
				GlStateManager.pushMatrix();
				
				mc.renderEngine.bindTexture(SKY_SPOOPY_TEXTURE);
				
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.5D, -0.5D, 1);
				GlStateManager.scale(2.0D, 2.0D, 0.0D);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				
				this.renderFlatSky(partialTicks, world, mc, false, true);
				
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				
				GlStateManager.popMatrix();
			}
		}
	}

	protected void renderFlatSky(float partialTicks, WorldClient world, Minecraft mc, boolean renderClipPlane, boolean spoopy) {
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.depthMask(false);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

		if(!renderClipPlane) {
			if(spoopy) {
				mc.renderEngine.bindTexture(SKY_SPOOPY_TEXTURE);
			} else {
				boolean shaderTexture = false;
				if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
					WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
					if(shader != null && shader.getStarfieldTexture() >= 0) {
						GlStateManager.bindTexture(shader.getStarfieldTexture());
						shaderTexture = true;
					}
				}
	
				if(!shaderTexture) {
					mc.renderEngine.bindTexture(SKY_TEXTURE);
				}
			}

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vertexBuffer.pos(-90.0D, -50.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			vertexBuffer.pos(-90.0D, -50.0D, 90.0D).tex(0.0D, 1.0D).endVertex();
			vertexBuffer.pos(90.0D, -50.0D, 90.0D).tex(1.0D, 1.0D).endVertex();
			vertexBuffer.pos(90.0D, -50.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			tessellator.draw();
		} else {
			//Render clip plane (for god rays)
			GlStateManager.depthMask(true);
			GlStateManager.disableTexture2D();
			GlStateManager.disableFog();
			GlStateManager.color(1, 1, 1, 1);

			try(FramebufferStack.State state = FramebufferStack.push()) {
				Framebuffer mcFbo = mc.getFramebuffer();
				clipPlaneBuffer.updateGeometryBuffer(mcFbo.framebufferWidth, mcFbo.framebufferHeight);
				clipPlaneBuffer.bind();
				clipPlaneBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);
	
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				vertexBuffer.pos(-9000.0D, -90.0D, -9000.0D).color(255, 255, 255, 255).endVertex();
				vertexBuffer.pos(-9000.0D, -90.0D, 9000.0D).color(255, 255, 255, 255).endVertex();
				vertexBuffer.pos(9000.0D, -90.0D, 9000.0D).color(255, 255, 255, 255).endVertex();
				vertexBuffer.pos(9000.0D, -90.0D, -9000.0D).color(255, 255, 255, 255).endVertex();
				tessellator.draw();
	
				clipPlaneBuffer.updateDepthBuffer();
			}

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();
		}

		GlStateManager.popMatrix();
	}

	protected void renderFog(float partialTicks, WorldClient world, Minecraft mc) {
		//Render sky dome with fog texture for fog noise illusion
		if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
			GlStateManager.pushMatrix();

			float renderRadius = 80.0F;

			GlStateManager.enableFog();
			GlStateManager.setFogStart(renderRadius / 2F);
			GlStateManager.setFogEnd(renderRadius * 2F);

			GlStateManager.scale(
					1.0F / 50.0F * renderRadius, 
					1.0F / 50.0F * renderRadius, 
					1.0F / 50.0F * renderRadius
					);

			GlStateManager.translate(0, 10, 0);

			mc.renderEngine.bindTexture(FOG_TEXTURE);

			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.depthMask(false);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			float renderTicks = this.ticks + partialTicks;

			float domeRotation = renderTicks * 0.1F;

			GlStateManager.scale(1F, 0.8F, 1F);

			GlStateManager.color(0, 0, 0, 0.25F);
			GlStateManager.callList(skyDomeDispList);

			GlStateManager.color(0, 0, 0, 0.15F);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(domeRotation, 0, 1, 0);
			GlStateManager.translate(0, Math.cos(renderTicks / 150.0F) * 6.0F + 4.0F, 0.0F);
			GlStateManager.callList(skyDomeDispList);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			GlStateManager.rotate(-domeRotation / 1.8F, 0, 1, 0);
			GlStateManager.translate(0, -Math.sin(renderTicks / 170.0F) * 6.0F + 4.0F, 0.0F);
			GlStateManager.callList(skyDomeDispList);
			GlStateManager.popMatrix();

			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();

			GlStateManager.popMatrix();

			GlStateManager.setFogStart(FogHandler.getCurrentFogStart());
			GlStateManager.setFogEnd(FogHandler.getCurrentFogEnd());
		}
	}

	protected void renderSkyDome() {
		double tileSize = 5.0D;
		Vec3d yOffset = new Vec3d(0, 2, 0);
		Vec3d cp = new Vec3d(0, -20, 0);
		double radius = 55.0D;
		int tiles = 12;
		GlStateManager.pushMatrix();
		GlStateManager.glBegin(GL11.GL_TRIANGLES);
		//Renders tiles and then normalizes their vertices to create a texture mapped dome
		for(int tx = -tiles; tx < tiles; tx++) {
			for(int tz = -tiles; tz < tiles; tz++) {
				/*
				 * 1-----4
				 * |     |
				 * 2-----3
				 */
				Vec3d tp1 = new Vec3d(tx * tileSize, 0, tz * tileSize);
				tp1 = cp.add(tp1.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3d tp2 = new Vec3d((tx) * tileSize, 0, (tz + 1) * tileSize);
				tp2 = cp.add(tp2.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3d tp3 = new Vec3d((tx + 1) * tileSize, 0, (tz + 1) * tileSize);
				tp3 = cp.add(tp3.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3d tp4 = new Vec3d((tx + 1) * tileSize, 0, (tz) * tileSize);
				tp4 = cp.add(tp4.subtract(cp).normalize().scale(radius)).add(yOffset);

				float u00 = (float)((tp1.x) / (radius * 2.0D) + 0.5D);
				float u10 = (float)((tp4.x) / (radius * 2.0D) + 0.5D);
				float u11 = (float)((tp3.x) / (radius * 2.0D) + 0.5D);
				float u01 = (float)((tp2.x) / (radius * 2.0D) + 0.5D);

				float v00 = (float)(1 - ((tp1.z) / (radius * 2.0D) + 0.5D));
				float v10 = (float)(1 - ((tp4.z) / (radius * 2.0D) + 0.5D));
				float v11 = (float)(1 - ((tp3.z) / (radius * 2.0D) + 0.5D));
				float v01 = (float)(1 - ((tp2.z) / (radius * 2.0D) + 0.5D));

				GlStateManager.glTexCoord2f(u00, v00);
				GlStateManager.glVertex3f((float)tp1.x, (float)tp1.y, (float)tp1.z);
				GlStateManager.glTexCoord2f(u11, v11);
				GlStateManager.glVertex3f((float)tp3.x, (float)tp3.y, (float)tp3.z);
				GlStateManager.glTexCoord2f(u01, v01);
				GlStateManager.glVertex3f((float)tp2.x, (float)tp2.y, (float)tp2.z);

				GlStateManager.glTexCoord2f(u11, v11);
				GlStateManager.glVertex3f((float)tp3.x, (float)tp3.y, (float)tp3.z);
				GlStateManager.glTexCoord2f(u00, v00);
				GlStateManager.glVertex3f((float)tp1.x, (float)tp1.y, (float)tp1.z);
				GlStateManager.glTexCoord2f(u10, v10);
				GlStateManager.glVertex3f((float)tp4.x, (float)tp4.y, (float)tp4.z);
			}
		}
		GlStateManager.glEnd();
		GlStateManager.popMatrix();
	}

	protected void resetRenderingStates() {
		//Value used while rendering the world, but is only set once before rendering the sky
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

		GlStateManager.setFogStart(FogHandler.getCurrentFogStart());
		GlStateManager.setFogEnd(FogHandler.getCurrentFogEnd());

		GlStateManager.disableBlend();

		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
	}

	protected void renderAuroras(float partialTicks, WorldClient world, Minecraft mc) {
		if(!this.auroras.isEmpty()) {
			GlStateManager.disableFog();
			GlStateManager.depthMask(false);
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.pushMatrix();
			GlStateManager.translate(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
			for(AuroraRenderer aurora : this.auroras) {
				aurora.render(partialTicks, 1);
			}
			GlStateManager.popMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.depthMask(true);
		}
	}

	public void update(WorldClient world, Minecraft mc) {
		this.ticks++;

		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
		if(storage != null) {
			BLEnvironmentEventRegistry reg = storage.getEnvironmentEventRegistry();

			this.spoopy = reg.spoopy.isActive();
			
			if(reg.auroras.isActive()) {
				Random rand = world.rand;
				double newAuroraPosX = mc.player.posX + rand.nextInt(160) - 80;
				double newAuroraPosZ = mc.player.posZ + rand.nextInt(160) - 80;
				double newAuroraPosY = 260;
				double minDist = 0.0D;

				for(AuroraRenderer aurora : this.auroras) {
					if(aurora.getDistance(mc.player.posX, aurora.getY(), mc.player.posZ) > 180) {
						aurora.setActive(false);
					}
					double dist = aurora.getDistance(newAuroraPosX, newAuroraPosY, newAuroraPosZ);
					if(dist < minDist || minDist == 0.0D) {
						minDist = dist;
					}
				}

				if(minDist > 150 || this.auroras.isEmpty()) {
					List<Vector4f> gradients = new ArrayList<Vector4f>();
					switch(reg.auroras.getAuroraType()) {
					default:
					case 0:
						gradients.add(new Vector4f(0, 1, 0, 0.01F));
						gradients.add(new Vector4f(0, 1, 0, 0.15F));
						gradients.add(new Vector4f(0, 1, 0.8F, 0.8F));
						gradients.add(new Vector4f(0, 0.7F, 1, 0.15F));
						gradients.add(new Vector4f(0, 0.4F, 1, 0.01F));
						break;
					case 1:
						gradients.add(new Vector4f(1, 0, 0, 0.05F));
						gradients.add(new Vector4f(1, 0, 0, 0.2F));
						gradients.add(new Vector4f(1, 0, 0.5F, 0.5F));
						gradients.add(new Vector4f(1, 0.2F, 0.5F, 0.8F));
						gradients.add(new Vector4f(1, 0, 0.5F, 0.5F));
						gradients.add(new Vector4f(0.8F, 0, 0.5F, 0.25F));
						break;
					case 2:
						gradients.add(new Vector4f(0, 1, 0, 0.05F));
						gradients.add(new Vector4f(0.5F, 1, 0, 0.15F));
						gradients.add(new Vector4f(1, 0.8F, 0, 0.7F));
						gradients.add(new Vector4f(0.5F, 0.4F, 0, 0.15F));
						gradients.add(new Vector4f(1, 0.2F, 0, 0.05F));
						break;
					}

					this.auroras.add(new AuroraRenderer(newAuroraPosX, newAuroraPosY + rand.nextInt(100), newAuroraPosZ, new Vector2d(rand.nextFloat()*2.0F-1.0F, rand.nextFloat()*2.0F-1.0F), rand.nextInt(40) + 15, gradients));
				}
			} else {
				for(AuroraRenderer aurora : this.auroras) {
					aurora.setActive(false);
				}
			}

			Iterator<AuroraRenderer> auroraIT = this.auroras.iterator();
			while(auroraIT.hasNext()) {
				AuroraRenderer aurora = auroraIT.next();
				if(aurora.isRemoved()) {
					auroraIT.remove();
				} else {
					aurora.update();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			WorldClient world = Minecraft.getMinecraft().world;
			BLSkyRenderer skyRenderer = WorldProviderBetweenlands.getBLSkyRenderer();
			if(world != null && skyRenderer != null) {
				skyRenderer.update(world, Minecraft.getMinecraft());
			}
		}
	}

	@Override
	public void setRiftRenderer(IRiftRenderer renderer) {
		this.riftRenderer = renderer;
	}

	@Override
	public IRiftRenderer getRiftRenderer() {
		return this.riftRenderer;
	}
}
