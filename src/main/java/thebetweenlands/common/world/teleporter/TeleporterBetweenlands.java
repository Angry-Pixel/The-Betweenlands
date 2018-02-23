package thebetweenlands.common.world.teleporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.BetweenlandsConfig;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWeedwoodPortalTree;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

public final class TeleporterBetweenlands extends Teleporter {
	private final WorldServer toWorld;
	private final int fromDim;
	private final AxisAlignedBB fromBounds;

	public TeleporterBetweenlands(int fromDim, AxisAlignedBB fromBounds, WorldServer toWorld) {
		super(toWorld);
		this.fromBounds = fromBounds;
		this.fromDim = fromDim;
		this.toWorld = toWorld;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		if (!this.placeInExistingPortal(entity, rotationYaw)) {
			if(!this.makePortal(entity)) {
				//Portal failed to generate... fallback?

				BlockPos pos = this.findSuitablePortalPos(entity.getPosition());
				Chunk chunk = this.toWorld.getChunkFromBlockCoords(pos); //Force chunk to generate
				pos = new BlockPos(pos.getX(), chunk.getHeight(pos), pos.getZ());
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						for(int yo = 0; yo <= 2; yo++) {
							this.toWorld.setBlockToAir(pos.add(xo, yo, zo));
						}
					}
				}

				entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, rotationYaw, 0);
				this.setDefaultPlayerSpawnLocation(entity);
			}
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
		BlockPos existingPortal = this.findExistingPortalPos();

		if(existingPortal != null) {
			//Portal exists already
			entity.setLocationAndAngles(existingPortal.getX() + 0.5D, existingPortal.getY() + 3.0D, existingPortal.getZ() + 0.5D, rotationYaw, 0);
			this.setDefaultPlayerSpawnLocation(entity);
			return true;
		}

		return false;
	}

	/**
	 * Tries to find an already existing portal
	 * @param entity
	 * @return
	 */
	@Nullable
	protected BlockPos findExistingPortalPos() {
		LocationPortal portal = this.getPortalLocation();
		if(portal != null) {
			LocationPortal otherPortal = this.getOtherPortalLocation(portal.getOtherPortalPosition());
			if(otherPortal != null) {
				return otherPortal.getPortalPosition();
			}
		}
		return null;
	}

	/**
	 * Returns the portal location at the specified entity
	 * @param entity
	 * @return
	 */
	@Nullable
	protected LocationPortal getPortalLocation() {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.toWorld.getMinecraftServer().getWorld(this.fromDim));
		List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, this.fromBounds, loc -> loc.intersects(this.fromBounds));
		this.validatePortals(portals);
		if(!portals.isEmpty()) {
			return portals.get(0);
		}
		return null;
	}

	/**
	 * Returns the portal location on the other side
	 * @return
	 */
	@Nullable
	protected LocationPortal getOtherPortalLocation(@Nullable BlockPos pos) {
		if(pos != null) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.toWorld);
			List<LocationPortal> otherPortals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, pos.getX(), pos.getZ(), loc -> loc.getPortalPosition().equals(pos));
			this.validatePortals(otherPortals);
			if(!otherPortals.isEmpty()) {
				return otherPortals.get(0);
			}
		}
		return null;
	}

	/**
	 * Validates a list of portals and if invalid they are removed from the list and the world
	 * @param portals
	 * @return
	 */
	protected void validatePortals(List<LocationPortal> portals) {
		Iterator<LocationPortal> it = portals.iterator();
		while(it.hasNext()) {
			LocationPortal portal = it.next();
			if(!this.checkPortal(portal)) {
				portal.getWorldStorage().getLocalStorageHandler().removeLocalStorage(portal);
				it.remove();
			}
		}
	}

	/**
	 * Verifies whether a portal still exists
	 * @return
	 */
	protected boolean checkPortal(LocationPortal portal) {
		World world = portal.getWorldStorage().getWorld();
		AxisAlignedBB aabb = portal.getBounds().get(0);
		MutableBlockPos pos = new MutableBlockPos();
		for(int x = MathHelper.floor(aabb.minX); x <= MathHelper.floor(aabb.maxX); x++) {
			for(int y = MathHelper.floor(aabb.minY); y <= MathHelper.floor(aabb.maxY); y++) {
				for(int z = MathHelper.floor(aabb.minZ); z <= MathHelper.floor(aabb.maxZ); z++) {
					pos.setPos(x, y, z);
					IBlockState blockState = world.getBlockState(pos);
					if(blockState.getBlock() instanceof BlockTreePortal) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Finds a suitable position for a portal to generate nearby
	 * @param start
	 * @return
	 */
	protected BlockPos findSuitablePortalPos(BlockPos start) {
		List<Biome> validBiomes = new ArrayList<Biome>();

		validBiomes.add(BiomeRegistry.SWAMPLANDS);
		validBiomes.add(BiomeRegistry.PATCHY_ISLANDS);

		BlockPos suitablePos = this.toWorld.getBiomeProvider().findBiomePosition(start.getX(), start.getZ(), 256, validBiomes, this.toWorld.rand);

		BlockPos selectedPos;
		if(suitablePos != null) {
			selectedPos = suitablePos;
		} else {
			selectedPos = start;
		}

		Chunk chunk = this.toWorld.getChunkFromBlockCoords(selectedPos); //Force chunk to generate
		int height = chunk.getHeight(selectedPos);
		return new BlockPos(selectedPos.getX(), height, selectedPos.getZ());
	}

	@Override
	public boolean makePortal(Entity entity) {
		BlockPos genPos = this.findSuitablePortalPos(entity.getPosition());
		int checkRadius = 64;
		MutableBlockPos checkPos = new MutableBlockPos();
		WorldGenWeedwoodPortalTree genTree = new WorldGenWeedwoodPortalTree();

		//Spiral from center outwards to stay as close to the preferred position as possible
		int xo = 0, zo = 0;
		int[] dir = new int[]{0, -1};
		for (int i = (int) Math.pow(checkRadius * 2, 2); i > 0; i--) {
			if (-checkRadius < xo && xo <= checkRadius && -checkRadius < zo && zo <= checkRadius) {

				checkPos.setPos(genPos.getX() + xo, 64, genPos.getZ() + zo);
				Chunk chunk = this.toWorld.getChunkFromBlockCoords(checkPos); //Force chunk to generate
				checkPos.setY(chunk.getHeight(checkPos) - 1);

				if(SurfaceType.MIXED_GROUND.matches(this.toWorld.getBlockState(checkPos)) && this.toWorld.isAirBlock(checkPos.up()) && this.canGenerate(this.toWorld, checkPos)) {
					if(genTree.generate(this.toWorld, this.toWorld.rand, checkPos.toImmutable())) {
						LocationPortal portal = this.getPortalLocation();

						if(portal != null) {
							BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.toWorld);
							List<LocationPortal> newPortals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, checkPos.up().getX(), checkPos.up().getZ(), loc -> loc.isInside(checkPos.up()));
							if(!newPortals.isEmpty()) {
								//Link portals
								LocationPortal newPortal = newPortals.get(0);
								newPortal.setOtherPortalPosition(this.fromDim, portal.getPortalPosition());
								portal.setOtherPortalPosition(this.toWorld.provider.getDimension(), newPortal.getPortalPosition());
							}
						}

						entity.setLocationAndAngles(checkPos.getX() + 0.5D, checkPos.getY() + 4.0D, checkPos.getZ() + 0.5D, entity.rotationYaw, 0);
						this.setDefaultPlayerSpawnLocation(entity);

						return true;
					}
				}

			}

			if (xo == zo || (xo < 0 && xo == -zo) || (xo > 0 && xo == 1 - zo)){
				int d0 = dir[0];
				dir[0] = -dir[1];
				dir[1] = d0;
			}

			xo += dir[0];
			zo += dir[1];        
		}

		return false;
	}

	/**
	 * Returns whether a portal tree can generate at the specified position
	 * @param world
	 * @param pos
	 * @return
	 */
	protected boolean canGenerate(World world, BlockPos pos){
		int height = 16;
		int maxRadius = 8;
		MutableBlockPos checkPos = new MutableBlockPos();
		for (int xo = -maxRadius; xo <= maxRadius; xo++) {
			for (int zo = -maxRadius; zo <= maxRadius; zo++) {
				for (int yo = 2; yo < height; yo++) {
					checkPos.setPos(pos.getX() + xo, pos.getY() + yo, pos.getZ() + zo);
					IBlockState blockState = world.getBlockState(checkPos);
					if (blockState.getMaterial().isLiquid() || blockState.isNormalCube() || blockState.getBlock() instanceof BlockLeaves) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Sets the entities spawn location if necessary
	 * @param entity
	 */
	public void setDefaultPlayerSpawnLocation(Entity entity) {
		if (entity instanceof EntityPlayerMP == false) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) entity;
		BlockPos coords = player.getBedLocation(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);

		if (coords == null) {
			coords = player.getPosition();
			int spawnFuzz = 64;
			int spawnFuzzHalf = spawnFuzz / 2;
			BlockPos spawnPlace = this.toWorld.getTopSolidOrLiquidBlock(coords.add(this.toWorld.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, this.toWorld.rand.nextInt(spawnFuzz) - spawnFuzzHalf));
			player.setSpawnChunk(spawnPlace, true, BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
		}
	}

	@Override
	public void removeStalePortalLocations(long timer) {
		//Not needed
	}
}