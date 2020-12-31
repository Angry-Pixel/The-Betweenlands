package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.block.terrain.BlockSnowBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventSnowfall extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "snowfall");

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { new ResourceLocation("thebetweenlands:textures/events/snowfall.png") };
	
	private float snowingStrength = 0.0F;
	protected static final DataParameter<Float> TARGET_SNOWING_STRENGTH = GenericDataManager.createKey(EventSnowfall.class, DataSerializers.FLOAT);

	public EventSnowfall(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	protected void initDataParameters() {
		super.initDataParameters();
		this.dataManager.register(TARGET_SNOWING_STRENGTH, 0.0F);
	}

	public static float getSnowingStrength(World world) {
		if (world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if (provider != null) {
				return provider.getEnvironmentEventRegistry().snowfall.getSnowingStrength();
			}
		}
		return 0;
	}

	public float getSnowingStrength() {
		return this.snowingStrength;
	}

	public boolean isSnowing() {
		return this.snowingStrength > 0;
	}

	@Override
	protected boolean canActivate() {
		return this.getRegistry().winter.isActive();
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active) {
		if(!this.getWorld().isRemote) {
			if (active) {
				this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.5F + this.getWorld().rand.nextFloat() * 7.5F);
			} else {
				this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.0F);
			}
		}
		super.setActive(active);
	}

	@Override
	public void update(World world) {
		super.update(world);

		if (!world.isRemote) {
			if(this.isActive() && !this.getRegistry().winter.isActive()) {
				this.setActive(false);
			}

			if (this.isActive() && world.provider instanceof WorldProviderBetweenlands && world instanceof WorldServer && world.rand.nextInt(5) == 0) {
				WorldServer worldServer = (WorldServer) world;
				for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();) {
					Chunk chunk = iterator.next();
					int cbx = world.rand.nextInt(16);
					int cbz = world.rand.nextInt(16);
					BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(chunk.getPos().getXStart() + cbx, -999, chunk.getPos().getZStart() + cbz)).down();
					if (world.rand.nextInt(Math.max(20 - (int) (this.getSnowingStrength() / 8.0F * 18.0F), 2)) == 0) {
						IBlockState stateAbove = world.getBlockState(pos.up());
						if (stateAbove.getBlock() == Blocks.AIR && BlockRegistry.SNOW.canPlaceBlockAt(world, pos.up())) {
							world.setBlockState(pos.up(), BlockRegistry.SNOW.getDefaultState());
						} else if (stateAbove.getBlock() instanceof BlockSnowBetweenlands) {
							int layers = stateAbove.getValue(BlockSnowBetweenlands.LAYERS);
							if (layers < 5) {
								boolean hasEnoughSnowAround = true;
								PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();
								for (EnumFacing dir : EnumFacing.HORIZONTALS) {
									checkPos.setPos(pos.getX() + dir.getXOffset(), pos.getY() + 1, pos.getZ() + dir.getZOffset());
									if (world.isBlockLoaded(checkPos)) {
										IBlockState neighourState = world.getBlockState(checkPos);
										if (BlockRegistry.SNOW.canPlaceBlockAt(world, checkPos)
												&& (neighourState.getBlock() != BlockRegistry.SNOW || neighourState.getValue(BlockSnowBetweenlands.LAYERS) < layers)) {
											hasEnoughSnowAround = false;
										}
									} else {
										hasEnoughSnowAround = false;
										break;
									}
								}
								checkPos.release();
								if (hasEnoughSnowAround) {
									world.setBlockState(pos.up(), stateAbove.withProperty(BlockSnowBetweenlands.LAYERS, layers + 1));
								}
							}
						}
					}
				}
			}
		} else {
			this.updateSnowRenderer(world);
		}

		if (!this.isActive()) {
			this.dataManager.set(TARGET_SNOWING_STRENGTH, 0.0F);
		}

		float targetSnowingStrength = this.dataManager.get(TARGET_SNOWING_STRENGTH);

		if (this.snowingStrength < targetSnowingStrength) {
			this.snowingStrength = this.snowingStrength + 0.01F;
			if (this.snowingStrength > targetSnowingStrength) {
				this.snowingStrength = targetSnowingStrength;
			}
		} else if (this.snowingStrength > targetSnowingStrength) {
			this.snowingStrength = this.snowingStrength - 0.01F;
			if (this.snowingStrength < targetSnowingStrength) {
				this.snowingStrength = targetSnowingStrength;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateSnowRenderer(World world) {
		BLSnowRenderer.INSTANCE.update(world);
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		NBTTagCompound nbt = this.getData();
		nbt.setFloat("targetSnowingStrength", this.dataManager.get(TARGET_SNOWING_STRENGTH));
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.dataManager.set(TARGET_SNOWING_STRENGTH, nbt.getFloat("targetSnowingStrength"));
	}

	@Override
	public int getOffTime(Random rnd) {
		return 18000 + rnd.nextInt(18000);
	}

	@Override
	public int getOnTime(Random rnd) {
		return 4800 + rnd.nextInt(6000);
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_SNOWFALL;
	}
}
