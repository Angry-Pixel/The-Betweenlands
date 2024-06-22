package thebetweenlands.common.fluid;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.TheBetweenlands;

import java.util.function.Consumer;

public class BetweenlandsFluidType extends FluidType {

	private final String fluidName;

	public BetweenlandsFluidType(String fluidName, Properties properties) {
		super(properties);
		this.fluidName = fluidName;
	}

	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return TheBetweenlands.prefix("fluids/" + BetweenlandsFluidType.this.fluidName + "_still");
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return TheBetweenlands.prefix("fluids/" + BetweenlandsFluidType.this.fluidName + "_flowing");
			}
		});
	}
}
