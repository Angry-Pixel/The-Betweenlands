package thebetweenlands.common.item.armor.amphibious;



import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemAmphibiousArmourUpgradeTrigger extends Item {

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		NBTTagCompound nbt = player.getEntityData();
		List<IAmphibiousArmorUpgrade> upgradeListChest = new ArrayList<>();

		if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
			if (!getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty()) {
				for (IAmphibiousArmorUpgrade upgrade : getUpgradeList(chest, EntityEquipmentSlot.CHEST))
					if (!upgrade.matches(EntityEquipmentSlot.CHEST, new ItemStack(ItemRegistry.AA_UPGRADE_GLIDE))) { // no need for glider here
						upgradeListChest.add(upgrade);
					}
			}
		}

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("scrollPos", -1);
            }

		if (player.isSneaking()) {
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
				int scrollSize = upgradeListChest.size();
				if (!upgradeListChest.isEmpty()) {
					if (stack.getTagCompound().getInteger("scrollPos") < scrollSize)
						stack.getTagCompound().setInteger("scrollPos", stack.getTagCompound().getInteger("scrollPos") + 1);
					if (stack.getTagCompound().getInteger("scrollPos") >= scrollSize)
						stack.getTagCompound().setInteger("scrollPos", 0);
					player.sendStatusMessage(new TextComponentTranslation("chat.aa_trigger.selected_effect", new TextComponentTranslation("" + upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")))), true);
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		} else {
			//activate effect based on armour upgrade and counter selection
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {

				if (stack.getTagCompound().getInteger("scrollPos") != -1 && !upgradeListChest.isEmpty()) {

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.URCHIN) {
						int urchinCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.URCHIN);
						long urchinAOECooldown = nbt.getLong(armorEffectsHelper.NBT_URCHIN_AOE_COOLDOWN);
						if (urchinCount >= 1) {
							if (world.getTotalWorldTime() >= urchinAOECooldown) {
								if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
									armorEffectsHelper.spawnUrchinSpikes(world, player, urchinCount);
									nbt.setLong(armorEffectsHelper.NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 50);
								}
							}
						}
					}

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.FISH_VORTEX) {
						int vortexCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.FISH_VORTEX);
						if (vortexCount >= 1)
							if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL)
								armorEffectsHelper.activateFishVortex(world, player, vortexCount);
					}

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.ELECTRIC) {
						int electricCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.ELECTRIC);
						long electricCooldown = nbt.getLong(armorEffectsHelper.NBT_ELECTRIC_COOLDOWN);
						if (electricCount >= 1) {
							if (world.getTotalWorldTime() >= electricCooldown) {
								if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL)
									armorEffectsHelper.spawnElectricEntity(world, player, player, electricCount);
									nbt.setLong(armorEffectsHelper.NBT_ELECTRIC_COOLDOWN, world.getTotalWorldTime() + 50);
							}
						}
					}

					// OTHER STUFFS HERE?
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

	public List<IAmphibiousArmorUpgrade> getUpgradeList(ItemStack stack, EntityEquipmentSlot slotType) {
		List<IAmphibiousArmorUpgrade> nameList = new ArrayList<>();
		for (IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.getUpgrades(slotType)) {
			int count = ((ItemAmphibiousArmor) stack.getItem()).getUpgradeCount(stack, upgrade);
			if (count > 0)
				nameList.add(upgrade);
		}
		return nameList;
	}

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            return false;
        }
        return true;
    }
}
