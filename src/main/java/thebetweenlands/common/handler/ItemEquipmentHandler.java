package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemEquipmentHandler {
	private ItemEquipmentHandler() { }

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(event.phase == Phase.END) {
			tickEquipmentInventories(event.world);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && !Minecraft.getInstance().isGamePaused()) {
				tickEquipmentInventories(world);
			}
		}
	}

	private static void tickEquipmentInventories(World world) {
		for(int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity entity = world.loadedEntityList.get(i);

			if(entity instanceof EntityLivingBase && entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

				for(EnumEquipmentInventory invType : EnumEquipmentInventory.values()) {
					IInventory inventory = cap.getInventory(invType);

					if(inventory instanceof ITickable) {
						((ITickable) inventory).update();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(player != null && target != null && target instanceof EntityPlayer == false) {
			ItemStack heldItem = event.getItemStack();

			if(!player.isSneaking() && !heldItem.isEmpty()) {
				if(heldItem.getItem() instanceof IEquippable) {
					IEquippable equippable = (IEquippable) heldItem.getItem();

					if(equippable.canEquipOnRightClick(heldItem, player, target)) {
						ItemStack result = EquipmentHelper.equipItem(player, target, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.abilities.isCreativeMode) {
								player.setHeldItem(event.getHand(), result);
							}

							player.swingArm(event.getHand());
						}
					}
				}
			} else if(player.isSneaking() && heldItem.isEmpty()) {
				if(EquipmentHelper.tryPlayerUnequip(player, target)) {
					player.swingArm(EnumHand.MAIN_HAND);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(event instanceof PlayerInteractEvent.RightClickBlock) {
			tryEquip(player, event.getHand(), false);
		} else if(event instanceof PlayerInteractEvent.RightClickEmpty) {
			if(!tryEquip(player, EnumHand.MAIN_HAND, true)) {
				tryEquip(player, EnumHand.OFF_HAND, true);
			}
		}
	}

	private static boolean tryEquip(EntityPlayer player, EnumHand hand, boolean packet) {
		ItemStack heldItem = player.getHeldItem(hand);

		if(player != null && !heldItem.isEmpty() && heldItem.getItem() instanceof IEquippable) {
			IEquippable equippable = (IEquippable) heldItem.getItem();

			if(equippable.canEquipOnRightClick(heldItem, player, player)) {
				if(packet) {
					if(player.world.isRemote) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(hand == EnumHand.OFF_HAND) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(-1, player));

								player.swingArm(hand);

								return true;
							} else {
								int slot = player.inventory.getSlotFor(heldItem);
								if(slot >= 0) {
									TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(slot, player));

									player.swingArm(hand);

									return true;
								}
							}
						}
					}
				} else {
					if(player.world.isRemote) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							player.swingArm(hand);
							return true;
						}
					} else {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.abilities.isCreativeMode) {
								player.setHeldItem(hand, result);
							}

							player.swingArm(hand);

							player.sendStatusMessage(new TextComponentTranslation("chat.equipment.equipped", new TextComponentTranslation(heldItem.getTranslationKey() + ".name")), true);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onDeathDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity != null && !entity.world.isRemote && !entity.world.getGameRules().getBoolean("keepInventory")) {
			if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

				for(EnumEquipmentInventory type : EnumEquipmentInventory.values()) {
					IInventory inv = cap.getInventory(type);

					for(int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);

						if(!stack.isEmpty()) {
							if(stack.getItem() instanceof IEquippable) {
								IEquippable equippable = (IEquippable) stack.getItem();
								equippable.onUnequip(stack, entity, inv);
								if(!equippable.canDrop(stack, entity, inv)) {
									continue;
								}
							}

							EntityItem equipmentDrop = new EntityItem(entity.world, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, stack.copy());
							equipmentDrop.setDefaultPickupDelay();
							event.getDrops().add(equipmentDrop);
						}
					}
				}
			}
		}
	}
}
