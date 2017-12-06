package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.FogState;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.config.ConfigHandler;

public class EventWinter extends EnvironmentEvent {
	private static final long WINTER_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 11, 1, 0, 0).getTime().getTime();

	private World world;
	private World lastWorld;
	private boolean chatSent = false;

	private float snowingStrength = 0.0F;
	private float targetSnowingStrength = 0.0F;

	private int snowingCooldownTicks = 0;
	private int snowingTicks = 0;

	public EventWinter(EnvironmentEventRegistry registry) {
		super(registry);
	}

	public long getDayDiff() {
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - WINTER_DATE, TimeUnit.MILLISECONDS);
	}

	private boolean wasSet = false;

	public static boolean isFroooosty(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().WINTER.isActive();
			}
		}
		return false;
	}

	public static float getSnowingStrength(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().WINTER.getSnowingStrength();
			}
		}
		return 0;
	}

	public float getSnowingStrength() {
		return this.snowingStrength;
	}

	@Override
	public String getEventName() {
		return "Winter";
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(active && TheBetweenlands.proxy.getClientWorld() != null && (!this.isActive() || this.lastWorld != TheBetweenlands.proxy.getClientWorld()) && TheBetweenlands.proxy.getClientPlayer() != null && this.world != null && this.world.isRemote) {
			this.lastWorld = TheBetweenlands.proxy.getClientWorld();
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			player.sendMessage(new TextComponentTranslation("chat.event.winter"));
		}
		//Mark blocks in range for render update to update block textures
		if(active != this.isActive() && TheBetweenlands.proxy.getClientWorld() != null && TheBetweenlands.proxy.getClientPlayer() != null) {
			updateModelActiveState(active);

			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			int px = MathHelper.floor(player.posX) - 256;
			int py = MathHelper.floor(player.posY) - 256;
			int pz = MathHelper.floor(player.posZ) - 256;
			TheBetweenlands.proxy.getClientWorld().markBlockRangeForRenderUpdate(px, py, pz, px + 512, py + 512, pz + 512);
		}
		super.setActive(active, markDirty);
	}

	@Override
	public void update(World world) {
		super.update(world);

		this.world = world;

		if(!world.isRemote) {
			if (ConfigHandler.enableSeasonalEvents) {
				long dayDiff = this.getDayDiff();
				if (dayDiff >= 0 && dayDiff <= 31) {
					if (!this.isActive() && !this.wasSet) {
						this.setActive(true, true);
						this.wasSet = true;
					}
				} else if (this.wasSet) {
					this.wasSet = false;
					this.setActive(false, true);
				}
			}

			if(this.isActive()) {
				if(this.snowingTicks <= 0) {
					if(this.snowingCooldownTicks <= 0) {
						this.snowingTicks = 4800 + world.rand.nextInt(6000);
						this.targetSnowingStrength = 0.5F + world.rand.nextFloat() * 7.5F;
						this.markDirty();
					} else {
						this.snowingCooldownTicks--;
					}
				} else {
					this.snowingTicks--;
					if(this.snowingTicks <= 0) {
						this.snowingCooldownTicks = 18000 + world.rand.nextInt(18000);
						this.targetSnowingStrength = 0;
						this.markDirty();
					}
				}

				if(world.provider instanceof WorldProviderBetweenlands && world instanceof WorldServer && world.rand.nextInt(10) == 0) {
					WorldServer worldServer = (WorldServer)world;
					for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
						Chunk chunk = iterator.next();
						if(world.rand.nextInt(3) == 0) {
							int cbx = world.rand.nextInt(16);
							int cbz = world.rand.nextInt(16);
							BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(chunk.getPos().getXStart() + cbx, -999, chunk.getPos().getZStart() + cbz)).down();
							boolean hasSuitableNeighbourBlock = false;
							PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();
							for(EnumFacing dir : EnumFacing.HORIZONTALS) {
								checkPos.setPos(pos.getX() + dir.getFrontOffsetX(), pos.getY(), pos.getZ() + dir.getFrontOffsetZ());
								if(world.isBlockLoaded(checkPos)) {
									if(!hasSuitableNeighbourBlock) {
										IBlockState neighourState = world.getBlockState(checkPos);
										if(neighourState.getBlock() == BlockRegistry.BLACK_ICE || neighourState.isSideSolid(world, checkPos, dir.getOpposite())) {
											hasSuitableNeighbourBlock = true;
										}
									}
								} else {
									hasSuitableNeighbourBlock = false;
									break;
								}
							}
							checkPos.release();
							if(hasSuitableNeighbourBlock && world.isAirBlock(pos.up()) && world.getBlockState(pos).getBlock() == BlockRegistry.SWAMP_WATER) {
								world.setBlockState(pos, BlockRegistry.BLACK_ICE.getDefaultState());
							}
						}
					}
				}
			}
		} else {
			this.updateSnowRenderer(world);
		}

		if(!this.isActive()) {
			this.targetSnowingStrength = 0;
		}

		if(this.snowingStrength < this.targetSnowingStrength) {
			this.snowingStrength += 0.01F;
			if(this.snowingStrength > this.targetSnowingStrength) {
				this.snowingStrength = this.targetSnowingStrength;
			}
		} else if(this.snowingStrength > this.targetSnowingStrength) {
			this.snowingStrength -= 0.01F;
			if(this.snowingStrength < this.targetSnowingStrength) {
				this.snowingStrength = this.targetSnowingStrength;
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
		nbt.setBoolean("wasSet", this.wasSet);
		nbt.setInteger("snowingCooldownTicks", this.snowingCooldownTicks);
		nbt.setInteger("snowingTicks", this.snowingTicks);
		nbt.setFloat("targetSnowingStrength", this.targetSnowingStrength);
	}

	@Override
	public void loadEventData() { 
		super.loadEventData();
		NBTTagCompound nbt = this.getData();
		this.wasSet = nbt.getBoolean("wasSet");
		this.snowingCooldownTicks = nbt.getInteger("snowingCooldownTicks");
		this.snowingTicks = nbt.getInteger("snowingTicks");
		this.targetSnowingStrength = nbt.getFloat("targetSnowingStrength");
	}

	@Override
	public void loadEventPacket(NBTTagCompound nbt) {
		super.loadEventPacket(nbt);
		this.targetSnowingStrength = nbt.getFloat("targetSnowingStrength");
	}

	@Override
	public void sendEventPacket(NBTTagCompound nbt) {
		super.sendEventPacket(nbt);
		nbt.setFloat("targetSnowingStrength", this.targetSnowingStrength);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientTick(ClientTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if(world != null && world.provider instanceof WorldProviderBetweenlands) {
			updateModelActiveState(((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.isActive());
		} else {
			updateModelActiveState(false);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void updateModelActiveState(boolean active) {
		ModelRegistry.WINTER_EVENT.setActive(active);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	@SideOnly(Side.CLIENT)
	public static void onUpdateFog(UpdateFogEvent event) {
		World world = event.getWorld();
		if(world.provider instanceof WorldProviderBetweenlands && ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.isActive()) {
			float snowingStrength = ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.getSnowingStrength();
			FogState state = event.getFogState();
			Fog.MutableFog newFog = new Fog.MutableFog(state.getFog());
			newFog.setStart(Math.min(2.0F + event.getFarPlaneDistance() * 0.8F / (1.0F + snowingStrength), state.getTargetFog().getStart()));
			newFog.setEnd(Math.min(8.0F + event.getFarPlaneDistance() / (1.0F + snowingStrength * 0.5F), state.getTargetFog().getEnd()));
			newFog.setColor(
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F), 
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F), 
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F)
					);
			state.setTargetFog(newFog.toImmutable());
		}
	}
}
