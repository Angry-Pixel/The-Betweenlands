package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
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
		int armorPieces = 0;
		for (ItemStack anArmor : armor) {
			if (anArmor != null && anArmor.getItem() instanceof LurkerSkinArmor) {
				armorPieces += 1;
			}
		}
		if (armorPieces > 1 && player.isInWater()) {
			if(player.moveForward > 0) {
				Vec3 lookVec = player.getLookVec().normalize();
				player.motionX += lookVec.xCoord * player.moveForward * 0.035D;
				player.motionZ += lookVec.zCoord * player.moveForward * 0.035D;
				player.motionY += lookVec.yCoord * player.moveForward * 0.035D;
				player.getFoodStats().addExhaustion(0.003F);
			}
			if(armorPieces >= 4) {
				if (!player.isPotionActive(Potion.waterBreathing) && armorPieces >= 4) {
					player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 10));
				}
				player.setAir(300);
			}
		}
	}
}