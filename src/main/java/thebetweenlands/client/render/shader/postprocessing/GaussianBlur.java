package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.util.ResourceLocation;

public class GaussianBlur extends PostProcessingEffect<GaussianBlur> {
	@Override
	protected PostProcessingEffect<?>[] getStages() {
		PostProcessingEffect<?> stages[] = new PostProcessingEffect<?>[]{
				new GaussianBlur() {
					@Override
					protected PostProcessingEffect<?>[] getStages() {
						return null;
					}

					@Override
					protected ResourceLocation[] getShaders() {
						return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/gauss/gauss.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/gauss/gaussh.fsh")};
					}
				}
		};
		return stages;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/gauss/gauss.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/gauss/gaussv.fsh")};
	}
}
