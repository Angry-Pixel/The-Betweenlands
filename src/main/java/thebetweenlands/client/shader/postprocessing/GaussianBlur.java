package thebetweenlands.client.shader.postprocessing;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class GaussianBlur extends PostProcessingEffect<GaussianBlur> {
	@Override
	protected PostProcessingEffect<?>[] getStages() {
		return new PostProcessingEffect<?>[]{
				new GaussianBlur() {
					@Override
					protected PostProcessingEffect<?>[] getStages() {
						return null;
					}

					@Override
					protected ResourceLocation[] getShaders() {
						return new ResourceLocation[] {TheBetweenlands.prefix("shaders/postprocessing/gauss/gauss.vsh"), TheBetweenlands.prefix("shaders/postprocessing/gauss/gaussh.fsh")};
					}
				}
		};
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("shaders/postprocessing/gauss/gauss.vsh"), TheBetweenlands.prefix("shaders/postprocessing/gauss/gaussv.fsh")};
	}
}
