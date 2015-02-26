package thebetweenlands.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class SwordBL extends ItemSword {

	public SwordBL(ToolMaterial material) {
		super(material);
	}
	
	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase entity, EntityLivingBase player) {
		is.damageItem(1, player);
		if(is.getItem() == BLItemRegistry.octineSword)
			if(player.worldObj.rand.nextInt(4) == 0)
				entity.setFire(10);
		return true;
	}
}