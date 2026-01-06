package thebetweenlands.common.world.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.container.LootPotBlock;
import thebetweenlands.common.block.structure.MobSpawnerBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.StructureTypeRegistry;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.UUID;

public class WightFortressPiece extends TemplateStructurePiece {

	public WightFortressPiece(StructureTemplateManager manager, ResourceLocation location, BlockPos pos, Rotation rotation) {
		super(StructureTypeRegistry.WIGHT_FORTRESS_PIECE.get(), 0, manager, location, location.toString(), makeSettings(rotation), pos);
	}

	public WightFortressPiece(StructurePieceSerializationContext context, CompoundTag tag) {
		super(StructureTypeRegistry.WIGHT_FORTRESS_PIECE.get(), tag, context.structureTemplateManager(), location -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		super.addAdditionalSaveData(context, tag);
		tag.putString("Rot", this.placeSettings.getRotation().name());
	}

	private static StructurePlaceSettings makeSettings(Rotation rotation) {
		return new StructurePlaceSettings()
			.setRotation(rotation)
			.setMirror(Mirror.NONE)
			.setRotationPivot(new BlockPos(17, 0, 17))
			.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING)
			.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		boolean placed = false;
		this.placeSettings.setBoundingBox(box);
		this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
		if (this.template.placeInWorld(level, this.templatePosition, pos, this.placeSettings, random, 2)) {
			for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
				if (structuretemplate$structureblockinfo.nbt() != null) {
					StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt().getString("mode"));
					if (structuremode == StructureMode.DATA) {
						this.handleDataMarker(
							structuretemplate$structureblockinfo.nbt().getString("metadata"),
							structuretemplate$structureblockinfo.pos(),
							level,
							random,
							box
						);
					}
				}
			}
			placed = true;
		}


		IWorldStorage storage = WorldStorageGetter.getNullable(level.getLevel());
		if (storage != null && placed) {
			LocalRegion region = LocalRegion.getFromBlockPos(pos);
			long locationSeed = random.nextLong();

			LocationGuarded fortressLocation = new LocationGuarded(storage, new StorageUUID(UUID.randomUUID()), region, "wight_tower", EnumLocationType.WIGHT_TOWER);
			//this.guard = fortressLocation.getGuard();
			fortressLocation.setVisible(true);
			fortressLocation.addBounds(new AABB(pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10, pos.getX() + 42, pos.getY() + 80, pos.getZ() + 42));
			fortressLocation.setAmbience(new LocationAmbience(LocationAmbience.EnumLocationAmbience.WIGHT_TOWER).setFogRangeMultiplier(0.2F).setFogBrightness(80));
			fortressLocation.setLayer(0);
			fortressLocation.setDirty(true);
			fortressLocation.setSeed(locationSeed);

			LocationStorage puzzleLocation = new LocationStorage(storage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_puzzle", EnumLocationType.WIGHT_TOWER);
			puzzleLocation.setVisible(true);
			puzzleLocation.addBounds(new AABB(pos.getX() - 10 + 20, pos.getY() + 17, pos.getZ() - 10 + 20, pos.getX() + 42 - 20, pos.getY() + 17 + 6, pos.getZ() + 42 - 20));
			puzzleLocation.setLayer(1);
			puzzleLocation.setDirty(true);
			puzzleLocation.setSeed(locationSeed);

			LocationStorage teleporterLocation = new LocationStorage(storage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_teleporter", EnumLocationType.WIGHT_TOWER);
			teleporterLocation.setVisible(true);
			teleporterLocation.addBounds(new AABB(pos.getX() - 10 + 23, pos.getY() + 17 + 12, pos.getZ() - 10 + 23, pos.getX() + 42 - 23, pos.getY() + 17 + 6 + 11, pos.getZ() + 42 - 23));
			teleporterLocation.setLayer(2);
			teleporterLocation.setDirty(true);
			teleporterLocation.setSeed(locationSeed);

			LocationStorage bossLocation = new LocationStorage(storage, new StorageUUID(UUID.randomUUID()), region, "wight_tower_boss", EnumLocationType.WIGHT_TOWER);
			bossLocation.setVisible(true);
			bossLocation.addBounds(new AABB(pos.getX() - 10 + 17, pos.getY() + 17 + 19, pos.getZ() - 10 + 17, pos.getX() + 42 - 17, pos.getY() + 17 + 12 + 32, pos.getZ() + 42 - 17));
			bossLocation.setAmbience(new LocationAmbience(LocationAmbience.EnumLocationAmbience.WIGHT_TOWER).setFogRange(12.0F, 20.0F).setFogColorMultiplier(0.1F));
			bossLocation.setLayer(3);
			bossLocation.setDirty(true);
			bossLocation.setSeed(locationSeed);

			storage.getLocalStorageHandler().addLocalStorage(level, fortressLocation);
			storage.getLocalStorageHandler().addLocalStorage(level, puzzleLocation);
			storage.getLocalStorageHandler().addLocalStorage(level, teleporterLocation);
			storage.getLocalStorageHandler().addLocalStorage(level, bossLocation);
		}
	}

	@Override
	protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
		if (name.contains("spawner")) {
			generateSpawner(name, pos, level, random);
		} else if (name.equals("basement_resource")) {
			BlockState block = switch (random.nextInt(3)) {
				case 1 -> BlockRegistry.OCTINE_BLOCK.get().defaultBlockState();
				case 2 -> BlockRegistry.VALONITE_BLOCK.get().defaultBlockState();
				default -> BlockRegistry.SYRMORITE_BLOCK.get().defaultBlockState();
			};
			level.setBlock(pos, block, 3);
		} else if (name.equals("pot")) {
			level.removeBlock(pos, false);
			if (random.nextInt(5) == 0) {
				BlockState state = switch (random.nextInt(3)) {
					case 1 -> BlockRegistry.LOOT_POT_2.get().defaultBlockState();
					case 2 -> BlockRegistry.LOOT_POT_3.get().defaultBlockState();
					default -> BlockRegistry.LOOT_POT_1.get().defaultBlockState();
				};
				level.setBlock(pos, state.setValue(LootPotBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 3);
			}
		}
	}

	private static void generateSpawner(String name, BlockPos pos, ServerLevelAccessor level, RandomSource rand) {
		level.removeBlock(pos, false);
		switch (name) {
			case "basement_spawner" -> {
				if (rand.nextBoolean()) {
					EntityType<?> type = rand.nextBoolean() ? EntityRegistry.SWAMP_HAG.get() : rand.nextBoolean() ? EntityRegistry.CHIROMAW.get() : EntityRegistry.TERMITE.get();
					level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
					MobSpawnerBlock.setMob(level.getLevel(), pos, type);
				}
			}
			case "lv1_spawner" -> {
				if (rand.nextBoolean()) {
					EntityType<?> type = rand.nextBoolean() ? EntityRegistry.SWAMP_HAG.get() : EntityRegistry.CHIROMAW.get();
					level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
					MobSpawnerBlock.setMob(level.getLevel(), pos, type);
				}
			}
			case "lv2_spawner" -> {
				if (rand.nextBoolean()) {
					EntityType<?> type = EntityRegistry.PYRAD.get();
					level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
					MobSpawnerBlock.setMob(level.getLevel(), pos, type, logic -> logic.setCheckRange(16.0D).setMaxEntities(1));
				}
			}
			case "lv3_spawner" -> {
				EntityType<?> type = EntityRegistry.TERMITE.get();
				level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
				MobSpawnerBlock.setMob(level.getLevel(), pos, type);
			}
			case "lv4_spawner" -> {
				EntityType<?> type = EntityRegistry.WIGHT.get();
				level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
				MobSpawnerBlock.setMob(level.getLevel(), pos, type, logic -> logic.setCheckRange(32.0D).setDelayRange(3000, 5000).setMaxEntities(3));
			}
			case "armory_spawner" -> {
				if (rand.nextBoolean()) {
					EntityType<?> type = EntityRegistry.CHIROMAW.get();
					level.setBlock(pos, BlockRegistry.MOB_SPAWNER.get().defaultBlockState(), 3);
					MobSpawnerBlock.setMob(level.getLevel(), pos, type, logic -> logic.setSpawnRange(2));
				}
			}
		}
	}
}
