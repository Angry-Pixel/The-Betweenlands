package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorEffectsHelper;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemSoulGemFocus extends Item {

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();
	private static final String NBT_URCHIN_AOE_COOLDOWN = "thebetweenlands.urchin_aoe_cooldown";

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		NBTTagCompound nbt = player.getEntityData();

		if (player.isSneaking()) {
			// scroll stuff (add 1 to counter or something)
			// display name active/toggle
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			//TODO activate effect based on armour upgrade and counter selection
			//testing single type here - auto effect is disabled in armour atm
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {

				int urchinCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.URCHIN);
				long urchinAOECooldown = nbt.getLong(NBT_URCHIN_AOE_COOLDOWN);

				if (urchinCount >= 1) { // needs check to see if toggled here
					if (world.getTotalWorldTime() >= urchinAOECooldown) {
						if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
							armorEffectsHelper.spawnUrchinSpikes(world, player, urchinCount);
							nbt.setLong(NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 50);
						}
					}
				}
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

}
