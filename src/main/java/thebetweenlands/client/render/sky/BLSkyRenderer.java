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
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EventAuroras;
import thebetweenlands.common.world.event.EventSpoopy;
import thebetweenlands.util.GLUProjection;
import thebetweenlands.util.Mesh;
import thebetweenlands.util.Mesh.Triangle;
import thebetweenlands.util.Mesh.Triangle.Vertex;
import thebetweenlands.util.Mesh.Triangle.Vertex.Vector3D;

public class BLSkyRenderer extends IRenderHandler {
	//private int starDispList;
	private Mesh starMesh;
	private int skyDispListStart;
	private int skyDispList1;
	private int skyDispList2;
	private static final ResourceLocation SKY_TEXTURE_RES = new ResourceLocation("thebetweenlands:textures/sky/sky_texture.png");
	private static final ResourceLocation FOG_TEXTURE_RES = new ResourceLocation("thebetweenlands:textures/sky/fog_texture.png");
	private static final ResourceLocation SKY_SPOOPY_TEXTURE_RES = new ResourceLocation("thebetweenlands:textures/sky/spoopy.png");
	private List<AuroraRenderer> auroras = new ArrayList<AuroraRenderer>();
	public final GeometryBuffer clipPlaneBuffer = new GeometryBuffer(true);
	public static final BLSkyRenderer INSTANCE = new BLSkyRenderer();

	public BLSkyRenderer() {
		//this.starDispList = GLAllocation.generateDisplayLists(3);

		//Render stars to display list
		/*GL11.glPushMatrix();
		GL11.glNewList(this.starDispList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();*/
		this.starMesh = this.createStarMesh();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		byte tileCount = 64;
		int tileSize = 256 / tileCount + 2;
		float skyY = 16.0F;

		this.skyDispListStart = GLAllocation.generateDisplayLists(3);

		//Render sky dome
		GL11.glNewList(this.skyDispListStart, GL11.GL_COMPILE);
		this.createSkyDispList();
		GL11.glEndList();

		//Render sky 1 to display list
		skyY = -50.0F;
		this.skyDispList1 = this.skyDispListStart + 1;
		GL11.glNewList(this.skyDispList1, GL11.GL_COMPILE);
		//tessellator.startDrawingQuads();
		vertexBuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
		for (int tileX = -tileCount * tileSize; tileX <= tileCount * tileSize; tileX += tileCount) {
			for (int tileZ = -tileCount * tileSize; tileZ <= tileCount * tileSize; tileZ += tileCount) {
				/*tessellator.addVertex((double)(tileX + 0), (double)skyY, (double)(tileZ + 0));
				tessellator.addVertex((double)(tileX + tileCount), (double)skyY, (double)(tileZ + 0));
				tessellator.addVertex((double)(tileX + tileCount), (double)skyY, (double)(tileZ + tileCount));
				tessellator.addVertex((double)(tileX + 0), (double)skyY, (double)(tileZ + tileCount));*/
				vertexBuffer.pos((double)(tileX + 0), (double)skyY, (double)(tileZ + 0)).endVertex();
				vertexBuffer.pos((double)(tileX + tileCount), (double)skyY, (double)(tileZ + 0)).endVertex();
				vertexBuffer.pos((double)(tileX + tileCount), (double)skyY, (double)(tileZ + tileCount)).endVertex();
				vertexBuffer.pos((double)(tileX + 0), (double)skyY, (double)(tileZ + tileCount)).endVertex();
			}
		}
		tessellator.draw();
		GL11.glEndList();

		//Render sky 2 to display list
		skyY = -16.0F;
		this.skyDispList2 = this.skyDispListStart + 2;
		GL11.glNewList(this.skyDispList2, GL11.GL_COMPILE);
		//tessellator.startDrawingQuads();
		vertexBuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
		for (int tileX = -tileCount * tileSize; tileX <= tileCount * tileSize; tileX += tileCount) {
			for (int tileZ = -tileCount * tileSize; tileZ <= tileCount * tileSize; tileZ += tileCount) {
				/*tessellator.addVertex((double)(tileX + tileCount), (double)skyY, (double)(tileZ + 0));
				tessellator.addVertex((double)(tileX + 0), (double)skyY, (double)(tileZ + 0));
				tessellator.addVertex((double)(tileX + 0), (double)skyY, (double)(tileZ + tileCount));
				tessellator.addVertex((double)(tileX + tileCount), (double)skyY, (double)(tileZ + tileCount));*/
				vertexBuffer.pos((double)(tileX + tileCount), (double)skyY, (double)(tileZ + 0)).endVertex();
				vertexBuffer.pos((double)(tileX + 0), (double)skyY, (double)(tileZ + 0)).endVertex();
				vertexBuffer.pos((double)(tileX + 0), (double)skyY, (double)(tileZ + tileCount)).endVertex();
				vertexBuffer.pos((double)(tileX + tileCount), (double)skyY, (double)(tileZ + tileCount)).endVertex();
			}
		}
		tessellator.draw();
		GL11.glEndList();
	}

