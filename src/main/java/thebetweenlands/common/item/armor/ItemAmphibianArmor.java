package thebetweenlands.common.item.armor;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemAmphibianArmor extends Item3DArmor {

	public ItemAmphibianArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIAN, 3, slot, "amphibian");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "amphibian_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "amphibian_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "amphibian_green");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack itemstack = player.getHeldItem(hand);
		EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
		ItemStack itemstack1 = player.getItemStackFromSlot(entityequipmentslot);
		if (player.isSneaking()) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_AMPHIBIOUS_ARMOUR, world, 0, 0, 0);
		} else {
			if (itemstack1.isEmpty()) {
				player.setItemStackToSlot(entityequipmentslot, itemstack.copy());
				itemstack.setCount(0);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
			} else {
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }
}
