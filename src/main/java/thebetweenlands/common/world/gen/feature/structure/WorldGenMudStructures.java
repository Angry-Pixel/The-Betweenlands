package thebetweenlands.common.world.gen.feature.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class WorldGenMudStructures extends WorldGenHelper {
	private static final IBlockState MUD_BRICKS = BlockRegistry.MUD_BRICKS.getDefaultState();
	private static final IBlockState MUD_BRICK_SLAB = BlockRegistry.MUD_BRICK_SLAB.getDefaultState();
	private static final IBlockState MUD_BRICK_SLAB_UPSIDE_DOWN = MUD_BRICK_SLAB.withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.TOP);
	private static final IBlockState MUD_BRICK_STAIRS = BlockRegistry.MUD_BRICK_STAIRS.getDefaultState();
	private static final IBlockState MUD_FLOWER_POT = BlockRegistry.MUD_FLOWER_POT.getDefaultState();
	private static final IBlockState ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState();
	private static final IBlockState GECKO_CAGE = BlockRegistry.GECKO_CAGE.getDefaultState();

	public WorldGenMudStructures() {

	}

	private List<AxisAlignedBB> bounds = new ArrayList<>();

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int randomInt = rand.nextInt(3);

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		boolean generated = false;
		this.bounds.clear();

		switch (randomInt) {
		case 0:
			generated = structure1(worldIn, rand, x, y, z);
			break;
		case 1:
			generated = structure2(worldIn, rand, x, y, z);
			break;
		case 2:
			generated = structure3(worldIn, rand, x, y, z);
			break;
		default:
			generated = false;
		}

		if(generated) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
			LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position), "abandoned_shack", EnumLocationType.RUINS);
			locationStorage.setVisible(true);
			locationStorage.setSeed(rand.nextLong());
			for(AxisAlignedBB aabb : this.bounds) {
				locationStorage.addBounds(aabb.grow(2, 1, 2));
			}
			locationStorage.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
		}

		return generated;
	}

	private boolean structure1(World world, Random random, int x, int y, int z) {
		width = 7;
		depth = 5;
		int direction = random.nextInt(4);

		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, -2, 0, width, 8, depth, direction);
		AxisAlignedBB aabbGrown = aabb.grow(2, 1, 2);
		if (!world.isAreaLoaded(new BlockPos(aabbGrown.minX, aabbGrown.minY, aabbGrown.minZ), new BlockPos(aabbGrown.maxX, aabbGrown.maxY, aabbGrown.maxZ)))
			return false;
		this.bounds.add(aabb);

		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, -1, 0, MUD_BRICKS, width, 1, depth, direction);

		rotatedCubeVolume(world, x, y, z, 0, 0, 0, MUD_BRICKS, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 0, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 1, 0, MUD_FLOWER_POT, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 1, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 1, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 1, MUD_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 3, MUD_BRICKS, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 3, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 4, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 4, getStateFromRotation(1, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 4, MUD_BRICKS, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 4, MUD_BRICK_SLAB, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 1, 4, getStateFromRotation(1, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 1, 4, getStateFromRotation(2, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 3, MUD_BRICKS, 1, 1, 1, direction);
		rotatedLootUrn(world, random, x, y, z, 5, 0, 3, direction, 1, 3, 2, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);

		return true;
	}

	private boolean structure2(World world, Random random, int x, int y, int z) {
		width = 6;
		depth = 6;
		int direction = random.nextInt(4);

		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, -2, 0, width, 8, depth, direction);
		AxisAlignedBB aabbGrown = aabb.grow(2, 1, 2);
		if (!world.isAreaLoaded(new BlockPos(aabbGrown.minX, aabbGrown.minY, aabbGrown.minZ), new BlockPos(aabbGrown.maxX, aabbGrown.maxY, aabbGrown.maxZ)))
			return false;
		this.bounds.add(aabb);

		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, -1, 1, MUD_BRICKS, width, 1, depth - 1, direction);
		rotatedCubeVolumeExtendedDown(world, x, y, z, 1, -1, 0, MUD_BRICKS, width - 2, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 0, 0, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 0, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 0, 1, MUD_BRICKS, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 1, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 1, MUD_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 2, 1, MUD_BRICK_SLAB, 4, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 1, 0, 5, MUD_BRICKS, 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 5,  getStateFromRotation(1, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 5, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 1, MUD_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 1, MUD_BRICKS, 1, 1, 5, direction);
		rotatedCubeVolume(world, x, y, z, 5, 1, 1, getStateFromRotation(2, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 4, direction);
		rotatedCubeVolume(world, x, y, z, 4, 0, 0, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 0,  getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 1, 1, 2, getStateFromRotation(2, direction, MUD_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 2, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.UPSIDE_DOWN_STAIR), 1, 1, 3, direction);
		rotatedLootUrn(world, random, x, y, z, 1, 0, 2, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);
		rotatedLootUrn(world, random, x, y, z, 1, 0, 3, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);
		rotatedLootUrn(world, random, x, y, z, 1, 0, 4, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);
		rotatedLootUrn(world, random, x, y, z, 4, 0, 2, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);
		rotatedLootUrn(world, random, x, y, z, 4, 0, 3, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);
		rotatedLootUrn(world, random, x, y, z, 4, 0, 4, direction, 1, 2, 3, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);

		return true;
	}

	private boolean structure3(World world, Random random, int x, int y, int z) {
		width = 8;
		depth = 8;
		int direction = random.nextInt(4);

		if (rotatedCubeCantReplace(world, x, y, z, 0, 0, 0, width, 2, depth, direction))
			return false;
		if (!rotatedCubeMatches(world, x, y, z, 0, -1, 0, width, 1, depth, direction, SurfaceType.MIXED_GROUND))
			return false;

		AxisAlignedBB aabb = this.rotatedAABB(world, x, y, z, 0, -2, 0, width, 8, depth, direction);
		AxisAlignedBB aabbGrown = aabb.grow(2, 1, 2);
		if (!world.isAreaLoaded(new BlockPos(aabbGrown.minX, aabbGrown.minY, aabbGrown.minZ), new BlockPos(aabbGrown.maxX, aabbGrown.maxY, aabbGrown.maxZ)))
			return false;
		this.bounds.add(aabb);

		rotatedCubeVolumeExtendedDown(world, x, y, z, 0, -1, 1, MUD_BRICKS, width, 1, depth - 2, direction);

		rotatedCubeVolume(world, x, y, z, 1, 0, 1, MUD_BRICKS, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 1, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 2, 2, 1, ROTTEN_BARK, 1, 1, 7, direction);
		rotatedCubeVolume(world, x, y, z, 4, 1, 1, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 4, 2, 1, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 1, MUD_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 1, MUD_BRICKS, 1, 1, 6, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 0, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 0, 2, MUD_BRICKS, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 2, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 2, MUD_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 2, 2, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 2, MUD_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 4, direction);

		rotatedCubeVolume(world, x, y, z, 1, 2, 3, MUD_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 2, 0, 3, MUD_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 3, getStateFromRotation(3, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 4, 2, 4, MUD_BRICK_SLAB_UPSIDE_DOWN, 1, 1, 2, direction);
		rotatedCubeVolume(world, x, y, z, 7, 0, 4, MUD_BRICKS, 1, 2, 1, direction);

		rotatedCubeVolume(world, x, y, z, 2, 0, 5, MUD_BRICKS, 1, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 2, 5, ROTTEN_BARK, 1, 1, 3, direction);
		rotatedCubeVolume(world, x, y, z, 6, 0, 5, GECKO_CAGE, 1, 1, 1, direction);

		rotatedCubeVolume(world, x, y, z, 0, 0, 6, MUD_BRICKS, 4, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 0, 1, 6, getStateFromRotation(0, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 1, 6, MUD_BRICKS, 3, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 1, 2, 6, MUD_BRICKS, 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 3, 2, 6, MUD_BRICKS, 2, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 5, 0, 6, MUD_BRICKS, 2, 2, 1, direction);
		rotatedCubeVolume(world, x, y, z, 6, 2, 6, getStateFromRotation(2, direction, MUD_BRICK_STAIRS, EnumRotationSequence.STAIR), 1, 1, 1, direction);
		rotatedCubeVolume(world, x, y, z, 7, 1, 6, MUD_BRICK_SLAB, 1, 1, 1, direction);
		rotatedLootUrn(world, random, x, y, z, 1, 0, 3, direction, 1, 4, 2, LootTableRegistry.SLUDGE_PLAINS_RUINS_URN);

		return true;
	}
}
