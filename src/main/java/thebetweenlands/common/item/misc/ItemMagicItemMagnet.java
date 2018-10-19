package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemMagicItemMagnet extends Item implements IEquippable, IAnimatorRepairable {
	public ItemMagicItemMagnet() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.setMaxStackSize(1);
		this.setMaxDamage(2048);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("magnetActive") : false;
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.DEFAULT;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
		return getMagnetInEquipment(player).isEmpty();
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("magnetActive", true);
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("magnetActive", false);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(stack.getItemDamage() < stack.getMaxDamage()) {
			double range = 7;

			AxisAlignedBB area = new AxisAlignedBB(entity.posX, entity.posY + entity.height / 2, entity.posZ, entity.posX, entity.posY + entity.height / 2, entity.posZ).grow(range);
			List<EntityItem> entities = entity.world.getEntitiesWithinAABB(EntityItem.class, area, e -> e.getDistanceSq(entity.posX, entity.posY + entity.height / 2, entity.posZ) <= range*range);

			for(EntityItem item : entities) {
				if(!item.hasNoGravity()) {
					item.motionY += 0.03999999910593033D;
				}

				double dx = entity.posX - item.posX;
				double dy = entity.posY + entity.height / 2 - (item.posY + item.height / 2);
				double dz = entity.posZ - item.posZ;
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);

				if(!entity.world.isRemote) {
					item.motionX += dx / len * 0.01D;
					item.motionY += dy / len * 0.01D;
					item.motionZ += dz / len * 0.01D;
					item.velocityChanged = true;
				} else {
					this.spawnParticles(item);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnParticles(EntityItem item) {
		if(item.ticksExisted % 4 == 0) {
			BLParticles.CORRUPTED.spawn(item.world, item.posX, item.posY + item.height / 2.0f + 0.25f, item.posZ, ParticleArgs.get().withScale(0.5f));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the magnet break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 32;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 38;
	}

	protected static ItemStack getMagnetInEquipment(Entity entity) {
		if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory mainInv = cap.getInventory(EnumEquipmentInventory.DEFAULT);
			for(int i = 0; i < mainInv.getSizeInventory(); i++) {
				ItemStack stack = mainInv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemMagicItemMagnet) {
					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@SubscribeEvent
	public static void onItemPickup(ItemPickupEvent event) {
		if(!event.player.world.isRemote) {
			ItemStack magnet = getMagnetInEquipment(event.player);
			if(!magnet.isEmpty()) {
				//Damage magnet on pickup
				magnet.damageItem(1, event.player);
			}
		}
	}
}
