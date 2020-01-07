package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.storage.IOfflinePlayerDataHandler;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.world.storage.OfflinePlayerHandlerImpl;

public class ItemRingOfGathering extends ItemRing {
	public static final String NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY = "GatheringRingEquipped";
	public static final String NBT_OFFLINE_PLAYER_DATA_LIST_KEY = "GatheringRingList";

	public static final String NBT_SYNC_COUNT_KEY = "GatheringRingCountSync";
	public static final String NBT_LAST_USER_UUID_KEY = "LastUserUuid";

	public static final String NBT_LAST_TELEPORT_TICKS = "LastTeleportTicks";

	@Nullable
	public UUID getLastUserUuid(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasUniqueId(NBT_LAST_USER_UUID_KEY)) {
			return nbt.getUniqueId(NBT_LAST_USER_UUID_KEY);
		}
		return null;
	}

	public boolean isRingEquipped(UUID playerUuid) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt != null && nbt.hasKey(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY, Constants.NBT.TAG_BYTE)) {
				return nbt.getBoolean(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY);
			}
		}
		return false;
	}

	public boolean setRingEquipped(UUID playerUuid, boolean equipped) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setBoolean(NBT_OFFLINE_PLAYER_DATA_EQUIPMENT_KEY, equipped);
			handler.setOfflinePlayerData(playerUuid, nbt);
			return true;
		}
		return false;
	}

	public boolean hasSpace(UUID playerUuid) {
		return this.getEntryCount(playerUuid) < this.getCapacity();
	}

	public int getEntryCount(UUID playerUuid) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);

			if(nbt != null && nbt.hasKey(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_LIST)) {
				NBTTagList list = nbt.getTagList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

				return list.tagCount();
			}
		}

		return 0;
	}

	public static class RingEntityEntry {
		public final ResourceLocation id;
		public final NBTTagCompound nbt;
		public final boolean respawnByAnimator;
		public final int animatorLifeCrystalCost;
		public final int animatorSulfurCost;

		public RingEntityEntry(ResourceLocation id, NBTTagCompound nbt) {
			this.id = id;
			this.nbt = nbt;
			this.respawnByAnimator = false;
			this.animatorLifeCrystalCost = 0;
			this.animatorSulfurCost = 0;
		}

		public RingEntityEntry(ResourceLocation id, NBTTagCompound nbt, int animatorLifeCrystalCost, int animatorSulfurCost) {
			this.id = id;
			this.nbt = nbt;
			this.respawnByAnimator = true;
			this.animatorLifeCrystalCost = animatorLifeCrystalCost;
			this.animatorSulfurCost = animatorSulfurCost;
		}
	}

	public boolean addEntry(UUID playerUuid, RingEntityEntry entry) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}

			NBTTagList list = nbt.getTagList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

			NBTTagCompound entryNbt = new NBTTagCompound();
			entryNbt.setString("id", entry.id.toString());
			entryNbt.setTag("data", entry.nbt);

			entryNbt.setBoolean("respawnByAnimator", entry.respawnByAnimator);
			if(entry.respawnByAnimator) {
				entryNbt.setInteger("animatorLifeCrystalCost", entry.animatorLifeCrystalCost);
				entryNbt.setInteger("animatorSulfurCost", entry.animatorSulfurCost);
			}

			list.appendTag(entryNbt);

			nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

			handler.setOfflinePlayerData(playerUuid, nbt);

			return true;
		}

		return false;
	}

	@Nullable
	public RingEntityEntry getEntry(UUID playerUuid, boolean fromAnimator, Predicate<RingEntityEntry> predicate, boolean remove) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);

			if(nbt != null && nbt.hasKey(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_LIST)) {
				NBTTagList list = nbt.getTagList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

				if(list.tagCount() > 0) {
					for(int i = 0; i < list.tagCount(); i++) {
						NBTTagCompound entryNbt = list.getCompoundTagAt(i);

						if(entryNbt.hasKey("id", Constants.NBT.TAG_STRING) && entryNbt.getBoolean("respawnByAnimator") == fromAnimator) {
							RingEntityEntry entry;

							if(fromAnimator) {
								entry = new RingEntityEntry(new ResourceLocation(entryNbt.getString("id")), entryNbt.getCompoundTag("data"), entryNbt.getInteger("animatorLifeCrystalCost"), entryNbt.getInteger("animatorSulfurCost"));
							} else {
								entry = new RingEntityEntry(new ResourceLocation(entryNbt.getString("id")), entryNbt.getCompoundTag("data"));
							}

							if(predicate.test(entry)) {
								if(remove) {
									list.removeTag(i);

									nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

									handler.setOfflinePlayerData(playerUuid, nbt);
								}

								return entry;
							}
						}
					}
				}
			}
		}

		return null;
	}

	@Nullable
	public Entity returnEntityFromRing(double x, double y, double z, Entity user, UUID playerUuid, boolean fromAnimator) {
		List<Entity> returnedEntity = new ArrayList<>();

		this.getEntry(playerUuid, fromAnimator, entry -> {
			Entity entity = EntityList.createEntityByIDFromName(entry.id, user.world);

			entity.setLocationAndAngles(x, y, z, user.world.rand.nextFloat() * 360, 0);

			if(entity instanceof IRingOfGatheringMinion && ((IRingOfGatheringMinion) entity).returnFromRing(user, entry.nbt)) {
				returnedEntity.add(entity);
				return true;
			}

			return false;
		}, true);

		return returnedEntity.isEmpty() ? null : returnedEntity.get(0);
	}

	public int getCapacity() {
		return 6;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ring.gathering.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.bl.ring.gathering", KeyBindRegistry.RADIAL_MENU.getDisplayName(), KeyBindRegistry.USE_RING.getDisplayName(), KeyBindRegistry.USE_SECONDARY_RING.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.bl.press.shift"));
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			return nbt.getByte(NBT_SYNC_COUNT_KEY) > 0;
		}
		return false;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			return 1 - nbt.getByte(NBT_SYNC_COUNT_KEY) / (float)this.getCapacity();
		}
		return 1;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		int count = 0;
		if(nbt != null && nbt.hasKey(NBT_SYNC_COUNT_KEY, Constants.NBT.TAG_BYTE)) {
			count = nbt.getByte(NBT_SYNC_COUNT_KEY);
		}
		int maxCount = this.getCapacity();
		if(count == maxCount - 1) {
			return 0xFFAA00;
		} else if(count == maxCount) {
			return 0xFF2020;
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
		return stack.getTagCompound() == null || stack.getTagCompound().getByte(NBT_SYNC_COUNT_KEY) <= 0 || player.isSneaking();
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
		return player == target;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof EntityPlayer) {
			this.setRingEquipped(entity.getUniqueID(), true);
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity instanceof EntityPlayer) {
			this.setRingEquipped(entity.getUniqueID(), false);
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity.ticksExisted % 5 == 0) {
			this.updateStackEntryCount(entity.world, stack, entity);
		}

		this.updateLastUserUuid(entity.world, stack, entity);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if(entityIn.ticksExisted % 5 == 0) {
			this.updateStackEntryCount(worldIn, stack, entityIn);
		}

		this.updateLastUserUuid(worldIn, stack, entityIn);
	}

	protected void updateLastUserUuid(World world, ItemStack stack, Entity entity) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			stack.setTagCompound(nbt = new NBTTagCompound());
		}
		nbt.setUniqueId(NBT_LAST_USER_UUID_KEY, entity.getUniqueID());
		stack.setTagCompound(nbt);
	}

	protected void updateStackEntryCount(World worldIn, ItemStack stack, Entity entityIn) {
		if(!worldIn.isRemote && entityIn instanceof EntityPlayer) {
			int count = this.getEntryCount(entityIn.getUniqueID());

			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null) {
				stack.setTagCompound(nbt = new NBTTagCompound());
			}

			int syncCounter = nbt.getByte(NBT_SYNC_COUNT_KEY);

			if(syncCounter != count) {
				nbt.setByte(NBT_SYNC_COUNT_KEY, (byte) count);
				stack.setTagCompound(nbt);
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		if(this.activateEffect(playerIn, stack)) {
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onKeybindState(EntityPlayer player, ItemStack stack, IInventory inventory, boolean active) {
		if(active) {
			this.activateEffect(player, stack);
		}
	}

	protected boolean activateEffect(EntityPlayer player, ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			if(nbt.getByte(NBT_SYNC_COUNT_KEY) > 0) {
				if(this.getEntry(player.getUniqueID(), false, e -> true, false) != null) {
					if(!player.world.isRemote) {
						if(removeXp(player, 15) >= 15) {
							if(this.returnEntityFromRing(player.posX, player.posY, player.posZ, player, player.getUniqueID(), false) != null) {
								return true;
							}
						} else {
							player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_gathering.not_enough_xp"), true);
						}
					}
				} else {
					if(!player.world.isRemote && this.getEntryCount(player.getUniqueID()) > 0) {
						player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_gathering.animator"), true);
					}
				}
			}

			boolean teleported = false;

			boolean missingTeleportXp = false;

			//Teleport loaded pets back to player
			long lastTeleportTicks = nbt.getLong(NBT_LAST_TELEPORT_TICKS);
			if(Math.abs(player.world.getTotalWorldTime() - lastTeleportTicks) > 20) {
				nbt.setLong(NBT_LAST_TELEPORT_TICKS, player.world.getTotalWorldTime());

				UUID thisPlayerUuid = player.getUniqueID();

				List<Entity> ownedEntities = player.world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().grow(256), e -> e instanceof IRingOfGatheringMinion);
				for(Entity ownedEntity : ownedEntities) {
					IRingOfGatheringMinion minion = (IRingOfGatheringMinion) ownedEntity;

					UUID playerUuid = minion.getRingOwnerId();

					if(playerUuid != null && playerUuid.equals(thisPlayerUuid)) {
						if(minion.shouldReturnOnCall()) {
							if(removeXp(player, 5) >= 5) {
								minion.returnToCall(player);
								teleported = true;
							} else {
								missingTeleportXp = true;
							}
						}
					}
				}
			}

			if(missingTeleportXp) {
				player.sendStatusMessage(new TextComponentTranslation("chat.ring_of_gathering.not_enough_xp"), true);
			}

			if(teleported) {
				return true;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();

		if(!entity.world.isRemote && entity instanceof IRingOfGatheringMinion) {
			IRingOfGatheringMinion minion = (IRingOfGatheringMinion) entity;

			UUID playerUuid = minion.getRingOwnerId();

			if(playerUuid != null && minion.shouldReturnOnDeath(entity.world.getPlayerEntityByUUID(playerUuid) != null) && returnMinionToRing(minion)) {
				//Don't spawn loot etc.
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if(!event.getWorld().isRemote) {
			Chunk chunk = event.getChunk();

			//Return minions that were lost due to chunk unloading back to ring

			List<Entity> entities = new ArrayList<>();

			int bx = chunk.x * 16;
			int bz = chunk.z * 16;

			chunk.getEntitiesOfTypeWithinAABB(Entity.class, new AxisAlignedBB(bx - 1, -999999, bz - 1, bx + 17, 999999, bz + 17), entities, e -> e instanceof IRingOfGatheringMinion);

			for(Entity entity : entities) {
				IRingOfGatheringMinion minion = (IRingOfGatheringMinion) entity;

				UUID playerUuid = minion.getRingOwnerId();

				if(playerUuid != null && minion.shouldReturnOnUnload(entity.world.getPlayerEntityByUUID(playerUuid) != null)) {
					returnMinionToRing(minion);
				}
			}
		}
	}

	private static boolean returnMinionToRing(IRingOfGatheringMinion minion) {
		Entity entity = (Entity) minion;

		if(!entity.world.isRemote) {
			ResourceLocation id = EntityList.getKey(entity);

			if(id != null) {
				UUID playerUuid = minion.getRingOwnerId();

				if(playerUuid != null && ItemRegistry.RING_OF_GATHERING.isRingEquipped(playerUuid) && ItemRegistry.RING_OF_GATHERING.hasSpace(playerUuid)) {
					NBTTagCompound entityNbt = minion.returnToRing(playerUuid);

					RingEntityEntry entry;

					if(minion.isRespawnedByAnimator()) {
						entry = new RingEntityEntry(id, entityNbt, minion.getAnimatorLifeCrystalCost(), minion.getAnimatorSulfurCost());
					} else {
						entry = new RingEntityEntry(id, entityNbt);
					}

					if(ItemRegistry.RING_OF_GATHERING.addEntry(playerUuid, entry)) {
						//Minion was successfully returned to ring and can be removed without dropping anything
						entity.removePassengers();
						entity.setDropItemsWhenDead(false);
						entity.setDead();

						return true;
					}
				}
			}
		}

		return false;
	}
}
