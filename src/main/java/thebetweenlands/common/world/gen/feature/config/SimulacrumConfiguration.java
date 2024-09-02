package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

public record SimulacrumConfiguration(List<BlockState> variants, ResourceKey<LootTable> lootTable) implements FeatureConfiguration {
	public static final Codec<SimulacrumConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockState.CODEC.listOf().fieldOf("variants").forGetter(obj -> obj.variants),
			ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(obj -> obj.lootTable)
		).apply(instance, SimulacrumConfiguration::new)
	);
}
