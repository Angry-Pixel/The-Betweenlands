package thebetweenlands.items.loot;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemVoodooDoll extends Item {
	public ItemVoodooDoll() {
		maxStackSize = 1;
		setMaxDamage(4);
		setUnlocalizedName("thebetweenlands.voodooDoll");
		setTextureName("thebetweenlands:voodooDoll");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(5, 5, 5));
			int damageCount = 0;
			for (EntityLivingBase entity : living) {
				if (entity != player) {
					damageCount++;
					entity.attackEntityFrom(DamageSource.magic, 20);
				}
			}
			if (damageCount == 0) {
				player.addChatMessage(new ChatComponentTranslation("voodoo.no.mobs"));
			} else {
				stack.damageItem(damageCount, player);
			}
		}
		return stack;
	}
}
