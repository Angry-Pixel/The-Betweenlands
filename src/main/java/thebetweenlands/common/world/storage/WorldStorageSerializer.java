package thebetweenlands.common.world.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldStorageSerializer implements IAttachmentSerializer<CompoundTag, BetweenlandsWorldStorage> {

	@Override
	public @Nullable CompoundTag write(BetweenlandsWorldStorage storage, HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		for (IEnvironmentEvent event : storage.getEnvironmentEventRegistry().getEvents().values()) {
			event.writeToNBT(tag, registries);
		}
		tag.putBoolean("eventsDisabled", storage.getEnvironmentEventRegistry().isDisabled());
		CompoundTag aspectData = new CompoundTag();
		storage.getAspectManager().saveStaticAspects(aspectData, registries);
		tag.put("itemAspects", aspectData);

//		CompoundTag biomesNbt = new CompoundTag();
//		for (BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
//			CompoundTag biomeSpawnEntriesNbt = new CompoundTag();
//			this.getBiomeSpawnEntriesData(biome).writeToNbt(biomeSpawnEntriesNbt);
//			biomesNbt.put(biome.getRegistryName().toString(), biomeSpawnEntriesNbt);
//		}
//		tag.put("biomeData", biomesNbt);

		ListTag spiritTreeKillTokensNbt = new ListTag();
		for (SpiritTreeKillToken token : storage.getSpiritTreeKillTokens()) {
			spiritTreeKillTokensNbt.add(token.writeToNBT());
		}
		tag.put("spiritTreeKillTokens", spiritTreeKillTokensNbt);
		return tag;
	}

	@Override
	public BetweenlandsWorldStorage read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider registries) {
		BetweenlandsWorldStorage storage = new BetweenlandsWorldStorage();
		for (IEnvironmentEvent event : storage.getEnvironmentEventRegistry().getEvents().values()) {
			event.readFromNBT(tag, registries);
		}
		storage.getEnvironmentEventRegistry().setDisabled(tag.getBoolean("eventsDisabled"));
		storage.getAspectManager().loadAndPopulateStaticAspects(tag.getCompound("itemAspects"), registries, AspectManager.getAspectsSeed(Optional.ofNullable(TheBetweenlands.tryGetServer(Level.OVERWORLD)).map(ServerLevel::getSeed).orElse(0L)));

		storage.getBiomeSpawnEntriesData().clear();
//		if (tag.contains("biomeData", Tag.TAG_COMPOUND)) {
//			CompoundTag biomesNbt = tag.getCompound("biomeData");
//			for (String key : biomesNbt.getAllKeys()) {
//				Biome biome = Biome.REGISTRY.getObject(ResourceLocation.tryParse(key));
//				if (biome instanceof ICustomSpawnEntriesProvider) {
//					this.getBiomeSpawnEntriesData(biome).readFromNbt(biomesNbt.getCompound(key));
//				}
//			}
//		}

		storage.getSpiritTreeKillTokens().clear();
		ListTag spiritTreeKillTokensNbt = tag.getList("spiritTreeKillTokens", Tag.TAG_COMPOUND);
		for (int i = 0; i < spiritTreeKillTokensNbt.size(); i++) {
			storage.getSpiritTreeKillTokens().add(SpiritTreeKillToken.readFromNBT(spiritTreeKillTokensNbt.getCompound(i)));
		}
		return storage;
	}
}
