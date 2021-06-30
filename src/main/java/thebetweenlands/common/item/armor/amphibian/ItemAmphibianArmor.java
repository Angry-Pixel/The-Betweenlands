package thebetweenlands.common.item.armor.amphibian;

import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAmphibianArmorUpgrade;
import thebetweenlands.client.render.model.armor.ModelAmphibianArmor;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.inventory.InventoryAmphibianArmor;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.Item3DArmor;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.util.NBTHelper;

public class ItemAmphibianArmor extends Item3DArmor {
	private static final Random UID_RNG = new Random();

	private static final String NBT_UPGRADE_MAP_KEY = "thebetweenlands.amphibian_armor_upgrades";
	private static final String NBT_DAMAGE_MAP_KEY = "thebetweenlands.amphibian_armor_damage";

	private static final String NBT_AMPHIBIAN_UPGRADE_HAD_NO_NBT_KEY = "thebetweenlands.amphibian_armor_upgrade_had_no_nbt";
	private static final String NBT_AMPHIBIAN_UPGRADE_DAMAGE_KEY = "thebetweenlands.amphibian_armor_upgrade_damage";
	private static final String NBT_AMPHIBIAN_UPGRADE_MAX_DAMAGE_KEY = "thebetweenlands.amphibian_armor_upgrade_max_damage";
	private static final String NBT_AMPHIBIAN_UPGRADE_DAMAGE_UID_KEY = "thebetweenlands.amphibian_armor_upgrade_damage_uid";

