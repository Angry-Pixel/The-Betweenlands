package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.util.ResourceLocation;

public class Blit extends PostProcessingEffect<Blit> {
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/mc/program/blit.vsh"), new ResourceLocation("thebetweenlands:shaders/mc/program/blit.fsh")};
	}
}
