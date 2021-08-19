package thebetweenlands.common.tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityCrabPot extends TileEntityBasicInventory implements ITickable {
	private boolean active;
	public int fallCounter = 16;
	public int fallCounterPrev;
	private int horizontalIndex = 0;
	public int prevAnimationTicks;
	public int animationTicks;
	public boolean animate = false;
	private EntityPlayer placer;
	private UUID placerUUID;
	private int catchTimer;
	private int catchTimerMax;
	private long lastCheckedTime;


	public TileEntityCrabPot() {
		super(1, "container.bl.crab_pot");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);

		this.updatePlacerFromUUID();
	}

	@Override
	public void update() {
		if (world.isRemote) {
			fallCounterPrev = fallCounter;
			if (!hasBaitItem()) {
				if (fallCounter > 0) {
					fallCounter--;
				}
				if (fallCounter <= 0) {
					fallCounter = 0;
				}
			}
			if (hasBaitItem() && fallCounter != 32) {
				fallCounter = 32;
			}

			prevAnimationTicks = animationTicks;
			
			if(animate) {
				animationTicks++;
			}
			if(!animate && animationTicks > 0) {
				animationTicks = 0;
			}
		}

		if(!world.isRemote) {
			if(this.lastCheckedTime == 0 || this.catchTimerMax == 0) {
				this.resetCatchTimer();
			}

			// because the player is always null unless the world is loaded but block NBT is loaded before grrrrr
			if(placerUUID != null && getPlacer() == null && world.getTotalWorldTime() % 20 == 0) {
				if(updatePlacerFromUUID()) {
					markForUpdate();
				}
			}

			if(hasBaitItem() && !active) {
				active = true;
				markDirty();
			}

			if(!hasBaitItem() && active) {
				active = false;
				markDirty();
			}

			if(active && this.getStackInSlot(0).getItem() instanceof ItemMob == false) {
				if(world.getTotalWorldTime() % 20 == 0) {
					EntitySiltCrab entity = null;

					this.updateCatchTimer();

					int remainingCatchTicks = this.getRemainingCatchTicks();

					if(remainingCatchTicks <= 0 && this.world.getGameRules().getBoolean("doMobSpawning") && this.world.getClosestPlayer(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, 32.0D, false) == null && this.world.canSeeSky(this.pos.up())) {
						int checks = 64;
						int spawnableBlocks = 0;

						Map<Biome, Boolean> spawnableBiomes = new HashMap<>();

						MutableBlockPos checkPos = new MutableBlockPos();
						for(int i = 0; i < checks; i++) {
							checkPos.setPos(this.pos.getX() + this.world.rand.nextInt(13) - 6, this.pos.getY() + this.world.rand.nextInt(5) - 1, this.pos.getZ() + this.world.rand.nextInt(13) - 6);

							if(this.world.getBlockState(checkPos).getMaterial() == Material.WATER && this.world.canSeeSky(checkPos)) {
								Biome biome = this.world.getBiome(checkPos);

								if(spawnableBiomes.containsKey(biome)) {
									if(spawnableBiomes.get(biome)) {
										spawnableBlocks++;
									}
								} else {
									boolean biomeSpawnable = false;

									if(biome instanceof ICustomSpawnEntriesProvider) {
										for(ICustomSpawnEntry spawnEntry : ((ICustomSpawnEntriesProvider) biome).getCustomSpawnEntries()) {
											if(EntitySiltCrab.class.isAssignableFrom(spawnEntry.getEntityType())) {
												biomeSpawnable = true;
												break;
											}
										}
									}

									spawnableBiomes.put(biome, biomeSpawnable);

									if(biomeSpawnable) {
										spawnableBlocks++;
									}
								}
							}
						}

						float probability = Math.min(1, (float)Math.pow((float)spawnableBlocks / (float)checks * 2.0f, 2.0f) * 2.0f);

						if(this.world.rand.nextFloat() <= probability) {
							entity = this.createRandomCatch(spawnableBiomes.entrySet().stream().filter(e -> e.getValue()).map(e -> e.getKey()).collect(Collectors.toSet()));
						}

						this.resetCatchTimer();
					}

					if(entity == null) {
						lureCloseCrab();
						entity = this.checkCatch();
					}

					if(entity != null) {
						ItemMob itemMob = entity.getCrabPotItem();

						if(itemMob != null) {
							ItemStack stack = itemMob.capture(entity);

							if(!stack.isEmpty()) {
								entity.setDropItemsWhenDead(false);
								entity.setDead();

								setInventorySlotContents(0, stack);

								EntityPlayer placer = getPlacer();
								if(placer == null) {
									updatePlacerFromUUID();
									placer = getPlacer();
								}

								markDirty();

								if((hasSiltCrab() || hasBubblerCrab()) && placer instanceof EntityPlayerMP) {
									AdvancementCriterionRegistry.CRAB_POT.trigger((EntityPlayerMP) placer);
								}
							}
						}
					}

					//Mark dirty without markForUpdate()
					super.markDirty();
				}
			} else {
				this.resetCatchTimer();
			}

			if (getWorld().getBlockState(pos.down()).getBlock() != BlockRegistry.CRAB_POT_FILTER && animate) {
				animate = false;
				markForUpdate();
			}
		}
	}

	public void resetCatchTimer() {
		this.catchTimerMax = 12000 + this.world.rand.nextInt(12000);
		this.catchTimer = 0;
		this.lastCheckedTime = this.world.getTotalWorldTime();
	}

	private void updateCatchTimer() {
		this.catchTimer = MathHelper.clamp(this.catchTimer + (int)(this.world.getTotalWorldTime() - this.lastCheckedTime), 0, this.catchTimerMax);
		this.lastCheckedTime = this.world.getTotalWorldTime();
	}

	public int getRemainingCatchTicks() {
		return MathHelper.clamp(this.catchTimerMax - this.catchTimer, 0, this.catchTimerMax);
	}

	private boolean updatePlacerFromUUID() {
		if(placerUUID != null) {
			EntityPlayer player = this.world.getPlayerEntityByUUID(placerUUID);
			if(player != null && player != getPlacer()) {
				setPlacer(player);
				return true;
			}
		}
		return false;
	}

	@Nullable
	private EntitySiltCrab createRandomCatch(Set<Biome> biomes) {
		EntitySiltCrab crab = null;
		switch(this.world.rand.nextInt(2)) {
		case 0:
			crab = new EntitySiltCrab(this.world);
			break;
		case 1:
			crab = new EntityBubblerCrab(this.world);
			break;
		}
		if(crab != null) {
			crab.randomizeSiltCrabProperties();
		}
		return crab;
	}

	@Nullable
	private EntitySiltCrab checkCatch() {
		List<EntityLiving> list = getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.up()).grow(-0.25D, 0F, -0.25D), e -> e instanceof EntitySiltCrab);
		if (!list.isEmpty()) {
			return (EntitySiltCrab) list.get(this.world.rand.nextInt(list.size()));
		}
		return null;
	}

	public void lureCloseCrab() {
		List<EntityLiving> list = getWorld().getEntitiesWithinAABB(EntityLiving.class, extendRangeBox(), e -> e.isInWater() && e instanceof EntitySiltCrab);
		if (!list.isEmpty()) {
			((EntitySiltCrab) list.get(this.world.rand.nextInt(list.size()))).lureToCrabPot(this.pos);
		}
	}

	public AxisAlignedBB extendRangeBox() {
		return  new AxisAlignedBB(pos).grow(12D, 5D, 12D);
	}

	public boolean hasBaitItem() {
		ItemStack baitItem = getStackInSlot(0);
		return !baitItem.isEmpty() && baitItem.getItem() == EnumItemMisc.ANADIA_REMAINS.getItem();
	}

	public boolean hasSiltCrab() {
		ItemStack crabItem = getStackInSlot(0);
		return !crabItem.isEmpty() && crabItem.getItem() == ItemRegistry.SILT_CRAB;
	}

	public boolean hasBubblerCrab() {
		ItemStack crabItem = getStackInSlot(0);
		return !crabItem.isEmpty() && crabItem.getItem() == ItemRegistry.BUBBLER_CRAB;
	}

	public void markForUpdate() {
		IBlockState state = this.getWorld().getBlockState(this.getPos());
		this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 2);
	}

	public Entity getEntity() {
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemMob && ((ItemMob) stack.getItem()).hasEntityData(stack)) {
			return ((ItemMob) stack.getItem()).createCapturedEntity(this.world, 0, 0, 0, stack, false);
		}
		return null;
	}

	public void setRotation(int horizontalIndexIn) {
		horizontalIndex = horizontalIndexIn;
	}

	public int getRotation() {
		return horizontalIndex;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		markForUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		active = nbt.getBoolean("active");
		animate = nbt.getBoolean("animate");
		setRotation(nbt.getInteger("horizontalIndex"));

		if (nbt.hasKey("OwnerUUID", 8)) {
			placerUUID = nbt.getUniqueId("OwnerUUID");
		}

		catchTimer = nbt.getInteger("catchTimer");
		catchTimerMax = nbt.getInteger("catchTimerMax");
		lastCheckedTime = nbt.getLong("lastLoadedTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean("active", active);
		nbt.setInteger("horizontalIndex", getRotation());
		nbt.setBoolean("animate", animate);

		EntityPlayer placer = getPlacer();
		if (placer != null) {
			nbt.setUniqueId("OwnerUUID", placer.getUniqueID());
		}

		nbt.setInteger("catchTimer", catchTimer);
		nbt.setInteger("catchTimerMax", catchTimerMax);
		nbt.setLong("lastLoadedTime", lastCheckedTime);

		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	private EntityPlayer getPlacer() {
		return this.placer;
	}

	public void setPlacer(EntityPlayer player) {
		this.placer = player;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return side != EnumFacing.DOWN && slot == 0 && this.getStackInSlot(slot).isEmpty() && EnumItemMisc.ANADIA_REMAINS.isItemOf(stack) && stack.getCount() == 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		return side == EnumFacing.DOWN && slot == 0 && !EnumItemMisc.ANADIA_REMAINS.isItemOf(stack);
	}

}
