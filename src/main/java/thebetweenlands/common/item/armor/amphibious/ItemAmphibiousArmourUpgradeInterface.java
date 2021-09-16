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

public class ItemAmphibiousArmourUpgradeInterface extends Item {

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();
	private static final String NBT_URCHIN_AOE_COOLDOWN = "thebetweenlands.urchin_aoe_cooldown";

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		NBTTagCompound nbt = player.getEntityData();

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("scrollPos", -1);
            }

		if (player.isSneaking()) {
			// scroll stuff (add 1 to counter or something)
			// display name active/toggle
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
				int scrollSize = getUpgradeList(chest, EntityEquipmentSlot.CHEST).size();
				if (!getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty()) {
					if (stack.getTagCompound().getInteger("scrollPos") < scrollSize)
						stack.getTagCompound().setInteger("scrollPos", stack.getTagCompound().getInteger("scrollPos") + 1);
					if (stack.getTagCompound().getInteger("scrollPos") >= scrollSize)
						stack.getTagCompound().setInteger("scrollPos", 0);
					player.sendStatusMessage( new TextComponentTranslation("Test: " + getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos"))), true);
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		} else {
			//activate effect based on armour upgrade and counter selection
			//testing single type here - auto effect is disabled in armour atm
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {

				if (stack.getTagCompound().getInteger("scrollPos") != -1 && !getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty()) {

					//URCHIN
					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.URCHIN) {
						int urchinCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.URCHIN);
						long urchinAOECooldown = nbt.getLong(NBT_URCHIN_AOE_COOLDOWN);
						if (urchinCount >= 1) {
							if (world.getTotalWorldTime() >= urchinAOECooldown) {
								if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
									armorEffectsHelper.spawnUrchinSpikes(world, player, urchinCount);
									nbt.setLong(NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 50);
								}
							}
						}
					}

					//VORTEX
					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.FISH_VORTEX) {
						int vortexCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.FISH_VORTEX);
						if (vortexCount >= 1)
							if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL)
								armorEffectsHelper.activateFishVortex(world, player, vortexCount);
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
