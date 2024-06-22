package thebetweenlands.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import thebetweenlands.client.rendering.shader.BetweenlandsShaders;
import thebetweenlands.client.rendering.vertex.BetweenlandsVertexFormats;
import thebetweenlands.common.TheBetweenlands;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsSkyHandler implements ISkyRenderHandler {

	// Renders horison texture, sky texture, blobs in sky and clouds
	public ResourceLocation SkyTexture = new ResourceLocation(TheBetweenlands.ID, "textures/sky/sky_texture.png");
	public ResourceLocation FogTexture = new ResourceLocation(TheBetweenlands.ID, "textures/sky/fog_texture.png");
	float ang = 0;
	float xtest = 0.5f;
	boolean invert = false;

	VertexBuffer vertexbuffer;

	public ISkyRenderHandler compositeSky;
	public boolean compositeRender = false;            // flag is called to switch from overwold sky to betweenlands sky

	boolean test = true;

	// Betweenlands sky without a composite sky
	public BetweenlandsSkyHandler() {
	}

	// Adds betweenlands sky renderer to a sky handeler
	public BetweenlandsSkyHandler(ISkyRenderHandler compositeSky) {
		this.compositeSky = compositeSky;
	}


	public float UValueGet() {
		return 0;
	}

	public float VValueGet() {
		return 0;
	}


	// When optifine shader is loaded skip this render step
	// then check for a betweenlands sky shader
	// and if possible, add the defalt sky shader over the top of it's overworld sky fragment shader
	@Override
	public void render(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft) {

		// Overlay Betweenlands sky
		//GL40.draw

		// if optifine shader is running
		// todo:	find optifine calling card
		//			and check if shader is loaded
		// if (minecraft.) {  //level.is
		//
		//}


		Camera camera = minecraft.gameRenderer.getMainCamera();
		LevelRenderer levelrenderer = minecraft.levelRenderer;
		ShaderInstance shaderinstance = RenderSystem.getShader();
		PoseStack posestack = null;
		//Vector3f lookDir = new Vector3f(camera.rotation());
		Matrix4f oldmatrix = RenderSystem.getProjectionMatrix();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();

		// Our buffer to draw the sky

		// step one, get texture atlass sprite

		// Check if texture is present and register

		Quaternion Rotation = new Quaternion(0, 0, 0, 1);
		Rotation.mul(Vector3f.XP.rotationDegrees(camera.getXRot()));
		Rotation.mul(Vector3f.YP.rotationDegrees(camera.getYRot()));
		Matrix4f matrix = new Matrix4f(oldmatrix);

		// Set projection to rotate with camera
		matrix.multiply(Rotation);
		RenderSystem.setProjectionMatrix(oldmatrix);


		float color0[] = RenderSystem.getShaderFogColor();

		// Render overworld sky
		compositeRender = true;
		RenderSystem.setShaderFogColor(0.753f, 0.847f, 1.0f);        // overworld fog
		levelrenderer.renderSky(poseStack, oldmatrix, partialTick, camera, false, () -> {
			FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_TERRAIN, Math.max(minecraft.gameRenderer.getRenderDistance(), 32.0F), false, partialTick);
		});
		levelrenderer.renderClouds(poseStack, oldmatrix, partialTick, 0, 64, 0);
		RenderSystem.depthMask(false);
		RenderSystem.enableCull();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();

		compositeRender = false;


		// Setup fog for down the pipeline
		FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_TERRAIN, Math.max(minecraft.gameRenderer.getRenderDistance(), 1.0F), true, partialTick);
		RenderSystem.setShaderFogColor(0.03922f, 0.11765f, 0.08627f, 1);


		RenderSystem.setProjectionMatrix(oldmatrix);

		// Todo: change const values to dynamic ones
		// Set Time
		//int time = 0;
		//BetweenlandsShaders.BetweenlandsSky.BETWEENLANDS_TIME.set(time);
		//BetweenlandsShaders.BetweenlandsSky.BETWEENLANDS_TIME_FRACTION.set(Betweenlands.FractinalTime);

		// Set lower fog color (shoud be the same as world fog)
		if (minecraft.screen != null) {
			BetweenlandsShaders.BetweenlandsSky.RESOLUTION.set((float) minecraft.getWindow().getWidth(), (float) minecraft.getWindow().getHeight());    // float is faster for divison aparently so im using it here (if im wrong please inform me)
		}
		BetweenlandsShaders.BetweenlandsSky.ROTATION.set((float) Math.toRadians(camera.getXRot()), (float) Math.toRadians(camera.getYRot()), 0.0f);
		BetweenlandsShaders.BetweenlandsSky.FOV.set((float) Math.toRadians(130), (float) Math.toRadians(90));
		BetweenlandsShaders.BetweenlandsSky.DEBUG_MODE.set(0.0f);                                // non zero for true
		BetweenlandsShaders.BetweenlandsSky.LOWER_FOG_APERTURE.set(TheBetweenlands.apeture);        // mid point of fog
		BetweenlandsShaders.BetweenlandsSky.LOWER_FOG_RANGE.set(TheBetweenlands.range);            // draws gradent out from aperture
		BetweenlandsShaders.BetweenlandsSky.LOWER_FOG_COLOR.set(color0);
		BetweenlandsShaders.BetweenlandsSky.FOG_ROTATION.set(TheBetweenlands.rotation);

		//BetweenlandsShaders.BetweenlandsSky.shadertest.set(Betweenlands.distmul[0] ,Betweenlands.distmul[1], Betweenlands.distmul[2]);

		// Sunlight level with mod of 2.5 X
		float lightset = (float) (Math.cos(level.getSunAngle(partialTick)) * 2.5f) + 0.5f;

		// clamp down to 1
		if (lightset > 1.0f) {
			lightset = 1;
		}
		// clamp up to 0
		if (lightset < 0.0f) {
			lightset = 0;
		}

		BetweenlandsShaders.BetweenlandsSky.DAY_LIGHT.set(lightset);
		//BetweenlandsShaders.BetweenlandsSky.DENSE_FOG.set(0.0f);									// set dense fog to usefog texture as mask
		// Set upper fog color (shoud blend with starfield)
		float color1[] = {0.035f, 0.049f, 0.035f, 1f}; // 0.035f, 0.049f, 0.035f
		BetweenlandsShaders.BetweenlandsSky.UPPER_FOG_COLOR.set(color1);
		// Update sky atlas if needed
		if (!TheBetweenlands.skyTextureHandler.isLoaded) {
			TheBetweenlands.skyTextureHandler.setup(Minecraft.getInstance().getResourceManager());
		}


		TheBetweenlands.skyTextureHandler.simpleUpload(new RiftObject(0, 0.0f, 1.0f, 2.4f, 1.2f, 1.0f, -90));
		//Betweenlands.skyTextureHandler.simpleUpload(new RiftObject(0, 0.5f, 0.5f, 1.2f, 1.2f, 1.0f, 0));

		GL40.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, TheBetweenlands.skyTextureHandler.dataBufferId);

		RenderSystem.setShader(() -> BetweenlandsShaders.BetweenlandsSky);
		RenderSystem.setShaderTexture(0, TheBetweenlands.skyTextureHandler.skyAtlas.getId());
		RenderSystem.setShaderTexture(1, FogTexture);
		RenderSystem.enableTexture();

		// TODO: get betweenlands game events and ajust fog color here
		// Render dome around player
		// Notes: to use ico spheres or uv? (yesh uv gaming)
		if (vertexbuffer == null) {

			vertexbuffer = new VertexBuffer();
			bufferbuilder.begin(VertexFormat.Mode.QUADS, BetweenlandsVertexFormats.BETWEENLANDS_SKY);

			// Get step factor
			// Generate uv sphere
			// May be depreceated due to fragment angle texture sampling

			int HorzNodes = 24;
			int VertNodes = 24;

			float xmul = 120;
			float ymul = 120;
			float zmul = 120;

			float PlaceHorzAng = (float) (360 / HorzNodes);
			float PlaceVertAng = (float) (180 / VertNodes);
			float UVHorzAng = (float) (360 / HorzNodes);

			int HorzNodesHalf = HorzNodes / 2;
			int VertNodesHalf = VertNodes / 2;

			// TODO: render compleatly seperate from base render

			// Generate uv sphere for sky sphere
			// TODO: cache mesh to reduse overhead from regenerating every frame
			// Also TODO: make sphere generation create full sphere's uv not just 2 domes ducktaped together
			for (int loopx = 0; loopx < HorzNodes; loopx++) {
				for (int loopy = 0; loopy <= VertNodes + 1; loopy++) {


					// Math thingi (sorta worked first try woho?...) (bloody god awfal uv sphere generation on my part)
					// to get the u and v of quads
					// u = 0.5 + x output
					// v = 0.5 + z output
					// revision: changed up uv generation to make full sphere
					// TODO: cache model to go vroom vroom (take less cpu time regenerating every frame)

					/*
					 *	HorzNodes = angle
					 *	angle = PlaceHorzAng*loopx
					 *
					 * 	VertNodes = distance
					 *	distance = VertNodes/loopy
					 */

				/*

					Solution A (slaped this together in a few minutes)
						uvalue = 0.5f + ((float)(Math.cos(Math.toRadians((PlaceHorzAng*(loopx)))) * (Math.cos(Math.toRadians((PlaceVertAng*(loopy+1))))))*0.5f);
						zvalue = 0.5f + ((float)(Math.sin(Math.toRadians((PlaceHorzAng*(loopx)))) * (Math.cos(Math.toRadians((PlaceVertAng*(loopy+1))))))*0.5f);

				 	Solution B (taken directly from my 3d modeler i made 4 years ago)
				 		distance = 0;
						if (loopy != 0) {
							distance = ((float)(loopy) / (float)VertNodes);
						}
						angle = (float)PlaceHorzAng * (float)loopx;
						uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
						zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);

					// DEBUG CODE
					// Debug copy-paste script

						if (loopy == 0 && test) {
							Betweenlands.LOGGER.info(uvalue);
						}

				 */


				/* current task:

					- Figuering out what the hell im doin:

						- Create a different wraping solution, this one (Solution A) i have made seems to be fit for plainer wraping and not sphere wraping
						- However (Solution B) being made for sphere wraping has seams at the "north and south poles" of the sphere (top and bottom)
						- With these problems i am unsure how to continue

					- I think i have an idea

						- see what makes Solution A work perfectly on the poles and tweak Solution B

				*/


					//	distance = VertNodes
					float uvalue;
					float zvalue;
					float modeY;
					float distance = 0;
					float angle = 0;

					modeY = (float) Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f));

					if (loopy == 0) {
						uvalue = 0.5f + ((float) (Math.cos(Math.toRadians((PlaceHorzAng * (loopx)))) * modeY) * 0.35f);
						zvalue = 0.5f + ((float) (Math.sin(Math.toRadians((PlaceHorzAng * (loopx)))) * modeY) * 0.35f);
					} else {
						distance = 0;
						if (loopy != 0) {
							distance = ((float) (loopy + 1) / (float) VertNodes);
						}
						angle = ((float) PlaceHorzAng * (float) loopx) + 180.0f;
						uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
						zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
					}

					bufferbuilder.vertex((Math.cos(Math.toRadians(PlaceHorzAng * (loopx))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f)) * xmul),
							Math.sin(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f)) * ymul,
							(Math.sin(Math.toRadians(PlaceHorzAng * (loopx))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f))) * zmul).color(1, 1, 1, 0.5f).
						uv(uvalue, zvalue).endVertex();
					// second point

					modeY = (float) Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f));
					if (loopy == 0) {
						uvalue = 0.5f + ((float) (Math.cos(Math.toRadians((PlaceHorzAng * (loopx + 1)))) * modeY) * 0.35f);
						zvalue = 0.5f + ((float) (Math.sin(Math.toRadians((PlaceHorzAng * (loopx + 1)))) * modeY) * 0.35f);
					} else {
						distance = 0;
						if (loopy != 0) {
							distance = ((float) (loopy + 1) / (float) VertNodes);
						}
						angle = ((float) PlaceHorzAng * (float) (loopx + 1)) + 180.0f;
						uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
						zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
					}

					bufferbuilder.vertex((Math.cos(Math.toRadians(PlaceHorzAng * (loopx + 1))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f)) * xmul),
							Math.sin(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f)) * ymul,
							(Math.sin(Math.toRadians(PlaceHorzAng * (loopx + 1))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy + 1)) + 90.0f))) * zmul).color(1, 1, 1, 0.5f).
						uv(uvalue, zvalue).endVertex();
					// third point
					modeY = (float) Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f));
					if (loopy == 0) {
						uvalue = 0.5f + ((float) (Math.cos(Math.toRadians((PlaceHorzAng * (loopx + 1)))) * modeY) * 0.35f);
						zvalue = 0.5f + ((float) (Math.sin(Math.toRadians((PlaceHorzAng * (loopx + 1)))) * modeY) * 0.35f);
					} else {
						distance = 0;
						if (loopy != 0) {
							distance = ((float) (loopy) / (float) VertNodes);
						}
						angle = ((float) PlaceHorzAng * (float) (loopx + 1)) + 180.0f;
						uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
						zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
					}

					bufferbuilder.vertex((Math.cos(Math.toRadians(PlaceHorzAng * (loopx + 1))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f)) * xmul),
							Math.sin(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f)) * ymul,
							(Math.sin(Math.toRadians(PlaceHorzAng * (loopx + 1))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f))) * zmul).color(1, 1, 1, 0.5f).
						uv(uvalue, zvalue).endVertex();
					// forth point
					modeY = (float) Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f));
					if (loopy == 0) {
						uvalue = 0.5f + ((float) (Math.cos(Math.toRadians((PlaceHorzAng * (loopx)))) * modeY) * 0.35f);
						zvalue = 0.5f + ((float) (Math.sin(Math.toRadians((PlaceHorzAng * (loopx)))) * modeY) * 0.35f);
					} else {
						distance = 0;
						if (loopy != 0) {
							distance = ((float) (loopy) / (float) VertNodes);
						}
						angle = ((float) PlaceHorzAng * (float) loopx) + 180.0f;
						uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
						zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
					}

					if (loopx == 0 && test) {
						TheBetweenlands.LOGGER.info("u: " + uvalue);
						//Betweenlands.LOGGER.info("v: " + zvalue);
					}

					bufferbuilder.vertex((Math.cos(Math.toRadians(PlaceHorzAng * (loopx))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f)) * xmul),
							Math.sin(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f)) * ymul,
							(Math.sin(Math.toRadians(PlaceHorzAng * (loopx))) * Math.cos(Math.toRadians((PlaceVertAng * (loopy)) + 90.0f))) * zmul).color(1, 1, 1, 0.5f).
						uv(uvalue, zvalue).endVertex();

				}
			}

			// Send buffer to vertex buffer and draw
			bufferbuilder.end();
			vertexbuffer.upload(bufferbuilder);
		}
		vertexbuffer.drawWithShader(poseStack.last().pose(), oldmatrix, BetweenlandsShaders.BetweenlandsSky);
		// debug
		test = false;

		// Reset projection matrix
		RenderSystem.setProjectionMatrix(oldmatrix);
		GL40.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
	}

}
