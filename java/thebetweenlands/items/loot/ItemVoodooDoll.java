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
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.manual.IManualEntryItem;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemVoodooDoll extends Item implements IManualEntryItem {
	public ItemVoodooDoll() {
		maxStackSize = 1;
		setMaxDamage(24);
		setUnlocalizedName("thebetweenlands.voodooDoll");
		setTextureName("thebetweenlands:voodooDoll");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(5, 5, 5));
		living.remove(player);
		boolean attacked = false;
		for (EntityLivingBase entity : living) {
			if(entity.isEntityAlive()) {
				if (!world.isRemote) {
					attacked |= entity.attackEntityFrom(DamageSource.magic, 20);
				} else if(!entity.isEntityInvulnerable()) {
					attacked = true;
					for(int i = 0; i < 20; i++)
						BLParticle.SWAMP_SMOKE.spawn(world, entity.posX, entity.posY + entity.height / 2.0D, entity.posZ, (world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F, 1.0F);
				}
			}
		}
		if(!world.isRemote) {
			if (living.isEmpty()) {
				player.addChatMessage(new ChatComponentTranslation("chat.voodoo.no.mobs"));
			} else if(attacked) {
				stack.damageItem(1, player);
				world.playSoundEffect(player.posX, player.posY, player.posZ, "thebetweenlands:voodooDoll", 1.0F, 1.0F);
			}
		}
		return stack;
	}

	@Override
	public String manualName(int meta) {
		return "voodooDoll";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}
}
