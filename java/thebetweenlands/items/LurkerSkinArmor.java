package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.BLMaterials;

public class LurkerSkinArmor extends ItemArmor {

	public LurkerSkinArmor(int armorType) {
		super(BLMaterials.armorLurkerSkin, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.lurkerSkinLeggings)
			return "thebetweenlands:textures/armour/lurker2.png";
		else
			return "thebetweenlands:textures/armour/lurker1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == EnumMaterialsBL.LURKER_SKIN.ordinal();
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		ItemStack[] armor = player.inventory.armorInventory;
		int armorPeaces = 0;
		for (ItemStack anArmor : armor) {
			if (anArmor != null && anArmor.getItem() instanceof LurkerSkinArmor) {
				armorPeaces+= 1;
			}
		}
		float speedMod = 1;
		speedMod *= 1.0F + 0.2F * armorPeaces + 1;
		if(armorPeaces > 1 && player.isInWater()){
			player.motionX+= speedMod;
			player.motionY+= speedMod;
			player.motionZ+= speedMod;

			if(!player.isPotionActive(Potion.waterBreathing) && armorPeaces >= 4)
				player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 10));
		}

	}
}