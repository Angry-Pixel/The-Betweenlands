package thebetweenlands.client.shader.postprocessing;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class Blit extends PostProcessingEffect<Blit> {
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("shaders/mc/program/blit.vsh"), TheBetweenlands.prefix("shaders/mc/program/blit.fsh")};
	}
}
