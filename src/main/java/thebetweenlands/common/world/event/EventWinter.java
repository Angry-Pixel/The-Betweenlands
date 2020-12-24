package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
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
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.BlockPresent;
import thebetweenlands.common.block.misc.BlockBauble;
import thebetweenlands.common.block.terrain.BlockSnowBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.common.tile.TileEntityPresent;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventWinter extends SeasonalEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "winter");

	private static final long WINTER_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 11, 1, 0, 0).getTime().getTime();

	public EventWinter(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public long getStartDateInMs() {
		return WINTER_DATE;
	}

	@Override
	public int getDurationInDays() {
		return 31;
	}

	public static boolean isFroooosty(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().winter.isActive();
			}
		}
		return false;
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active) {
		//Mark blocks in range for render update to update block textures
		if(active != this.isActive() && TheBetweenlands.proxy.getClientWorld() != null && TheBetweenlands.proxy.getClientPlayer() != null) {
			updateModelActiveState(active);

			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			int px = MathHelper.floor(player.posX) - 256;
			int py = MathHelper.floor(player.posY) - 256;
			int pz = MathHelper.floor(player.posZ) - 256;
			TheBetweenlands.proxy.getClientWorld().markBlockRangeForRenderUpdate(px, py, pz, px + 512, py + 512, pz + 512);
		}

		super.setActive(active);
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(!world.isRemote && this.isActive()) {
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
								checkPos.setPos(pos.getX() + dir.getXOffset(), pos.getY(), pos.getZ() + dir.getZOffset());
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
					} else if(world.rand.nextInt(3000) == 0) {
						IBlockState state = world.getBlockState(pos);
						if(state.getBlock().isLeaves(state, world, pos)) {
							BlockPos offsetPos = pos;
							
							for(int i = 0; i < 6; i++) {
								offsetPos = offsetPos.down();
								state = world.getBlockState(offsetPos);
								
								if(!state.getBlock().isLeaves(state, world, offsetPos)) {
									if(world.isAirBlock(offsetPos) && world.getLightFor(EnumSkyBlock.BLOCK, offsetPos) == 0 &&
											(world.isSideSolid(offsetPos.up(), EnumFacing.DOWN) || world.getBlockState(offsetPos.up()).getBlockFaceShape(world, offsetPos.up(), EnumFacing.DOWN) != BlockFaceShape.UNDEFINED)) {
										world.setBlockState(offsetPos, BlockRegistry.BAUBLE.getDefaultState().withProperty(BlockBauble.DIAGONAL, world.rand.nextBoolean()).withProperty(BlockBauble.COLOR, world.rand.nextInt(8)));
									}
									
									break;
								}
							}
						}
					}

					if(world.rand.nextInt(3000) == 0 && world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64.0D, false) == null) {
						if(world.isSideSolid(pos, EnumFacing.UP)) {
							IBlockState stateAbove = world.getBlockState(pos.up());
							if(stateAbove.getBlock() == Blocks.AIR || (stateAbove.getBlock() instanceof BlockSnowBetweenlands && stateAbove.getValue(BlockSnowBetweenlands.LAYERS) <= 5)) {
								world.setBlockState(pos.up(), BlockRegistry.PRESENT.getDefaultState().withProperty(BlockPresent.COLOR, EnumDyeColor.values()[world.rand.nextInt(EnumDyeColor.values().length)]));
								TileEntityPresent tile = BlockPresent.getTileEntity(world, pos.up());
								if (tile != null) {
									tile.setLootTable(LootTableRegistry.PRESENT, world.rand.nextLong());
									tile.markDirty();
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void showStatusMessage(EntityPlayer player) {
		player.sendStatusMessage(new TextComponentTranslation("chat.event.winter"), true);
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
			float snowingStrength = ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().snowfall.getSnowingStrength();
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
