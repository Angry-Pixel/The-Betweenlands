package thebetweenlands.common.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;

import java.util.function.Supplier;

public class GenLayersEvent extends Event implements IModBusEvent {

	// Register
	public static GenLayerRegistry REGISTRY = new GenLayerRegistry();

	public static class RegisterGenLayersEvent extends GenLayersEvent {

		public RegisterGenLayersEvent() {
			super();
		}

		// Variable input
		@SafeVarargs
		public final void Register(Pair<ResourceLocation, Supplier<GenLayer>>... registerGenLayer) {
			for (Pair<ResourceLocation, Supplier<GenLayer>> gen : registerGenLayer) {
				this.Register(gen.getFirst(), gen.getSecond());
			}
		}

		// Single input
		public void Register(ResourceLocation name, Supplier<GenLayer> registerGenLayer) {
			REGISTRY.Register(name, registerGenLayer);
		}

		public void RegisterProvider(ResourceLocation name, Supplier<ProviderGenLayer> provider) {
			REGISTRY.RegisterProvider(name, provider);
		}
	}
}