	public ItemAmphibianArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIAN, 3, slot, "amphibian");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "amphibian_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "amphibian_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "amphibian_green");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBodyAttachment createModel() {
		return new ModelAmphibianArmor();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		//TODO Testing
		for(IAmphibianArmorUpgrade upgrade : AmphibianArmorUpgrades.values()) {
			System.out.println(upgrade.getId() + ": " + this.getUpgradeCount(stack, upgrade));
		}

		if (player.isSneaking()) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_AMPHIBIAN_ARMOR, world, 0, 0, 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			return super.onItemRightClick(world, player, hand);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

		if(slot == this.armorType) {
			for(IAmphibianArmorUpgrade upgrade : AmphibianArmorUpgrades.getUpgrades(this.armorType)) {
				int count = this.getUpgradeCount(stack, upgrade);

				if(count > 0) {
					upgrade.applyAttributeModifiers(this.armorType, stack, count, modifiers);
				}
			}
		}

		return modifiers;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, damage);

		//TODO Testing
		for(IAmphibianArmorUpgrade upgrade : AmphibianArmorUpgrades.values()) {
			this.damageUpgrade(stack, upgrade, 1, true);
		}
	}

	public void setUpgradeCounts(ItemStack stack, IInventory inv) {
		NBTTagCompound upgradesMap = new NBTTagCompound();

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack upgradeItem = inv.getStackInSlot(i);

			if(!upgradeItem.isEmpty()) {
				IAmphibianArmorUpgrade upgrade = AmphibianArmorUpgrades.getUpgrade(this.armorType, upgradeItem);

				if(upgrade != null) {
					String idStr = upgrade.getId().toString();
					upgradesMap.setInteger(idStr, upgradesMap.getInteger(idStr) + 1);
				}
			}
		}

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setTag(NBT_UPGRADE_MAP_KEY, upgradesMap);
	}

	public int getUpgradeCount(ItemStack stack, IAmphibianArmorUpgrade upgrade) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			return nbt.getCompoundTag(NBT_UPGRADE_MAP_KEY).getInteger(upgrade.getId().toString());
		}

		return 0;
	}

	public boolean damageUpgrade(ItemStack stack, IAmphibianArmorUpgrade upgrade, int amount, boolean damageAll) {
		boolean damaged = false;

		IInventory inv = new InventoryAmphibianArmor(stack, "");

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack upgradeItem = inv.getStackInSlot(i);

			if(!upgradeItem.isEmpty()) {
				IAmphibianArmorUpgrade itemUpgrade = AmphibianArmorUpgrades.getUpgrade(this.armorType, upgradeItem);

				if(itemUpgrade == upgrade) {
					int damage = this.getUpgradeDamage(stack, i);
					int maxDamage = this.getUpgradeMaxDamage(stack, i);

					if(damage + amount > maxDamage) {
						upgradeItem.shrink(1);
						inv.setInventorySlotContents(i, upgradeItem);
						this.setUpgradeDamage(stack, i, 0, itemUpgrade.getMaxDamage());
					} else {
						this.setUpgradeDamage(stack, i, damage + amount);
					}

					damaged = true;

					if(!damageAll) {
						return true;
					}
				}
			}
		}

		return damaged;
	}

	public void setUpgradeDamage(ItemStack stack, int slot, int damage) {
		this.setUpgradeDamage(stack, slot, damage, -1);
	}

	public void setUpgradeDamage(ItemStack stack, int slot, int damage, int maxDamage) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		String key = NBT_DAMAGE_MAP_KEY + "." + slot;
		NBTTagCompound damageNbt = nbt.getCompoundTag(key);
		if(maxDamage < 0) {
			maxDamage = damageNbt.getInteger("maxDamage");
		} else {
			damageNbt.setInteger("maxDamage", maxDamage);
		}
		damageNbt.setInteger("damage", MathHelper.clamp(damage, 0, maxDamage));
		nbt.setTag(key, damageNbt);
	}

	public int getUpgradeDamage(ItemStack stack, int slot) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			String key = NBT_DAMAGE_MAP_KEY + "." + slot;
			NBTTagCompound damageNbt = nbt.getCompoundTag(key);
			return damageNbt.getInteger("damage");
		}
		return 0;
	}

	public int getUpgradeMaxDamage(ItemStack stack, int slot) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			String key = NBT_DAMAGE_MAP_KEY + "." + slot;
			NBTTagCompound damageNbt = nbt.getCompoundTag(key);
			return damageNbt.getInteger("maxDamage");
		}
		return 0;
	}

	public static int getUpgradeItemStoredDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			return nbt.getInteger(NBT_AMPHIBIAN_UPGRADE_DAMAGE_KEY);
		}
		return 0;
	}


	public static int getUpgradeItemMaxStoredDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			return nbt.getInteger(NBT_AMPHIBIAN_UPGRADE_MAX_DAMAGE_KEY);
		}
		return 0;
	}

	public static void setUpgradeItemStoredDamage(ItemStack stack, int damage, int maxDamage) {
		boolean hadNbt = stack.getTagCompound() != null;

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		if(!nbt.hasKey(NBT_AMPHIBIAN_UPGRADE_DAMAGE_KEY, Constants.NBT.TAG_INT)) {
			//Store whether item previously already had a tag compound
			//If not it can be removed again when damage is removed
			nbt.setBoolean(NBT_AMPHIBIAN_UPGRADE_HAD_NO_NBT_KEY, !hadNbt);
		}

		if(damage > 0) {
			nbt.setInteger(NBT_AMPHIBIAN_UPGRADE_DAMAGE_KEY, damage);
			nbt.setInteger(NBT_AMPHIBIAN_UPGRADE_MAX_DAMAGE_KEY, maxDamage);
			nbt.setLong(NBT_AMPHIBIAN_UPGRADE_DAMAGE_UID_KEY, UID_RNG.nextLong()); //unique ID makes sure the item cannot be stacked
		} else {
			boolean hadNoNbt = nbt.getBoolean(NBT_AMPHIBIAN_UPGRADE_HAD_NO_NBT_KEY);

			nbt.removeTag(NBT_AMPHIBIAN_UPGRADE_HAD_NO_NBT_KEY);
			nbt.removeTag(NBT_AMPHIBIAN_UPGRADE_DAMAGE_KEY);
			nbt.removeTag(NBT_AMPHIBIAN_UPGRADE_MAX_DAMAGE_KEY);
			nbt.removeTag(NBT_AMPHIBIAN_UPGRADE_DAMAGE_UID_KEY);

			if(hadNoNbt && nbt.isEmpty()) {
				stack.setTagCompound(null);
			}
		}
	}
}
