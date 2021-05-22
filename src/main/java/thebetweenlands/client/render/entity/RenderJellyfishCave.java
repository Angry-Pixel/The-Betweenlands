package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelJellyfishCave;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.common.entity.mobs.EntityJellyfishCave;

public class RenderJellyfishCave extends RenderJellyfish<EntityJellyfishCave> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/jellyfish_cave.png");

	private static final float[][] GLOW_COLORS = {
			{238 / 255.0f, 173 / 255.0f, 114 / 255.0f},
			{180 / 255.0f, 42 / 255.0f, 42 / 255.0f},
			{224 / 255.0f, 83 / 255.0f, 108 / 255.0f},
			{90 / 255.0f, 170 / 255.0f, 145 / 255.0f},
			{215 / 255.0f, 176 / 255.0f, 195 / 255.0f},
	};

	private static final ModelJellyfishCave MODEL = new ModelJellyfishCave();

	public RenderJellyfishCave(RenderManager manager) {
		super(manager, MODEL);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityJellyfish entity) {
		return TEXTURE;
	}

	@Override
	protected void addLighting(EntityJellyfishCave jellofooosh, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double interpX = jellofooosh.lastTickPosX + (jellofooosh.posX - jellofooosh.lastTickPosX) * partialTicks;
			double interpY = jellofooosh.lastTickPosY + (jellofooosh.posY - jellofooosh.lastTickPosY) * partialTicks;
			double interpZ = jellofooosh.lastTickPosZ + (jellofooosh.posZ - jellofooosh.lastTickPosZ) * partialTicks;

			float str = 2.0f * jellofooosh.getJellyfishSize();

			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(interpX, interpY, interpZ, 3.0f, str * 0.49f, str, str));
		}
	}
}
