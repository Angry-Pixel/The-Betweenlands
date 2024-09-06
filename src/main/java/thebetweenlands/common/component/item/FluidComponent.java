package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidComponent(Fluid fluid, int amount) {

	public static final FluidComponent EMPTY = new FluidComponent(Fluids.EMPTY, 0);

	public static final Codec<FluidComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidComponent::fluid),
		Codec.INT.fieldOf("amount").forGetter(FluidComponent::amount)
	).apply(instance, FluidComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidComponent> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.FLUID), FluidComponent::fluid,
		ByteBufCodecs.INT, FluidComponent::amount,
		FluidComponent::new
	);

	public static FluidComponent fromFluidStack(FluidStack stack) {
		return new FluidComponent(stack.getFluid(), stack.getAmount());
	}

	public FluidStack makeFluidStack() {
		return new FluidStack(this.fluid(), this.amount());
	}

	public boolean isEmpty() {
		return this.fluid().isSame(Fluids.EMPTY);
	}
}
