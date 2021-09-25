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
import net.minecraft.world.World;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemAmphibiousArmourUpgradeToggle extends Item {

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();

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
					player.sendStatusMessage(new TextComponentTranslation("chat.aa_toggle.selected_effect", new TextComponentTranslation("" + getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")))), true);
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		} else {
			//activate effect based on armour upgrade and counter selection
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {

				if (stack.getTagCompound().getInteger("scrollPos") != -1 && !getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty()) {

					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.URCHIN) {
						int urchinCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.URCHIN);
						if (urchinCount >= 1) {
							chest.getTagCompound().setBoolean("urchinAuto", !chest.getTagCompound().getBoolean("urchinAuto"));
							player.sendStatusMessage(new TextComponentTranslation("chat.aa_toggle.urchin_active", new TextComponentTranslation("" + chest.getTagCompound().getBoolean("urchinAuto"))), true);
						}
					}

					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.FISH_VORTEX) {
						int vortexCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.FISH_VORTEX);
						if (vortexCount >= 1) {
							chest.getTagCompound().setBoolean("vortexAuto", !chest.getTagCompound().getBoolean("vortexAuto"));
							player.sendStatusMessage(new TextComponentTranslation("chat.aa_toggle.vortex_active", new TextComponentTranslation("" + chest.getTagCompound().getBoolean("vortexAuto"))), true);
						}
					}

					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.ELECTRIC) {
						int electricCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.ELECTRIC);
						if (electricCount >= 1) {
							chest.getTagCompound().setBoolean("electricAuto", !chest.getTagCompound().getBoolean("electricAuto"));
							player.sendStatusMessage(new TextComponentTranslation("chat.aa_toggle.electric_active", new TextComponentTranslation("" + chest.getTagCompound().getBoolean("electricAuto"))), true);
						}
					}

					if (getUpgradeList(chest, EntityEquipmentSlot.CHEST).get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.GLIDE) {
						int glideCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.GLIDE);
						if (glideCount >= 1) {
							chest.getTagCompound().setBoolean("glideAuto", !chest.getTagCompound().getBoolean("glideAuto"));
							player.sendStatusMessage(new TextComponentTranslation("chat.aa_toggle.glide_active", new TextComponentTranslation("" + chest.getTagCompound().getBoolean("glideAuto"))), true);
						}
					}

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
