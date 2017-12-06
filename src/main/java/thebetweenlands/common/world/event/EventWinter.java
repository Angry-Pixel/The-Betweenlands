package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.FogState;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockSnowBetweenlands;
import thebetweenlands.common.lib.ModInfo;
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
				return provider.getEnvironmentEventRegistry().winter.isActive();
			}
		}
		return false;
	}

	public static float getSnowingStrength(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().winter.getSnowingStrength();
			}
		}
		return 0;
	}

	public float getSnowingStrength() {
		return this.snowingStrength;
	}

	@Override
	public ResourceLocation getEventName() {
		return new ResourceLocation(ModInfo.ID, "winter");
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
						this.markDirty();
					} else {
						this.snowingCooldownTicks--;
					}
				} else {
					this.snowingTicks--;
					if(this.snowingTicks <= 0) {
						this.snowingCooldownTicks = 18000 + world.rand.nextInt(18000);
						this.markDirty();
					}
				}

				if(this.snowingTicks > 0 && this.targetSnowingStrength <= 0) {
					this.targetSnowingStrength = 0.5F + world.rand.nextFloat() * 7.5F;
					this.markDirty();
				} else if(this.snowingTicks <= 0 && this.targetSnowingStrength > 0) {
					this.targetSnowingStrength = 0;
					this.markDirty();
				}

				if(world.provider instanceof WorldProviderBetweenlands && world instanceof WorldServer && world.rand.nextInt(10) == 0) {
					WorldServer worldServer = (WorldServer)world;
					for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
						Chunk chunk = iterator.next();
						int cbx = world.rand.nextInt(16);
						int cbz = world.rand.nextInt(16);
						BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(chunk.getPos().getXStart() + cbx, -999, chunk.getPos().getZStart() + cbz)).down();
						if(world.isAirBlock(pos.up()) && world.getBlockState(pos).getBlock() == BlockRegistry.SWAMP_WATER) {
							if(world.rand.nextInt(3) == 0) {
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
								if(hasSuitableNeighbourBlock) {
									world.setBlockState(pos, BlockRegistry.BLACK_ICE.getDefaultState());
								}
							}
						} else if(this.snowingTicks > 0) {
							if(world.rand.nextInt(Math.max(6 - (int)(this.getSnowingStrength() / 8.0F * 4.0F), 2)) == 0) {
								IBlockState stateAbove = world.getBlockState(pos.up());
								if(stateAbove.getBlock() == Blocks.AIR && BlockRegistry.SNOW.canPlaceBlockAt(world, pos.up())) {
									world.setBlockState(pos.up(), BlockRegistry.SNOW.getDefaultState());
								} else if(stateAbove.getBlock() instanceof BlockSnowBetweenlands) {
									int layers = stateAbove.getValue(BlockSnowBetweenlands.LAYERS);
									if(layers < 5) {
										boolean hasEnoughSnowAround = true;
										PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();
										for(EnumFacing dir : EnumFacing.HORIZONTALS) {
											checkPos.setPos(pos.getX() + dir.getFrontOffsetX(), pos.getY() + 1, pos.getZ() + dir.getFrontOffsetZ());
											if(world.isBlockLoaded(checkPos)) {
												IBlockState neighourState = world.getBlockState(checkPos);
												if(BlockRegistry.SNOW.canPlaceBlockAt(world, checkPos) && (neighourState.getBlock() != BlockRegistry.SNOW || neighourState.getValue(BlockSnowBetweenlands.LAYERS) < layers)) {
													hasEnoughSnowAround = false;
												}
											} else {
												hasEnoughSnowAround = false;
												break;
											}
										}
										checkPos.release();
										if(hasEnoughSnowAround) {
											world.setBlockState(pos.up(), stateAbove.withProperty(BlockSnowBetweenlands.LAYERS, layers + 1));
										}
									}
								}
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
			updateModelActiveState(((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().winter.isActive());
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
		if(world.provider instanceof WorldProviderBetweenlands && ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().winter.isActive()) {
			Fog targetFog = event.getAmbientFog();
			float interp = (float) MathHelper.clamp((Minecraft.getMinecraft().player.posY - WorldProviderBetweenlands.CAVE_START + 10) / 10.0F, 0.0F, 1.0F);
			float snowingStrength = ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().winter.getSnowingStrength();
			FogState state = event.getFogState();
			Fog.MutableFog newFog = new Fog.MutableFog(event.getAmbientFog());
			float newStart = Math.min(2.0F + event.getFarPlaneDistance() * 0.8F / (1.0F + snowingStrength), targetFog.getStart());
			newFog.setStart(targetFog.getStart() + (newStart - targetFog.getStart()) * interp);
			float newEnd = Math.min(8.0F + event.getFarPlaneDistance() / (1.0F + snowingStrength * 0.5F),targetFog.getEnd());
			newFog.setEnd(targetFog.getEnd() + (newEnd - targetFog.getEnd()) * interp);
			float fogBrightness = MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F);
			newFog.setColor(
					targetFog.getRed() + (fogBrightness - targetFog.getRed()) * interp, 
					targetFog.getGreen() + (fogBrightness - targetFog.getGreen()) * interp, 
					targetFog.getBlue() + (fogBrightness - targetFog.getBlue()) * interp
					);
			newFog.setColorIncrement(Math.max(targetFog.getColorIncrement(), 0.008F));
			state.setTargetFog(newFog.toImmutable());
		}
	}
}
