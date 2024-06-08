package thebetweenlands.common.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;

import java.util.function.Supplier;

public class BiomeDecoratorEvent extends Event implements IModBusEvent {

    // Register
    public static BiomeDecoratorRegistry REGISTRY = new BiomeDecoratorRegistry();

    public static class RegisterBiomeDecoratorEvent extends BiomeDecoratorEvent {

        public RegisterBiomeDecoratorEvent() {
            super();
        }

        // Variable input
        @SafeVarargs
        public final void Register(Pair<ResourceLocation, Supplier<BiomeDecorator>>...registerDecorator){
            for (Pair<ResourceLocation, Supplier<BiomeDecorator>> gen : registerDecorator) {
                this.Register(gen.getFirst(), gen.getSecond());
            }
        }

        // Single input
        public void Register(ResourceLocation name, Supplier<BiomeDecorator> registerDecorator){
            REGISTRY.Register(name, registerDecorator);
        }
    }
}
