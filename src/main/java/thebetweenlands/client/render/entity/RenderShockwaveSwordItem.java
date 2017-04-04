package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;

public class RenderShockwaveSwordItem extends RenderEntityItem {
	public RenderShockwaveSwordItem(RenderManager renderManager, RenderItem renderItem) {
		super(renderManager, renderItem);
	}

	@Override
	public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y + 0.35F, z, entityYaw, partialTicks);

		EntityShockwaveSwordItem shockWaveSwordItem = (EntityShockwaveSwordItem) entity;

		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			float waveProgress = shockWaveSwordItem.getWaveProgress(partialTicks);
			float waveSize = (float) (Math.pow(waveProgress, 3) / 64000.0F);

			if(waveProgress < 50) {
				if(waveProgress > 40) {
					waveSize = (float) (Math.pow((10 - (waveProgress - 40)) / 10.0F * 40.0F, 3) / 64000.0F);
				}

				ShaderHelper.INSTANCE.require();
				
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						waveSize * 30.0F,
						5.0f / 255.0f * 13.0F, 
						20.0f / 255.0f * 13.0F, 
						80.0f / 255.0f * 13.0F));
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						waveSize * 15.0F,
						5.0f / 255.0f * 13.0F, 
						20.0f / 255.0f * 13.0F, 
						80.0f / 255.0f * 13.0F));
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						waveSize * 8.0F,
						5.0f / 255.0f * 13.0F, 
						20.0f / 255.0f * 13.0F, 
						80.0f / 255.0f * 13.0F));
			}

			if(waveProgress > 40) {
				ShaderHelper.INSTANCE.require();
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						((1.0F + (float)Math.sin((entity.ticksExisted + partialTicks) / 20.0F)) / 2.0F + 0.25F) * (waveProgress - 40) / 10.0F + 1.0F,
						10.0f / 255.0f * 13.0F, 
						40.0f / 255.0f * 13.0F, 
						160.0f / 255.0f * 13.0F));
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						((1.0F + (float)Math.sin((entity.ticksExisted + partialTicks) / 20.0F)) / 4.0F + 0.25F) * (waveProgress - 40) / 10.0F,
						-10.0F, 
						-10.0F, 
						-10.0F));
			}
		}
	}
}
