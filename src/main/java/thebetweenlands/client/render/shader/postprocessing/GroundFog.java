package thebetweenlands.client.render.shader.postprocessing;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.RenderUtils;

public class GroundFog extends PostProcessingEffect<GroundFog> {
	public static final ResourceLocation TEXTURE_GROUND_FOG_HEIGHT_MAP = new ResourceLocation(ModInfo.ID, "textures/shaders/ground_fog_height_map.png");

	public static final int MAX_FOG_VOLUMES = 16;

	public static class GroundFogVolume {
		public final Vec3d position, size;
		public final float r, g, b;
		public final float inScattering, extinction;

		public GroundFogVolume(Vec3d position, Vec3d size, float inScattering, float extinction, int color) {
			this(position, size, inScattering, extinction, (float)(color >> 16 & 0xff) / 255F, (float)(color >> 8 & 0xff) / 255F, (float)(color & 0xff) / 255F);
		}

		public GroundFogVolume(Vec3d position, Vec3d size, float inScattering, float extinction, float r, float g, float b) {
			this.position = position;
			this.size = size;
			this.inScattering = inScattering;
			this.extinction = extinction;
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	private final int[] fogVolumePositionUniformIDs = new int[MAX_FOG_VOLUMES];
	private final int[] fogVolumeColorUniformIDs = new int[MAX_FOG_VOLUMES];
	private final int[] fogVolumeSizeUniformIDs = new int[MAX_FOG_VOLUMES];
	private final int[] fogVolumeExtinctionUniformIDs = new int[MAX_FOG_VOLUMES];
	private final int[] fogVolumeInScatteringUniformIDs = new int[MAX_FOG_VOLUMES];

	//Uniforms
	private int depthUniformID = -1;
	private int heightMapUniformID = -1;
	private int invMVPUniformID = -1;
	private int msTimeUniformID = -1;
	private int worldTimeUniformID = -1;
	private int renderPosUniformID = -1;
	private int viewPosUniformID = -1;
	private int fogVolumeAmountUniformIDs = -1;
	private int fogModeUniformID = -1;
	
	private int depthBufferTexture = -1;

	private List<GroundFogVolume> volumes = Collections.emptyList();

	public GroundFog setDepthBufferTexture(int id) {
		this.depthBufferTexture = id;
		return this;
	}

	public GroundFog setFogVolumes(List<GroundFogVolume> volumes) {
		this.volumes = volumes;
		return this;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/ground_fog/ground_fog.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/ground_fog/ground_fog.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.depthUniformID = this.getUniform("s_diffuse_depth");
		this.heightMapUniformID = this.getUniform("s_height_map");
		this.invMVPUniformID = this.getUniform("u_INVMVP");
		this.msTimeUniformID = this.getUniform("u_msTime");
		this.worldTimeUniformID = this.getUniform("u_worldTime");
		this.viewPosUniformID = this.getUniform("u_viewPos");
		this.renderPosUniformID = this.getUniform("u_renderPos");
		this.fogModeUniformID = this.getUniform("u_fogMode");

		for(int i = 0; i < MAX_FOG_VOLUMES; i++) {
			this.fogVolumePositionUniformIDs[i] = this.getUniform("u_fogVolumes[" + i + "].position");
			this.fogVolumeColorUniformIDs[i] = this.getUniform("u_fogVolumes[" + i + "].color");
			this.fogVolumeSizeUniformIDs[i] = this.getUniform("u_fogVolumes[" + i + "].size");
			this.fogVolumeExtinctionUniformIDs[i] = this.getUniform("u_fogVolumes[" + i + "].extinction");
			this.fogVolumeInScatteringUniformIDs[i] = this.getUniform("u_fogVolumes[" + i + "].inScattering");
		}

		this.fogVolumeAmountUniformIDs = this.getUniform("u_fogVolumesAmount");

		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();

		this.uploadSampler(this.depthUniformID, shader.getDepthBuffer().getGlTextureId(), 1);

		this.uploadSampler(this.heightMapUniformID, TEXTURE_GROUND_FOG_HEIGHT_MAP, 2);
		
		this.uploadMatrix4f(this.invMVPUniformID, shader.getInvertedModelviewProjectionMatrix());

		final double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		final double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		final double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		this.uploadFloat(this.msTimeUniformID, System.nanoTime() / 1000000.0F);
		this.uploadFloat(this.worldTimeUniformID, RenderUtils.getRenderTickCounter() + partialTicks);

		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		Vec3d camPos = renderView != null ? ActiveRenderInfo.projectViewFromEntity(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks) : Vec3d.ZERO;
		this.uploadFloat(this.viewPosUniformID, (float)(camPos.x - renderPosX), (float)(camPos.y - renderPosY), (float)(camPos.z - renderPosZ));

		this.uploadFloat(this.renderPosUniformID, (float)renderPosX, (float)renderPosY, (float)renderPosZ);

		int renderedVolumes = Math.min(MAX_FOG_VOLUMES, this.volumes.size());

		this.uploadInt(this.fogVolumeAmountUniformIDs, renderedVolumes);

		for(int i = 0; i < renderedVolumes; i++) {
			GroundFogVolume volume = this.volumes.get(i);
			this.uploadFloat(this.fogVolumePositionUniformIDs[i], (float)(volume.position.x - renderPosX), (float)(volume.position.y - renderPosY), (float)(volume.position.z - renderPosZ));
			this.uploadFloat(this.fogVolumeColorUniformIDs[i], volume.r, volume.g, volume.b);
			this.uploadFloat(this.fogVolumeSizeUniformIDs[i], (float)volume.size.x, (float)volume.size.y, (float)volume.size.z);
			this.uploadFloat(this.fogVolumeExtinctionUniformIDs[i], (float)volume.extinction);
			this.uploadFloat(this.fogVolumeInScatteringUniformIDs[i], (float)volume.inScattering);
		}
		
		this.uploadInt(this.fogModeUniformID, FogHandler.getCurrentFogMode());
	}
}
