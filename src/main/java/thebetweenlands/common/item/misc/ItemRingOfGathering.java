package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	public boolean addEntry(UUID playerUuid, ResourceLocation entityRegName, NBTTagCompound entityNbt) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}

			NBTTagList list = nbt.getTagList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

			NBTTagCompound entryNbt = new NBTTagCompound();
			entryNbt.setString("id", entityRegName.toString());
			entryNbt.setTag("data", entityNbt);

			list.appendTag(entryNbt);

			nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

			handler.setOfflinePlayerData(playerUuid, nbt);

			return true;
		}

		return false;
	}

	public boolean returnEntityFromRing(Entity user, UUID playerUuid) {
		IOfflinePlayerDataHandler handler = OfflinePlayerHandlerImpl.getHandler();
		if(handler != null) {
			NBTTagCompound nbt = handler.getOfflinePlayerData(playerUuid);

			if(nbt != null && nbt.hasKey(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_LIST)) {
				NBTTagList list = nbt.getTagList(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, Constants.NBT.TAG_COMPOUND);

				if(list.tagCount() > 0) {
					int removedTag = -1;

					for(int i = 0; i < list.tagCount(); i++) {
						NBTTagCompound entryNbt = list.getCompoundTagAt(i);

						if(entryNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
							ResourceLocation entityRegName = new ResourceLocation(entryNbt.getString("id"));

							Entity entity = EntityList.createEntityByIDFromName(entityRegName, user.world);

							entity.setLocationAndAngles(user.posX, user.posY, user.posZ, user.world.rand.nextFloat() * 360, 0);

							if(entity instanceof IRingOfGatheringMinion && ((IRingOfGatheringMinion) entity).returnFromRing(user, entryNbt.getCompoundTag("data"))) {
								removedTag = i;
								break;
							}
						}
					}

					if(removedTag >= 0) {
						list.removeTag(removedTag);

						nbt.setTag(NBT_OFFLINE_PLAYER_DATA_LIST_KEY, list);

						handler.setOfflinePlayerData(playerUuid, nbt);

						return true;
					}
				}
			}
		}

		return false;
	}

	public int getCapacity() {
		return 6;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.ring.gathering.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.ring.gathering", KeyBindRegistry.RADIAL_MENU.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.press.shift"));
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
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if(entityIn.ticksExisted % 5 == 0) {
			this.updateStackEntryCount(worldIn, stack, entityIn);
		}
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
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null && nbt.getByte(NBT_SYNC_COUNT_KEY) > 0 && removeXp(playerIn, 15) >= 15) {
			if(!worldIn.isRemote) {
				this.returnEntityFromRing(playerIn, playerIn.getUniqueID());
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();

		if(!entity.world.isRemote && entity instanceof IRingOfGatheringMinion) {
			IRingOfGatheringMinion minion = (IRingOfGatheringMinion) entity;

			UUID playerUuid = minion.getRingOwnerId();

			if(playerUuid != null && minion.shouldReturnOnDeath(entity.world.getPlayerEntityByUUID(playerUuid) != null)) {
				returnMinionToRing(minion);
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

					if(ItemRegistry.RING_OF_GATHERING.addEntry(playerUuid, id, entityNbt)) {
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