	private Mesh createStarMesh() {
		List<Triangle> triangles = new ArrayList<Triangle>();

		Random random = new Random(10842L);

		//tessellator.startDrawingQuads();
		//GL11.glBegin(GL11.GL_QUADS);
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
					//tessellator.setColorOpaque(0, 96, 0);
					//GL11.glColor3f(0, 96 / 255.0F, 0);
					color = 0xFF009900;
				}

				double randSize = (double)(0.15F + random.nextFloat() * 0.1F);
				/*for (int j = 0; j < 4; ++j) {
					double randRotYMultiplier = 0.0D;
					double vertX = (double)((j & 2) - 1) * randSize;
					double vertZ = (double)((j + 1 & 2) - 1) * randSize;
					double rotVertX = vertX * randRotY - vertZ * randRotX;
					double rotVertZ = vertZ * randRotY + vertX * randRotX;
					double rotVertX2 = rotVertX * distYRotX + randRotYMultiplier * distYRotZ;
					double rotVertZ2 = randRotYMultiplier * distYRotX - rotVertX * distYRotZ;
					vertX = rotVertZ2 * xzRotX - rotVertZ * xzRotY;
					vertZ = rotVertZ * xzRotX + rotVertZ2 * xzRotY;
					//tessellator.addVertex(farX + vertX, farY + rotVertX2, farZ + vertZ);
					GL11.glVertex3d(farX + vertX, farY + rotVertX2, farZ + vertZ);
				}*/
				Vertex v1 = this.getQuadPoint(0, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v2 = this.getQuadPoint(1, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v3 = this.getQuadPoint(2, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				//Vertex v3 = this.getQuadPoint(2, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v4 = this.getQuadPoint(3, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				//Vertex v1 = this.getQuadPoint(0, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Triangle t1 = new Triangle(v1, v2, v3);
				Triangle t2 = new Triangle(v3, v4, v1);
				triangles.add(t1);
				triangles.add(t2);
			}
		}
		//GL11.glEnd();
		//tessellator.draw();

		return new Mesh(triangles);
	}

