package thebetweenlands.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import thebetweenlands.client.BetweenlandsClient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LootTableFetcher {

	@Nullable
	private final ReloadableServerRegistries.Holder reloadableServerRegistries;
	@Nullable
	private static LootTableFetcher instance;

	public LootTableFetcher() {
		this.reloadableServerRegistries = null;
	}

	public LootTableFetcher(ReloadableServerRegistries.Holder reloadableServerRegistries) {
		this.reloadableServerRegistries = reloadableServerRegistries;
	}

	public LootTable getLootTable(ResourceKey<LootTable> lootTableKey) {
		if (this.reloadableServerRegistries == null) {
			return LootTable.EMPTY;
		}
		return reloadableServerRegistries.getLootTable(lootTableKey);
	}

	public static LootTableFetcher getFetcher() {
		Level level = getServerLevel().orElse(BetweenlandsClient.getClientLevel());
		if (level.getServer() == null) {
			if (instance == null) {
				instance = new LootTableFetcher();

				return instance;
			}
			return instance;
		}
		return new LootTableFetcher(level.getServer().reloadableRegistries());
	}

	public static List<ItemStack> getDropsForTable(ResourceKey<LootTable> key) {
		List<ItemStack> drops = new ArrayList<>();

		final LootTableFetcher lootTableFetcher = getFetcher();

		lootTableFetcher.getLootTable(key).pools.forEach(
			pool -> {
				pool.entries.stream()
					.filter(entry -> entry instanceof LootItem).map(LootItem.class::cast)
					.map(entry -> new ItemStack(entry.item.value()))
					.forEach(drops::add);

				pool.entries.stream()
					.filter(entry -> entry instanceof NestedLootTable).map(NestedLootTable.class::cast)
					.map(entry -> getDropsForTable(entry.contents.left().orElse(BuiltInLootTables.EMPTY))).forEach(drops::addAll);

				pool.entries.stream()
					.filter(entry -> entry instanceof DynamicLoot).map(DynamicLoot.class::cast)
					.map(entry -> getDropsForTable(ResourceKey.create(Registries.LOOT_TABLE, entry.name))).forEach(drops::addAll);
			}
		);

		drops.removeIf(Objects::isNull);
		return drops;
	}

	public static Optional<Level> getServerLevel() {
		Minecraft minecraft = Minecraft.getInstance();
		return Optional.of(minecraft)
			.map(Minecraft::getSingleplayerServer)
			.map(integratedServer -> integratedServer.getLevel(Level.OVERWORLD));
	}
}
