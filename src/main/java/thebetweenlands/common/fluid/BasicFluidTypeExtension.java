package thebetweenlands.common.fluid;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import thebetweenlands.common.TheBetweenlands;

public class BasicFluidTypeExtension implements IClientFluidTypeExtensions {

	private final String fluidName;

	public BasicFluidTypeExtension(String fluidName) {
		this.fluidName = fluidName;
	}

	@Override
	public ResourceLocation getStillTexture() {
		return TheBetweenlands.prefix("fluid/" + this.fluidName + "_still");
	}

	@Override
	public ResourceLocation getFlowingTexture() {
		return TheBetweenlands.prefix("fluid/" + this.fluidName + "_flowing");
	}
}
