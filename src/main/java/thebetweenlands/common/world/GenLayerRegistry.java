package thebetweenlands.common.world;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// Only needed if you intend to make your genlayer accessible via data pack
public class GenLayerRegistry {

    public GenLayerRegistry() {

    }
    public Map<ResourceLocation, Supplier<GenLayer>> GenLayers = new HashMap<>();
    public Map<ResourceLocation, Supplier<ProviderGenLayer>> Providers = new HashMap<>();

    public void Register(ResourceLocation name, Supplier<GenLayer> contructor) {
        GenLayers.put(name, contructor);
    }

    public void RegisterProvider(ResourceLocation name, Supplier<ProviderGenLayer> contructor) {
        Providers.put(name, contructor);
    }

}
