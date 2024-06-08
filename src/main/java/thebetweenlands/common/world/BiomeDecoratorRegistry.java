package thebetweenlands.common.world;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


// TODO: make a datapack functionality to add feature to a registered decorator, or to make a decorator from scratch
public class BiomeDecoratorRegistry {

    public Map<ResourceLocation, Supplier<BiomeDecorator>> Decorators = new HashMap<>();

    public void Register(ResourceLocation name, Supplier<BiomeDecorator> contructor) {
        Decorators.put(name, contructor);
    }
}
