package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.entity.ShockwaveSwordItemEntity;

public class ShockwaveSwordItemEntityRenderer extends ItemEntityRenderer {
	public ShockwaveSwordItemEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
		super.render(entity, entityYaw, partialTicks, stack, buffer, light);

		if (entity instanceof ShockwaveSwordItemEntity sword) {

			if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
				float waveProgress = sword.getWaveProgress(partialTicks);
				float waveSize = (float) (Math.pow(waveProgress, 3) / 64000.0F);

				if (waveProgress < 50) {
					if (waveProgress > 40) {
						waveSize = (float) (Math.pow((10 - (waveProgress - 40)) / 10.0F * 40.0F, 3) / 64000.0F);
					}

					ShaderHelper.INSTANCE.require();

					ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(),
						waveSize * 30.0F,
						5.0f / 255.0f * 13.0F,
						20.0f / 255.0f * 13.0F,
						80.0f / 255.0f * 13.0F));
					ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(),
						waveSize * 15.0F,
						5.0f / 255.0f * 13.0F,
						20.0f / 255.0f * 13.0F,
						80.0f / 255.0f * 13.0F));
					ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(),
						waveSize * 8.0F,
						5.0f / 255.0f * 13.0F,
						20.0f / 255.0f * 13.0F,
						80.0f / 255.0f * 13.0F));
				}

				if (waveProgress > 40) {
					ShaderHelper.INSTANCE.require();
					ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(),
						((1.0F + (float) Math.sin((entity.tickCount + partialTicks) / 20.0F)) / 2.0F + 0.25F) * (waveProgress - 40) / 10.0F + 1.0F,
						10.0f / 255.0f * 13.0F,
						40.0f / 255.0f * 13.0F,
						160.0f / 255.0f * 13.0F));
					ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(),
						((1.0F + (float) Math.sin((entity.tickCount + partialTicks) / 20.0F)) / 4.0F + 0.25F) * (waveProgress - 40) / 10.0F,
						-10.0F,
						-10.0F,
						-10.0F));
				}
			}
		}
	}
}