	private Vertex getQuadPoint(int vertex, double randSize, double randRotX, double randRotY, 
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

	private void createSkyDispList() {
		double tileSize = 5.0D;
		GLUProjection.Vector3D yOffset = new GLUProjection.Vector3D(0, 2, 0);
		GLUProjection.Vector3D cp = new GLUProjection.Vector3D(0, -20, 0);
		double radius = 50.0D;
		int tiles = 30;
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_TRIANGLES);
		//Renders tiles and then normalizes their vertices to create a texture mapped dome
		for(int tx = -tiles; tx < tiles; tx++) {
			for(int tz = -tiles; tz < tiles; tz++) {
				/*
				 * 1-----4
				 * |     |
				 * 2-----3
				 */
				GLUProjection.Vector3D tp1 = new GLUProjection.Vector3D(tx * tileSize, 0, tz * tileSize);
				tp1 = cp.add(tp1.sub(cp).normalized().mul(radius)).add(yOffset);

				GLUProjection.Vector3D tp2 = new GLUProjection.Vector3D((tx) * tileSize, 0, (tz + 1) * tileSize);
				tp2 = cp.add(tp2.sub(cp).normalized().mul(radius)).add(yOffset);

				GLUProjection.Vector3D tp3 = new GLUProjection.Vector3D((tx + 1) * tileSize, 0, (tz + 1) * tileSize);
				tp3 = cp.add(tp3.sub(cp).normalized().mul(radius)).add(yOffset);

				GLUProjection.Vector3D tp4 = new GLUProjection.Vector3D((tx + 1) * tileSize, 0, (tz) * tileSize);
				tp4 = cp.add(tp4.sub(cp).normalized().mul(radius)).add(yOffset);

				double u00 = (tp1.x) / (radius * 2.0D) + 0.5D;
				double u10 = (tp4.x) / (radius * 2.0D) + 0.5D;
				double u11 = (tp3.x) / (radius * 2.0D) + 0.5D;
				double u01 = (tp2.x) / (radius * 2.0D) + 0.5D;

				double v00 = 1 - ((tp1.z) / (radius * 2.0D) + 0.5D);
				double v10 = 1 - ((tp4.z) / (radius * 2.0D) + 0.5D);
				double v11 = 1 - ((tp3.z) / (radius * 2.0D) + 0.5D);
				double v01 = 1 - ((tp2.z) / (radius * 2.0D) + 0.5D);

				GL11.glTexCoord2d(u00, v00);
				GL11.glVertex3d(tp1.x, tp1.y, tp1.z);
				GL11.glTexCoord2d(u11, v11);
				GL11.glVertex3d(tp3.x, tp3.y, tp3.z);
				GL11.glTexCoord2d(u01, v01);
				GL11.glVertex3d(tp2.x, tp2.y, tp2.z);

				GL11.glTexCoord2d(u11, v11);
				GL11.glVertex3d(tp3.x, tp3.y, tp3.z);
				GL11.glTexCoord2d(u00, v00);
				GL11.glVertex3d(tp1.x, tp1.y, tp1.z);
				GL11.glTexCoord2d(u10, v10);
				GL11.glVertex3d(tp4.x, tp4.y, tp4.z);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	private void renderSkyTexture(Minecraft mc, boolean renderClipPlane) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderHelper.disableStandardItemLighting();
		GL11.glDepthMask(false);
		mc.renderEngine.bindTexture(SKY_TEXTURE_RES);

		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null && shader.getStarfieldTexture() >= 0) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, shader.getStarfieldTexture());
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		if(!renderClipPlane) {
			/*tessellator.startDrawingQuads();
			vertexBuffer.pos(-90.0D, -40.0D, -90.0D, 0.0D, 0.0D);
			vertexBuffer.pos(-90.0D, -40.0D, 90.0D, 0.0D, 1.0D);
			vertexBuffer.pos(90.0D, -40.0D, 90.0D, 1.0D, 1.0D);
			vertexBuffer.pos(90.0D, -40.0D, -90.0D, 1.0D, 0.0D);*/
			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vertexBuffer.pos(-90.0D, -40.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			vertexBuffer.pos(-90.0D, -40.0D, 90.0D).tex(0.0D, 1.0D).endVertex();
			vertexBuffer.pos(90.0D, -40.0D, 90.0D).tex(1.0D, 1.0D).endVertex();
			vertexBuffer.pos(90.0D, -40.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			tessellator.draw();
		} else {
			//Render clip plane (for god rays)
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glColor4f(1, 1, 1, 1);
			Framebuffer parentFBO = Minecraft.getMinecraft().getFramebuffer();
			this.clipPlaneBuffer.updateGeometryBuffer(parentFBO.framebufferWidth, parentFBO.framebufferHeight);
			this.clipPlaneBuffer.bind();
			this.clipPlaneBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);
			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			/*tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 255);
			tessellator.addVertex(-9000.0D, -90.0D, -9000.0D);
			tessellator.addVertex(-9000.0D, -90.0D, 9000.0D);
			tessellator.addVertex(9000.0D, -90.0D, 9000.0D);
			tessellator.addVertex(9000.0D, -90.0D, -9000.0D);*/
			vertexBuffer.pos(-9000.0D, -90.0D, -9000.0D).color(255, 255, 255, 255).endVertex();
			vertexBuffer.pos(-9000.0D, -90.0D, 9000.0D).color(255, 255, 255, 255).endVertex();
			vertexBuffer.pos(9000.0D, -90.0D, 9000.0D).color(255, 255, 255, 255).endVertex();
			vertexBuffer.pos(9000.0D, -90.0D, -9000.0D).color(255, 255, 255, 255).endVertex();
			tessellator.draw();
			this.clipPlaneBuffer.updateDepthBuffer();
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		GL11.glPopMatrix();
	}

	private void renderAuroras(Minecraft mc, float partialTicks) {
		Random rand = mc.world.rand;
		double newAuroraPosX = mc.player.posX + rand.nextInt(160) - 80;
		double newAuroraPosZ = mc.player.posZ + rand.nextInt(160) - 80;
		double newAuroraPosY = 260;
		double minDist = 0.0D;

		Iterator<AuroraRenderer> auroraIT = this.auroras.iterator();
		while(auroraIT.hasNext()) {
			AuroraRenderer aurora = auroraIT.next();
			if(aurora.getDistance(mc.player.posX, aurora.getY(), mc.player.posZ) > 180) {
				auroraIT.remove();
				this.auroras.remove(aurora);
			}
			double dist = aurora.getDistance(newAuroraPosX, newAuroraPosY, newAuroraPosZ);
			if(dist < minDist || minDist == 0.0D) {
				minDist = dist;
			}
		}
		if(minDist > 150 || this.auroras.size() == 0) {
			this.auroras.add(new AuroraRenderer(newAuroraPosX, newAuroraPosY + rand.nextInt(100), newAuroraPosZ, new Vector2d(rand.nextFloat()*2.0F-1.0F, rand.nextFloat()*2.0F-1.0F), rand.nextInt(40) + 15));
		}

		List<Vector4f> gradients = new ArrayList<Vector4f>();

		EventAuroras event = null;
		if(mc.world != null && mc.world.provider instanceof WorldProviderBetweenlands) {
			event = ((WorldProviderBetweenlands)mc.world.provider).getWorldData().getEnvironmentEventRegistry().auroras;
		}
		if(event != null) {
			switch(event.getAuroraType()) {
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
			}
		}

		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glTranslated(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ);
		float alpha = 0.4F;
		float ticksElapsed = event.getTicksElapsed() + partialTicks;
		if(ticksElapsed < 500) {
			alpha *= ticksElapsed / 500.0F;
		}
		float ticksRemaining = event.getTicks() - partialTicks;
		if(ticksRemaining < 500) {
			alpha *= ticksRemaining / 500.0F;
		}
		for(AuroraRenderer aurora : this.auroras) {
			aurora.render(alpha, gradients);
		}
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(true);
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
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

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogf(GL11.GL_FOG_START, 30);
		GL11.glFogf(GL11.GL_FOG_END, 50);
		GL11.glColor3f(skyR, skyG, skyB);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderHelper.disableStandardItemLighting();

		float sunriseSunsetR = 0.0F;
		float sunriseSunsetG = 0.0F;
		float sunriseSunsetB = 0.0F;

		float[] sunriseSunsetColors = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
		if (sunriseSunsetColors != null) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			sunriseSunsetR = sunriseSunsetColors[0];
			sunriseSunsetG = sunriseSunsetColors[1];
			sunriseSunsetB = sunriseSunsetColors[2];

			if (mc.gameSettings.anaglyph) {
				anaglyphR = (sunriseSunsetR * 30.0F + sunriseSunsetG * 59.0F + sunriseSunsetB * 11.0F) / 100.0F;
				anaglyphG = (sunriseSunsetR * 30.0F + sunriseSunsetG * 70.0F) / 100.0F;
				anaglyphB = (sunriseSunsetR * 30.0F + sunriseSunsetB * 70.0F) / 100.0F;
				sunriseSunsetR = anaglyphR;
				sunriseSunsetG = anaglyphG;
				sunriseSunsetB = anaglyphB;
			}

			vertexBuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			vertexBuffer.pos(0.0D, 100.0D, 0.0D).color(sunriseSunsetR, sunriseSunsetG, sunriseSunsetB, sunriseSunsetColors[3]).endVertex();;
			//tessellator.setColorRGBA_F(sunriseSunsetR, sunriseSunsetG, sunriseSunsetB, sunriseSunsetColors[3]);
			//tessellator.addVertex(0.0D, 100.0D, 0.0D);
			//tessellator.setColorRGBA_F(sunriseSunsetColors[0], sunriseSunsetColors[1], sunriseSunsetColors[2], 0.0F);

			byte segments = 16;
			for (int j = 0; j <= segments; ++j) {
				float angle = (float)j * (float)Math.PI * 2.0F / (float)segments;
				float vx = MathHelper.sin(angle);
				float vy = MathHelper.cos(angle);
				//tessellator.addVertex((double)(vx * 120.0F), (double)(vy * 120.0F), (double)(-vy * 40.0F * sunriseSunsetColors[3]));
				vertexBuffer.pos((double)(vx * 120.0F), (double)(vy * 120.0F), (double)(-vy * 40.0F * sunriseSunsetColors[3])).color(sunriseSunsetColors[0], sunriseSunsetColors[1], sunriseSunsetColors[2], 0.0F).endVertex();
			}

			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		float invRainStrength = 1.0F - world.getRainStrength(partialTicks);

		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		boolean useShaderSky = ShaderHelper.INSTANCE.isWorldShaderActive() && ShaderHelper.INSTANCE.getWorldShader() != null && ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture() >= 0;

		float starBrightness = (world.getStarBrightness(partialTicks) + 0.5F) * invRainStrength * invRainStrength * invRainStrength;
		float fade = 1.0F;
		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(mc.world);
		if(provider != null) {
			fade = provider.getEnvironmentEventRegistry().denseFog.getFade(partialTicks) * 0.95F + 0.05F;
		}
		starBrightness *= fade;
		if (starBrightness > 0.0F && !useShaderSky) {
			GL14.glBlendColor(0, 0, 0, (starBrightness - 0.22F) * 3.5F);
			GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
			GL11.glPushMatrix();
			GL11.glRotated(55, 1, 0, 0);
			this.starMesh.render();
			GL11.glPopMatrix();
			GL14.glBlendColor(1, 1, 1, 1);
			GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
		}

		GL11.glPopMatrix();

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);

		double horizon = mc.player.getPositionEyes(partialTicks).y - world.getHorizon();
		float relHorizon = -((float)(horizon + 65.0D));
		if (horizon < 0.0D) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(this.skyDispList2);
			GL11.glPopMatrix();
			float boxWidth = 2.0F * relHorizon;
			float boxHeight = -boxWidth;
			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			/*tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertex((double)(-boxWidth), (double)relHorizon, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)relHorizon, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth));
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)(-boxWidth));
			tessellator.addVertex((double)boxWidth, (double)relHorizon, (double)(-boxWidth));
			tessellator.addVertex((double)(-boxWidth), (double)relHorizon, (double)(-boxWidth));
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)(-boxWidth));
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)relHorizon, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)relHorizon, (double)(-boxWidth));
			tessellator.addVertex((double)(-boxWidth), (double)relHorizon, (double)(-boxWidth));
			tessellator.addVertex((double)(-boxWidth), (double)relHorizon, (double)boxWidth);
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth));
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth));
			tessellator.addVertex((double)(-boxWidth), (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)boxWidth);
			tessellator.addVertex((double)boxWidth, (double)boxHeight, (double)(-boxWidth));*/
			vertexBuffer.pos((double)(-boxWidth), (double)relHorizon, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)relHorizon, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)relHorizon, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)relHorizon, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)relHorizon, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)relHorizon, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)relHorizon, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)relHorizon, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)(-boxWidth), (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)boxWidth).color(0, 0, 0, 255).endVertex();
			vertexBuffer.pos((double)boxWidth, (double)boxHeight, (double)(-boxWidth)).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
		}

		if (world.provider.isSkyColored()) {
			GL11.glColor3f(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F);
		} else {
			GL11.glColor3f(skyR, skyG, skyB);
		}

		/*GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float)(horizon - 16.0D)), 0.0F);
		GL11.glCallList(this.skyDispList2);
		GL11.glPopMatrix();*/

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.1F, 0.8F, 0.55F, starBrightness / (!useShaderSky ? 1.5F : 1.0F));

		if(useShaderSky) {
			//Render shader sky dome
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture());
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glCallList(this.skyDispListStart);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			//Render sky clip plane
			this.renderSkyTexture(mc, true);
		} else {
			if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
				//Render fancy non-shader sky dome
				mc.renderEngine.bindTexture(SKY_TEXTURE_RES);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				RenderHelper.disableStandardItemLighting();
				GL11.glDepthMask(false);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				GL11.glCallList(this.skyDispListStart);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glDepthMask(true);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			} else {
				//Render flat sky
				this.renderSkyTexture(mc, false);
			}
		}

		//Render sky dome with fog texture for fog noise illusion
		if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
			GL11.glPushMatrix();

			float renderRadius = 80.0F;

			GL11.glEnable(GL11.GL_FOG);
			GL11.glFogf(GL11.GL_FOG_START, renderRadius / 2F);
			GL11.glFogf(GL11.GL_FOG_END, renderRadius * 2F);

			GL11.glScaled(
					1.0F / 50.0F * renderRadius, 
					1.0F / 50.0F * renderRadius, 
					1.0F / 50.0F * renderRadius
					);

			GL11.glTranslated(0, 10, 0);

			mc.renderEngine.bindTexture(FOG_TEXTURE_RES);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

			//TODO: Don't use world time because of lag
			float ticks = Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks;

			float domeRotation = (float)(Math.sin(ticks / 1600.0F) * 120.0F - ticks / 20.0F + Math.cos(ticks / 800.0F) * 30.0F * Math.sin(ticks / 1400.0F));

			GL11.glScalef(1F, 0.8F, 1F);

			GL11.glColor4f(0, 0, 0, 0.25F);
			GL11.glCallList(this.skyDispListStart);

			GL11.glColor4f(0, 0, 0, 0.15F);
			GL11.glPushMatrix();
			GL11.glRotated(domeRotation, 0, 1, 0);
			GL11.glTranslated(0, Math.cos(ticks / 160.0F) * 4.0F, 0.0F);
			GL11.glCallList(this.skyDispListStart);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glRotated(-domeRotation / 1.8F * (Math.sin(ticks / 2000.0F) / 60.0F), 0, 1, 0);
			GL11.glTranslated(0, -Math.cos(ticks / 180.0F) * 5.0F, 0.0F);
			GL11.glCallList(this.skyDispListStart);
			GL11.glPopMatrix();

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			GL11.glPopMatrix();

			GL11.glFogf(GL11.GL_FOG_START, FogHandler.getCurrentFogStart());
			GL11.glFogf(GL11.GL_FOG_END, FogHandler.getCurrentFogEnd());
		}

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().world)) {
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_FOG);
			mc.renderEngine.bindTexture(SKY_SPOOPY_TEXTURE_RES);
			GL11.glPushMatrix();
			GL11.glRotatef(-120, 0, 1, 0);
			GL11.glRotatef(-10, 1, 0, 0);
			GL11.glTranslated(0, 50, 0);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glCallList(this.skyDispListStart);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glPopMatrix();
		}

		if(mc.world != null && mc.world.provider instanceof WorldProviderBetweenlands) {
			if(((WorldProviderBetweenlands)mc.world.provider).getWorldData().getEnvironmentEventRegistry().auroras.isActive()) {
				GL11.glDisable(GL11.GL_FOG);
				this.renderAuroras(mc, partialTicks);
				GL11.glEnable(GL11.GL_FOG);
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
	}
}
