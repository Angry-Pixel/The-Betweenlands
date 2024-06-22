package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import thebetweenlands.common.TheBetweenlands;

public class DimensionRegistries {

	public static final ResourceKey<Level> BETWEENLANDS_DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, TheBetweenlands.prefix("the_betweenlands"));
	public static final ResourceKey<DimensionType> BETWEENLANDS_DIMENSION_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, TheBetweenlands.prefix("the_betweenlands"));
}