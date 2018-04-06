package thebetweenlands.common.world.teleporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSmallPortal;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWeedwoodPortalTree;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

public final class TeleporterBetweenlands extends Teleporter {
	private final WorldServer toWorld;
	private final int fromDim;
	private final AxisAlignedBB fromBounds;

	public static final String LAST_PORTAL_POS_NBT = "thebetweenlands.last_portal_location";
	
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

				BlockPos pos = this.findSuitableBetweenlandsPortalPos(entity.getPosition());
				Chunk chunk = this.toWorld.getChunkFromBlockCoords(pos); //Force chunk to generate
				pos = new BlockPos(pos.getX(), chunk.getHeight(pos), pos.getZ());
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						for(int yo = 0; yo <= 2; yo++) {
							this.toWorld.setBlockToAir(pos.add(xo, yo, zo));
						}
					}
				}

				this.setEntityLocation(entity, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, rotationYaw, 0);
				this.setDefaultPlayerSpawnLocation(pos, entity);
			}
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
		BlockPos existingPortal = this.findExistingPortalPos();

		if(existingPortal != null) {
			//Portal exists already
			this.setEntityLocation(entity, existingPortal.getX() + 0.5D, existingPortal.getY() + 2.0D, existingPortal.getZ() + 0.5D, rotationYaw, 0);
			this.setDefaultPlayerSpawnLocation(existingPortal, entity);
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
	protected BlockPos findSuitableBetweenlandsPortalPos(BlockPos start) {
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

	/**
	 * Finds a suitable position for a portal to generate nearby
	 * @param start
	 * @return
	 */
	protected BlockPos findSuitableNonBLPortalPos(BlockPos start) {
		int bestYSpace = -1;
		BlockPos bestSuitablePos = null;
		MutableBlockPos checkPos = new MutableBlockPos();
		for(int xo = -16; xo <= -16; xo++) {
			for(int zo = -16; zo <= -16; zo++) {
				checkPos.setPos(start.getX() + xo, start.getY(), start.getZ() + zo);
				Chunk chunk = this.toWorld.getChunkFromBlockCoords(checkPos); //Force chunk to generate
				int height = chunk.getHeight(checkPos);
				if(height > 0 && height < this.toWorld.getActualHeight() - 16) {
					return new BlockPos(checkPos.getX(), height, checkPos.getZ());
				}
				int ySpace = -1;
				boolean isUnsuitable = true;
				for(int y = this.toWorld.getActualHeight() - 16 + 1; y > 8; y--) {
					checkPos.setY(y);
					IBlockState state = chunk.getBlockState(checkPos);
					boolean isNextLiquid = state.getMaterial().isLiquid();
					boolean isNextUnsuitable = state.isNormalCube() || isNextLiquid;
					if(!isUnsuitable) {
						if(isNextUnsuitable) {
							if(ySpace >= bestYSpace && !isNextLiquid) {
								bestYSpace = ySpace;
								bestSuitablePos = new BlockPos(checkPos.getX(), y, checkPos.getZ());

								if(bestYSpace >= 20) {
									return bestSuitablePos;
								}
							}
							ySpace = 0;
						} else {
							ySpace++;
						}
					}
					isUnsuitable = isNextUnsuitable;
				}
			}
		}
		if(bestSuitablePos != null) {
			return bestSuitablePos;
		}
		int randY = 8 + this.toWorld.rand.nextInt(this.toWorld.getActualHeight() - 16 - 8);
		skip: while(randY < this.toWorld.getActualHeight() - 28) {
			int height = 5;
			for(int yo = height; yo > 0; yo--) {
				for(int xo = -4; xo <= 4; xo++) {
					for(int zo = -4; zo <= 4; zo++) {
						checkPos.setPos(start.getX() + xo, randY + yo, start.getZ() + zo);
						if(this.toWorld.getBlockState(checkPos).getMaterial().isLiquid()) {
							randY += yo + 1;
							continue skip;
						}
					}
				}
			}
			break;
		}
		return new BlockPos(start.getX(), randY, start.getZ());
	}

	@Override
	public boolean makePortal(Entity entity) {
		boolean isToBL = this.toWorld.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
		BlockPos center;
		if(isToBL) {
			center = this.findSuitableBetweenlandsPortalPos(entity.getPosition());
		} else {
			center = this.findSuitableNonBLPortalPos(entity.getPosition());
		}
		if(isToBL && this.generateBetweenlandsTreePortal(entity, center)) {
			return true;
		} else if(!isToBL && this.generateTreePortal(entity, center)) {
			return true;
		}
		return this.generateSmallPortal(entity, center);
	}

	protected boolean generateBetweenlandsTreePortal(Entity entity, BlockPos center) {
		WorldGenWeedwoodPortalTree genTree = new WorldGenWeedwoodPortalTree();

		return this.spiralGenerate(center, 64, 0, 0, checkPos -> {
			Chunk chunk = this.toWorld.getChunkFromBlockCoords(checkPos); //Force chunk to generate
			checkPos.setY(chunk.getHeight(checkPos) - 1);

			if(SurfaceType.MIXED_GROUND.matches(this.toWorld.getBlockState(checkPos)) && this.toWorld.isAirBlock(checkPos.up()) && this.canGeneratePortalTree(this.toWorld, checkPos)) {
				if(genTree.generate(this.toWorld, this.toWorld.rand, checkPos.toImmutable())) {
					this.lonkPortalsTogetherAndTeleport(entity, checkPos, 0.5D, 2.0D, 0.5D);
					return true;
				}
			}

			return false;
		});
	}

	protected boolean generateTreePortal(Entity entity, BlockPos center) {
		WorldGenWeedwoodPortalTree genTree = new WorldGenWeedwoodPortalTree();

		return this.spiralGenerate(center, 32, Math.min(center.getY() - 2, 8), 8, checkPos -> {
			if(this.toWorld.getBlockState(checkPos).isNormalCube() && this.toWorld.isAirBlock(checkPos.up()) && this.canGeneratePortalTree(this.toWorld, checkPos)) {
				if(genTree.generate(this.toWorld, this.toWorld.rand, checkPos.toImmutable())) {
					this.lonkPortalsTogetherAndTeleport(entity, checkPos, 0.5D, 2.0D, 0.5D);
					return true;
				}
			}

			return false;
		});
	}

	protected boolean generateSmallPortal(Entity entity, BlockPos center) {
		WorldGenSmallPortal genPortal = new WorldGenSmallPortal(EnumFacing.NORTH);

		if(this.spiralGenerate(center, 32, Math.min(center.getY() - 2, 8), 8, checkPos -> {
			if(this.toWorld.getBlockState(checkPos).isNormalCube() && this.toWorld.isAirBlock(checkPos.up()) && this.canGenerateSmallPortalInOpen(this.toWorld, checkPos)) {
				if(genPortal.generate(this.toWorld, this.toWorld.rand, checkPos.toImmutable().up())) {
					this.lonkPortalsTogetherAndTeleport(entity, checkPos.up(), 0.5D, 1.0D, -0.5D);
					return true;
				}
			}

			return false;
		})) {
			return true;
		}

		if(this.spiralGenerate(center, 32, Math.min(center.getY() - 2, 8), 8, checkPos -> {
			if(this.toWorld.getBlockState(checkPos).isNormalCube() && !this.isSmallPortalObstructedByOthersOrLiquid(this.toWorld, checkPos)) {
				if(genPortal.generate(this.toWorld, this.toWorld.rand, checkPos.toImmutable().up())) {
					this.lonkPortalsTogetherAndTeleport(entity, checkPos.up(), 0.5D, 1.0D, -0.5D);
					return true;
				}
			}

			return false;
		})) {
			return true;
		}

		if(genPortal.generate(this.toWorld, this.toWorld.rand, center.toImmutable())) {
			this.lonkPortalsTogetherAndTeleport(entity, center, 0.5D, 1.0D, -0.5D);
			return true;
		}

		return false;
	}

	protected void lonkPortalsTogetherAndTeleport(Entity entity, BlockPos newPortalPos, double playerOffsetX, double playerOffsetY, double playerOffsetZ) {
		LocationPortal portal = this.getPortalLocation();

		if(portal != null) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.toWorld);
			List<LocationPortal> newPortals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, newPortalPos.up().getX(), newPortalPos.up().getZ(), loc -> loc.isInside(newPortalPos.up()));
			if(!newPortals.isEmpty()) {
				//Link portals
				LocationPortal newPortal = newPortals.get(0);
				newPortal.setOtherPortalPosition(this.fromDim, portal.getPortalPosition());
				portal.setOtherPortalPosition(this.toWorld.provider.getDimension(), newPortal.getPortalPosition());
			}
		}

		this.setEntityLocation(entity, newPortalPos.getX() + playerOffsetX, newPortalPos.getY() + playerOffsetY, newPortalPos.getZ() + playerOffsetZ, entity.rotationYaw, 0);
		this.setDefaultPlayerSpawnLocation(newPortalPos, entity);
	}

	protected boolean spiralGenerate(BlockPos center, int radius, int yDown, int yUp, Function<MutableBlockPos, Boolean> gen) {
		MutableBlockPos checkPos = new MutableBlockPos();

		//Spiral from center outwards to stay as close to the preferred position as possible
		int xo = 0, zo = 0;
		int[] dir = new int[]{0, -1};
		for (int i = (int) Math.pow(radius * 2, 2); i > 0; i--) {
			if (-radius < xo && xo <= radius && -radius < zo && zo <= radius) {
				checkPos.setPos(center.getX() + xo, center.getY(), center.getZ() + zo);
				if(gen.apply(checkPos)) {
					return true;
				}
				for(int yo = 1; yo <= yUp; yo++) {
					checkPos.setPos(center.getX() + xo, center.getY() + yo, center.getZ() + zo);
					if(gen.apply(checkPos)) {
						return true;
					}
				}
				for(int yo = 1; yo <= yDown; yo++) {
					checkPos.setPos(center.getX() + xo, center.getY() - yo, center.getZ() + zo);
					if(gen.apply(checkPos)) {
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
	protected boolean canGeneratePortalTree(World world, BlockPos pos){
		int height = 10;
		int maxRadius = 8;
		MutableBlockPos checkPos = new MutableBlockPos();
		for (int xo = -maxRadius; xo <= maxRadius; xo++) {
			for (int zo = -maxRadius; zo <= maxRadius; zo++) {
				if(Math.sqrt(xo*xo + zo*zo) <= maxRadius) {
					for (int yo = 2; yo < height; yo++) {
						checkPos.setPos(pos.getX() + xo, pos.getY() + yo, pos.getZ() + zo);
						IBlockState blockState = world.getBlockState(checkPos);
						if (blockState.getMaterial().isLiquid() || blockState.isNormalCube() || blockState.getBlock() instanceof BlockLeaves) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns whether a small portal can generate at the specified position
	 * @param world
	 * @param pos
	 * @return
	 */
	protected boolean canGenerateSmallPortalInOpen(World world, BlockPos pos){
		for(MutableBlockPos p : BlockPos.getAllInBoxMutable(pos.getX() - 3, pos.getY(), pos.getZ() - 2, pos.getX() + 3, pos.getY() + 7, pos.getZ() + 3)) {
			IBlockState blockState = world.getBlockState(p);
			boolean isOutside = Math.abs(pos.getX() - p.getX()) >= 2 || Math.abs(pos.getZ() - p.getZ()) >= 2 || p.getY() - pos.getY() >= 5;
			if (!isOutside && (blockState.getMaterial().isLiquid() || blockState.isNormalCube() || blockState.getBlock() instanceof BlockLeaves)) {
				return false;
			} else if(isOutside && blockState.getBlock() == BlockRegistry.TREE_PORTAL) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether the specified position is obstructed by another small portal or liquid
	 * @param world
	 * @param pos
	 * @return
	 */
	protected boolean isSmallPortalObstructedByOthersOrLiquid(World world, BlockPos pos) {
		for(MutableBlockPos p : BlockPos.getAllInBoxMutable(pos.getX() - 3, pos.getY(), pos.getZ() - 3, pos.getX() + 3, pos.getY() + 7, pos.getZ() + 3)) {
			IBlockState blockState = world.getBlockState(p);
			if (blockState.getMaterial().isLiquid() || blockState.getBlock() == BlockRegistry.TREE_PORTAL) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the entities spawn location if necessary
	 * @param entity
	 */
	public void setDefaultPlayerSpawnLocation(BlockPos portalPos, Entity entity) {
		if (entity instanceof EntityPlayerMP == false) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) entity;
		BlockPos coords = player.getBedLocation(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);

		if (coords == null) {
			coords = PlayerRespawnHandler.getRespawnPointNearPos(this.toWorld, portalPos);
			player.setSpawnChunk(coords, true, BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
		}

		if(this.toWorld.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			NBTTagCompound dataNbt = player.getEntityData();
			NBTTagCompound persistentNbt = dataNbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

			persistentNbt.setLong(LAST_PORTAL_POS_NBT, portalPos.toLong());
			dataNbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNbt);
		}
	}

	protected void setEntityLocation(Entity entity, double x, double y, double z, float yaw, float pitch) {
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP)entity).connection.setPlayerLocation(x, y, z, yaw, pitch);
		} else {
			entity.setLocationAndAngles(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void removeStalePortalLocations(long timer) {
		//Not needed
	}
}