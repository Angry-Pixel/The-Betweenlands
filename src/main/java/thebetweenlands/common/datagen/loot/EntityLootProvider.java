package thebetweenlands.common.datagen.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class EntityLootProvider extends EntityLootSubProvider {

	protected EntityLootProvider(HolderLookup.Provider provider) {
		super(FeatureFlags.REGISTRY.allFlags(), provider);
	}

	//TODO actually generate loot tables
	@Override
	public void generate() {
		this.noLoot(EntityRegistry.WIGHT);
		this.noLoot(EntityRegistry.SWAMP_HAG);
		this.noLoot(EntityRegistry.GECKO);
		this.noLoot(EntityRegistry.BUBBLER_CRAB);
		this.noLoot(EntityRegistry.SILT_CRAB);
		this.noLoot(EntityRegistry.ANADIA);
	}

	public <T extends Entity> void noLoot(DeferredHolder<EntityType<?>, EntityType<T>> type) {
		this.add(type.get(), LootTable.lootTable());
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return EntityRegistry.ENTITY_TYPES.getEntries().stream().map(Supplier::get);
	}
}
