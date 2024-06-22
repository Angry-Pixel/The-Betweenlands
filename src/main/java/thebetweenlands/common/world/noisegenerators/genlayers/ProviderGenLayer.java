package thebetweenlands.common.world.noisegenerators.genlayers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.world.GenLayersEvent;

public abstract class ProviderGenLayer {

	// Genlayer provider settings here
	public static MapCodec<ProviderGenLayer> CODEC = RecordCodecBuilder.mapCodec((codec) -> {
		return codec.group(ResourceLocation.CODEC.fieldOf("type").forGetter((obj) -> obj.name)).
			apply(codec, (obj) -> {
				return GenLayersEvent.REGISTRY.Providers.get(obj).get();
			});
	});

	public ResourceLocation name;

	public abstract GenLayer[] initialize(long seed);
}
