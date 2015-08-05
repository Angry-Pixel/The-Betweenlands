package thebetweenlands.client.render.shader.effect;

import net.minecraft.util.ResourceLocation;

public class GaussianBlur extends DeferredEffect {
	@Override
	protected DeferredEffect[] getStages() {
		DeferredEffect stages[] = new DeferredEffect[]{
				new GaussianBlur() {
					@Override
					protected DeferredEffect[] getStages() {
						return null;
					}

					@Override
					protected ResourceLocation[] getShaders() {
						return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/gauss/gauss.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/gauss/gaussh.fsh")};
					}
				}
		};
		return stages;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/gauss/gauss.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/gauss/gaussv.fsh")};
	}
}
