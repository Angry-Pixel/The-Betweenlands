package thebetweenlands.common.world.storage.location;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
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

	private final LinkedList<BlockChange> changes = new LinkedList<>();

	// TODO Save
	private BlockPos latestFrontier = null;
	private final Set<BlockPos> growthFrontier = new LinkedHashSet<>();

	private NoiseGeneratorPerlin noiseGenerator;

	private boolean isGrowing;

	private double size = 1.0D;

	private double growthSpeed = 0.05D;

	// TODO Temp for testing, or needs to be saved
	private int age = 0;

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
		for(BlockChange change : this.changes) {
			changesNbt.appendTag(change.writeToNBT());
		}
		nbt.setTag("changes", changesNbt);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.createNoiseGenerator();

		this.source = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));

		this.changes.clear();
		NBTTagList changesNbt = nbt.getTagList("changes", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < changesNbt.tagCount(); ++i) {
			BlockChange change = BlockChange.readFromNBT(changesNbt.getCompoundTagAt(i));
			if(change != null) {
				this.changes.add(change);
			}
		}
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
		if(this.age > 10000) {
			this.isGrowing = false;
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
		return world.getTileEntity(pos) == null;
	}

	protected boolean canOvergrowBlockNaturally(World world, BlockPos pos, IBlockState state) {
		return this.isOvergrownBlock(world, pos, state) || (this.canOvergrowBlock(world, pos, state) && SurfaceType.MIXED_GROUND.matches(state));
	}

	protected boolean isOvergrownBlock(World world, BlockPos pos, IBlockState state) {
		return state.getBlock() == BlockRegistry.MOULDY_SOIL;
	}

	protected IBlockState getOvergrownBlock(World world, BlockPos pos, IBlockState state) {
		if(world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() == BlockRegistry.MOULDY_SOIL) {
			return BlockRegistry.MOULD_HORN.getDefaultState();
		}
		return BlockRegistry.MOULDY_SOIL.getDefaultState();
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
		IBlockState state = world.getBlockState(pos);

		if((world.isAirBlock(pos) || this.canOvergrowBlock(world, pos, state)) && world.setBlockState(pos, newState, flags)) {
			this.changes.add(new BlockChange(pos, state, newState));
			return true;
		}

		return false;
	}

	protected boolean overgrowBlock(World world, BlockPos pos) {
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState = this.getOvergrownBlock(world, pos, oldState);

		if(world.setBlockState(pos, newState)) {
			this.changes.add(new BlockChange(pos, oldState, newState));
			return true;
		}

		return false;
	}

	protected void grow(World world) {
			BlockPos pos = this.source.down();
			IBlockState state = world.getBlockState(pos);

			if(!this.isOvergrownBlock(world, pos, state)) {
				if(this.canOvergrowBlockNaturally(world, pos, state)) {
					this.overgrowBlock(world, pos);
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
								return this.canOvergrowBlockNaturally(world, checkPos, checkState)
										&& (this.isOvergrownBlock(world, checkPos.down(), world.getBlockState(checkPos.down())) || this.canOvergrowBlockNaturally(world, checkPos.down(), world.getBlockState(checkPos.down())))
										&& (world.isAirBlock(checkPos.up()) || !world.isBlockNormalCube(checkPos.up(), false));
							}

							return false;
						};

						boolean isCurrentOvergrown = this.isOvergrownBlock(world, pos, state);

						if(!isCurrentOvergrown && canOvergrowAtPos.test(pos)) {
							blockGrown = !this.isOvergrownBlock(world, pos, state) && this.overgrowBlock(world, pos);
						} else if(canOvergrowAtPos.test(pos.up())) {
							if(this.canOvergrowBlockNaturally(world, pos, state)) {
								blockGrown |= !this.isOvergrownBlock(world, pos, state) && this.overgrowBlock(world, pos);
								pos = pos.up();
							} else if(this.canOvergrowBlockNaturally(world, prevPos.up(), world.getBlockState(prevPos.up()))) {
								blockGrown |= !this.isOvergrownBlock(world, prevPos.up(), world.getBlockState(prevPos.up())) && this.overgrowBlock(world, prevPos.up());
								pos = pos.up();
							}

							if(blockGrown) {
								this.overgrowBlock(world, pos);
							}

						} else if(!isCurrentOvergrown && canOvergrowAtPos.test(pos.down())) {
							if(this.canOvergrowBlockNaturally(world, prevPos.down(), world.getBlockState(prevPos.down()))) {
								blockGrown |= !this.isOvergrownBlock(world, prevPos.down(), world.getBlockState(prevPos.down())) && this.overgrowBlock(world, prevPos.down());
								pos = pos.down();
							} else if(this.canOvergrowBlockNaturally(world, pos, state)) {
								blockGrown |= !this.isOvergrownBlock(world, pos, state) && this.overgrowBlock(world, pos);
								pos = pos.down();
							}

							if(blockGrown) {
								this.overgrowBlock(world, pos);
							}
						}

						state = world.getBlockState(pos);
						isCurrentOvergrown = this.isOvergrownBlock(world, pos, state);

						if(isCurrentOvergrown && !blockGrown && MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()) % 5 == 0 && world.isAirBlock(pos.up())) {
							blockGrown = this.overgrowBlock(world, pos.up());
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
				BlockChange change = this.changes.removeLast();

				if(world.getBlockState(change.pos).equals(change.newState)) {
					world.setBlockState(change.pos, change.oldState);
				}

				this.markDirty();
			}
		} else {
			// TODO Become inactive
			this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
		}
	}

}
