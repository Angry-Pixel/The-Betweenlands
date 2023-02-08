package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class LocationSporeHive extends LocationStorage implements ITickable {

	private static class BlockChange {
		private static final IBlockStatePalette REGISTRY_BASED_PALETTE = new BlockStatePaletteRegistry();

		public final BlockPos pos;
		public final IBlockState oldState;
		public final IBlockState newState;

		public BlockChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
			this.pos = pos;
			this.oldState = oldState;
			this.newState = newState;
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("x", this.pos.getX());
			nbt.setInteger("y", this.pos.getY());
			nbt.setInteger("z", this.pos.getZ());
			nbt.setInteger("oldState", REGISTRY_BASED_PALETTE.idFor(this.oldState));
			nbt.setInteger("newState", REGISTRY_BASED_PALETTE.idFor(this.newState));
			return nbt;
		}

		@Nullable
		public static BlockChange readFromNBT(NBTTagCompound nbt) {
			BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
			IBlockState oldState = REGISTRY_BASED_PALETTE.getBlockState(nbt.getInteger("oldState"));
			IBlockState newState = REGISTRY_BASED_PALETTE.getBlockState(nbt.getInteger("newState"));
			if(oldState != null && newState != null) {
				return new BlockChange(pos, oldState, newState);
			}
			return null;
		}
	}

	private BlockPos source;

	private final LinkedList<BlockChange[]> changes = new LinkedList<>();

	private BlockPos latestFrontier = null;
	private final Set<BlockPos> growthFrontier = new LinkedHashSet<>();

	private double size = 1.0D;
	private double growthSpeed = 10.05D;

	// TODO Temp for testing, or needs to be saved
	private int age = 0;
	private boolean isGrowing;

	private NoiseGeneratorPerlin noiseGenerator;

	public LocationSporeHive(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "", EnumLocationType.SPORE_HIVE);
		this.createNoiseGenerator();
	}

	public LocationSporeHive(IWorldStorage worldStorage, StorageID id, LocalRegion region, BlockPos source) {
		super(worldStorage, id, region, "", EnumLocationType.SPORE_HIVE);
		this.source = source;
		this.createNoiseGenerator();
	}

	private void createNoiseGenerator() {
		this.noiseGenerator = new NoiseGeneratorPerlin(new Random(this.getSeed()), 4);
	}

	@Override
	public LocationStorage setSeed(long seed) {
		super.setSeed(seed);
		this.createNoiseGenerator();
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);

		nbt.setInteger("x", this.source.getX());
		nbt.setInteger("y", this.source.getY());
		nbt.setInteger("z", this.source.getZ());

		NBTTagList changesNbt = new NBTTagList();
		for(BlockChange[] changeGroup : this.changes) {
			NBTTagList changesNbt2 = new NBTTagList();
			for(BlockChange change : changeGroup) {
				changesNbt2.appendTag(change.writeToNBT());
			}
			changesNbt.appendTag(changesNbt2);
		}
		nbt.setTag("changes", changesNbt);

		if(this.latestFrontier != null) {
			nbt.setInteger("latestFrontierX", this.latestFrontier.getX());
			nbt.setInteger("latestFrontierY", this.latestFrontier.getY());
			nbt.setInteger("latestFrontierZ", this.latestFrontier.getZ());
		}

		NBTTagList growthFrontierNbt = new NBTTagList();
		for(BlockPos pos : this.growthFrontier) {
			NBTTagCompound posNbt = new NBTTagCompound();
			posNbt.setInteger("x", pos.getX());
			posNbt.setInteger("y", pos.getY());
			posNbt.setInteger("z", pos.getZ());
			growthFrontierNbt.appendTag(posNbt);
		}
		nbt.setTag("growthFrontier", growthFrontierNbt);

		nbt.setDouble("growthSpeed", this.growthSpeed);

		nbt.setDouble("hiveSize", this.size);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.createNoiseGenerator();

		this.source = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));

		this.changes.clear();
		NBTTagList changesNbt = nbt.getTagList("changes", Constants.NBT.TAG_LIST);
		for(int i = 0; i < changesNbt.tagCount(); ++i) {
			NBTTagList changesNbt2 = (NBTTagList) changesNbt.get(i);
			List<BlockChange> changeGroup = new ArrayList<>();
			for(int j = 0; j < changesNbt2.tagCount(); j++) {
				BlockChange change = BlockChange.readFromNBT(changesNbt2.getCompoundTagAt(j));
				if(change != null) {
					changeGroup.add(change);
				}
			}

			this.changes.add(changeGroup.toArray(new BlockChange[0]));
		}

		if(nbt.hasKey("latestFrontierX", Constants.NBT.TAG_INT) && nbt.hasKey("latestFrontierY", Constants.NBT.TAG_INT) && nbt.hasKey("latestFrontierZ", Constants.NBT.TAG_INT)) {
			this.latestFrontier = new BlockPos(nbt.getInteger("latestFrontierX"), nbt.getInteger("latestFrontierY"), nbt.getInteger("latestFrontierZ"));
		} else {
			this.latestFrontier = null;
		}

		this.growthFrontier.clear();
		NBTTagList growthFrontierNbt = nbt.getTagList("growthFrontier", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < growthFrontierNbt.tagCount(); ++i) {
			NBTTagCompound posNbt = growthFrontierNbt.getCompoundTagAt(i);
			this.growthFrontier.add(new BlockPos(posNbt.getInteger("x"), posNbt.getInteger("y"), posNbt.getInteger("z")));
		}

		this.growthSpeed = nbt.getDouble("growthSpeed");

		this.size = nbt.getDouble("hiveSize");
	}

	@Override
	public void update() {
		IWorldStorage storage = this.getWorldStorage();
		World world = storage.getWorld();

		if(!world.isRemote) {
			if(this.source == null || this.getBoundingBox() == null) {
				storage.getLocalStorageHandler().removeLocalStorage(this);
			} else {
				this.updateGrowth(world);
			}
		}
	}

	public boolean isGrowing() {
		return this.isGrowing;
	}

	protected boolean isHiveSource(BlockPos pos) {
		IBlockState state = this.getWorldStorage().getWorld().getBlockState(pos);
		return state.getBlock() == BlockRegistry.MOULD_HORN;
	}

	protected void updateGrowth(World world) {
		this.isGrowing = this.isHiveSource(this.source);

		this.age++;
		if(this.age > 10000000) {
			//TODO
			//this.isGrowing = false;
		}

		if(this.isGrowing) {
			int steps = MathHelper.floor(this.growthSpeed);

			for(int i = 0; i < Math.min(steps, 10); ++i) {
				this.grow(world);
			}

			if(world.rand.nextFloat() < this.growthSpeed - steps) {
				this.grow(world);
			}
		} else {
			int steps = MathHelper.floor(this.growthSpeed * 4);

			for(int i = 0; i < Math.min(steps, 10); ++i) {
				this.ungrow(world);
			}

			if(world.rand.nextFloat() < this.growthSpeed * 4 - steps) {
				this.ungrow(world);
			}
		}
	}

	protected boolean canOvergrowBlock(World world, BlockPos pos, IBlockState state) {
		// Tile entities should never be overgrown
		// because only block and metadata is saved
		return world.getTileEntity(pos) == null;
	}

	protected boolean canOvergrowBlockNaturally(World world, BlockPos pos, IBlockState state, boolean plants) {
		if(this.isOvergrownBlock(world, pos, state)) {
			return true;
		}

		if(!this.canOvergrowBlock(world, pos, state)) {
			return false;
		}

		if(plants) {
			Block plantBlock = state.getBlock();
			if(plantBlock instanceof IPlantable && this.isOvergrownBlock(world, pos.down(), world.getBlockState(pos.down()))) {
				MutableBlockPos checkPos = new MutableBlockPos();

				// Make sure plant isn't underwater
				if(state.getMaterial().isLiquid()) {
					return false;
				}
				for(EnumFacing dir : EnumFacing.HORIZONTALS) {
					checkPos.setPos(pos.getX() + dir.getXOffset(), pos.getY(), pos.getZ() + dir.getZOffset());

					if(!world.isBlockLoaded(checkPos) || world.getBlockState(checkPos).getMaterial().isLiquid()) {
						return false;
					}
				}

				// Only allow overgrowing plants if
				// they're max. 8 blocks tall
				for(int i = 0; i < 8; ++i) {
					checkPos.setPos(pos.getX(), pos.getY() + i + 1, pos.getZ());

					if(world.getBlockState(checkPos).getBlock() == plantBlock) {
						continue;
					} else {
						return true;
					}
				}

				return false;
			}

			return false;
		} else {
			return SurfaceType.MIXED_GROUND.matches(state);
		}
	}

	protected boolean isOvergrownBlock(World world, BlockPos pos, IBlockState state) {
		return this.isOvergrownBlock(world, pos, state, false) || this.isOvergrownBlock(world, pos, state, true);
	}

	protected boolean isOvergrownBlock(World world, BlockPos pos, IBlockState state, boolean plants) {
		if(plants) {
			return state.getBlock() == BlockRegistry.MOULD_HORN;
		} else {
			return state.getBlock() == BlockRegistry.MOULDY_SOIL;
		}
	}

	protected IBlockState getNaturallyOvergrownBlock(World world, BlockPos pos, IBlockState state, boolean plants) {
		if(plants) {
			return BlockRegistry.MOULD_HORN.getDefaultState();
		} else {
			return BlockRegistry.MOULDY_SOIL.getDefaultState();
		}
	}

	@Nullable
	public static LocationSporeHive getAtBlock(World world, BlockPos pos) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
		return storage.getLocalStorageHandler().getLocalStorages(LocationSporeHive.class, new AxisAlignedBB(pos).grow(1), null)
				.stream().findFirst().orElse(null);
	}

	public static boolean overgrowBlock(World world, BlockPos pos, IBlockState newState, int flags) {
		LocationSporeHive hive = getAtBlock(world, pos);

		if(hive != null) {
			return hive.overgrowBlock(pos, newState, flags);
		} else {
			return world.setBlockState(pos, newState, flags);
		}
	}

	public boolean overgrowBlock(BlockPos pos, IBlockState newState, int flags) {
		World world = this.getWorldStorage().getWorld();
		IBlockState oldState = world.getBlockState(pos);

		if((world.isAirBlock(pos) || this.canOvergrowBlock(world, pos, oldState)) && world.setBlockState(pos, newState, flags)) {
			this.changes.add(new BlockChange[] { new BlockChange(pos, oldState, newState) });
			this.markDirty();
			return true;
		}

		return false;
	}

	protected boolean overgrowBlockNaturally(World world, BlockPos pos, boolean plants) {
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState = this.getNaturallyOvergrownBlock(world, pos, oldState, plants);

		List<BlockChange> changes = new ArrayList<>();

		// Try to apply without block updates
		if(world.setBlockState(pos, newState, 0)) {
			// Just to be safe, otherwise change list could grow infinitely
			if(!this.isOvergrownBlock(world, pos, newState = world.getBlockState(pos))) {
				world.setBlockState(pos, oldState, 0);
				TheBetweenlands.logger.warn("Tried to overgrow {} at {} with {}, but {} is not a overgrown block. This shouldn't happen!", oldState, pos, newState, newState);
				return false;
			}

			changes.add(new BlockChange(pos, oldState, newState));

			if(plants) {
				// If block is plant, also remove any plants above
				// (e.g. reeds, tall grass, etc.)
				Block plantBlock = oldState.getBlock();
				if(plantBlock instanceof IPlantable && this.isOvergrownBlock(world, pos.down(), world.getBlockState(pos.down()))) {
					for(int i = 0; i < 8; ++i) {
						BlockPos offsetPos = pos.up(i + 1);
						IBlockState offsetState = world.getBlockState(offsetPos);

						if(offsetState.getBlock() == plantBlock) {
							if(world.setBlockState(offsetPos, Blocks.AIR.getDefaultState(), 2)) {
								changes.add(new BlockChange(offsetPos, offsetState, Blocks.AIR.getDefaultState()));
							}
						} else {
							break;
						}
					}
				}
			}
		}

		// Apply block updates
		for(BlockChange change : changes) {
			world.notifyBlockUpdate(change.pos, change.oldState, change.newState, 3);
		}

		this.changes.add(changes.toArray(new BlockChange[0]));
		this.markDirty();

		return !changes.isEmpty();
	}

	protected void grow(World world) {
		BlockPos pos = this.source.down();
		IBlockState state = world.getBlockState(pos);

		if(!this.isOvergrownBlock(world, pos, state)) {
			if(this.canOvergrowBlockNaturally(world, pos, state, false)) {
				this.overgrowBlockNaturally(world, pos, false);
			}
		} else {
			BlockPos frontier = null;

			this.growthFrontier.add(pos);

			if(world.rand.nextInt(this.latestFrontier == null ? 4 : 16) == 0) {
				for(int i = 0; i < this.growthFrontier.size(); ++i) {
					frontier = this.growthFrontier.stream().findFirst().get();

					if(this.isOvergrownBlock(world, frontier, world.getBlockState(frontier))) {
						pos = frontier;
						break;
					} else {
						frontier = null;

						this.growthFrontier.remove(frontier);
					}
				}
			} else if(this.latestFrontier != null) {
				pos = frontier = this.latestFrontier;
			}

			BlockPos prevPos = pos;

			int s = world.rand.nextInt(4);

			boolean blockGrown = false;

			for(int i = 0; i < 64; ++i) {
				if(i < 4) {
					pos = prevPos;
				}

				prevPos = pos;
				pos = pos.offset(EnumFacing.HORIZONTALS[i < 4 ? ((i + s) % 4) : world.rand.nextInt(4)]);

				if(!world.isBlockLoaded(pos) || !this.isInside(pos)) {
					pos = prevPos;
				} else {
					state = world.getBlockState(pos);

					Predicate<BlockPos> canOvergrowAtPos = checkPos -> {
						AxisAlignedBB aabb = this.getBoundingBox();

						double sx = aabb.maxX - aabb.minX;
						double sz = aabb.maxZ - aabb.minZ;

						double scale = (sx + sz) * 4.5D;

						double dx = (checkPos.getX() - this.source.getX()) / sx * 2.0D;
						double dz = (checkPos.getZ() - this.source.getZ()) / sz * 2.0D;

						double size = MathHelper.clamp(this.size, 0, 1);

						double value = Math.max(-30, this.noiseGenerator.getValue(dx / sx * scale, dz / sz * scale) * 8.0D) * Math.sqrt(size);
						value -= (dx * dx + dz * dz - 0.5D + (1.0D - size * size) * 0.65D) * 200.0D; 
						value += (1.0D - (dx * dx + dz * dz)) * (0.5D + (1.0D - size) * 8.5D);
						value += (1.0D - ((dx * dx + dz * dz) - (0.5D + 0.5D * size))) * 16.5D;

						if(value > 0) {
							IBlockState checkState = world.getBlockState(checkPos);
							return this.canOvergrowBlockNaturally(world, checkPos, checkState, false)
									&& (this.isOvergrownBlock(world, checkPos.down(), world.getBlockState(checkPos.down())) || this.canOvergrowBlockNaturally(world, checkPos.down(), world.getBlockState(checkPos.down()), false))
									&& (world.isAirBlock(checkPos.up()) || !world.isBlockNormalCube(checkPos.up(), false));
						}

						return false;
					};

					boolean isCurrentOvergrown = this.isOvergrownBlock(world, pos, state);

					if(!isCurrentOvergrown && canOvergrowAtPos.test(pos)) {
						blockGrown = !this.isOvergrownBlock(world, pos, state) && this.overgrowBlockNaturally(world, pos, false);
					} else if(canOvergrowAtPos.test(pos.up())) {
						if(this.canOvergrowBlockNaturally(world, pos, state, false)) {
							blockGrown |= !this.isOvergrownBlock(world, pos, state) && this.overgrowBlockNaturally(world, pos, false);
							pos = pos.up();
						} else if(this.canOvergrowBlockNaturally(world, prevPos.up(), world.getBlockState(prevPos.up()), false)) {
							blockGrown |= !this.isOvergrownBlock(world, prevPos.up(), world.getBlockState(prevPos.up())) && this.overgrowBlockNaturally(world, prevPos.up(), false);
							pos = pos.up();
						}

						if(blockGrown) {
							this.overgrowBlockNaturally(world, pos, false);
						}

					} else if(!isCurrentOvergrown && canOvergrowAtPos.test(pos.down())) {
						if(this.canOvergrowBlockNaturally(world, prevPos.down(), world.getBlockState(prevPos.down()), false)) {
							blockGrown |= !this.isOvergrownBlock(world, prevPos.down(), world.getBlockState(prevPos.down())) && this.overgrowBlockNaturally(world, prevPos.down(), false);
							pos = pos.down();
						} else if(this.canOvergrowBlockNaturally(world, pos, state, false)) {
							blockGrown |= !this.isOvergrownBlock(world, pos, state) && this.overgrowBlockNaturally(world, pos, false);
							pos = pos.down();
						}

						if(blockGrown) {
							this.overgrowBlockNaturally(world, pos, false);
						}
					}

					state = world.getBlockState(pos);
					isCurrentOvergrown = this.isOvergrownBlock(world, pos, state, false);

					BlockPos posUp = pos.up();
					IBlockState stateUp = world.getBlockState(posUp);

					if(isCurrentOvergrown && !blockGrown && !this.isOvergrownBlock(world, posUp, stateUp) &&
							((world.isAirBlock(posUp) && MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()) % 10 == 0) || this.canOvergrowBlockNaturally(world, posUp, stateUp, true))) {
						blockGrown |= this.overgrowBlockNaturally(world, posUp, true);
					}

					if(blockGrown) {
						if(this.growthFrontier.size() < 128) {
							this.growthFrontier.add(pos);
							this.latestFrontier = frontier;
						}

						boolean remove = true;

						for(EnumFacing offset : EnumFacing.HORIZONTALS) {
							if(!this.isOvergrownBlock(world, prevPos.offset(offset), world.getBlockState(prevPos.offset(offset)))) {
								remove = false;
								break;
							}
						}

						if(remove) {
							this.growthFrontier.remove(prevPos);
						}

						break;
					} else if(!isCurrentOvergrown) {
						pos = prevPos;
					} else if(this.growthFrontier.size() < 32 && world.rand.nextInt(16) == 0) {
						this.growthFrontier.add(pos);
						this.growthFrontier.add(prevPos);
					}
				}

				if(frontier != null) {
					if(!blockGrown) {
						this.growthFrontier.remove(frontier);
					}
				}
			}
		}

		this.markDirty();
	}

	protected void ungrow(World world) {
		if(!this.changes.isEmpty()) {
			if(this.age % 1 == 0) {
				BlockChange[] changeGroup = this.changes.removeLast();

				List<BlockChange> appliedChanges = new ArrayList<>();

				// Apply without block updates
				for(int i = changeGroup.length - 1; i >= 0; --i) {
					BlockChange change = changeGroup[i];
					if(world.getBlockState(change.pos).equals(change.newState)) {
						world.setBlockState(change.pos, change.oldState, 0);
						appliedChanges.add(change);
					}
				}

				// Apply block updates
				for(BlockChange change : appliedChanges) {
					world.notifyBlockUpdate(change.pos, change.newState, change.oldState, 3);
				}

				this.markDirty();
			}
		} else {
			// TODO Become inactive
			this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
		}
	}

}
