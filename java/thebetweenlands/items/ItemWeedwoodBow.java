package thebetweenlands.items;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import thebetweenlands.entities.EntityBLArrow;

public class ItemWeedwoodBow extends ItemBow {

	public static final String[] bowAnimationIcon = new String[] { "weedwoodBow0", "weedwoodBow1", "weedwoodBow2" };

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;

	public ItemWeedwoodBow() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister icon) {
		itemIcon = icon.registerIcon("thebetweenlands:weedwoodBow");
		iconArray = new IIcon[bowAnimationIcon.length];

		for (int iconIndex = 0; iconIndex < iconArray.length; ++iconIndex) {
			iconArray[iconIndex] = icon.registerIcon("thebetweenlands:" + bowAnimationIcon[iconIndex]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (usingItem != null) {
			int time = 36000 - useRemaining;
			if (time < 6)
				return iconArray[0];
			if (time < 10)
				return iconArray[1];
			return iconArray[2];
		}
		return getIcon(stack, renderPass);
	}

	@Override
	public IIcon getItemIconForUseDuration(int iconIndex) {
		return iconArray[iconIndex];
	}

	@Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 36000;
    }

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int inUseCount) {
		int maxUseDuration = getMaxItemUseDuration(stack) - inUseCount;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, maxUseDuration);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
			return;
		maxUseDuration = event.charge;

		boolean canShoot = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (canShoot || player.inventory.hasItem(BLItemRegistry.anglerToothArrow)) {
			float power = (float) maxUseDuration / 10.0F;
			power = (power * power + power * 2.0F) / 3.0F;

			if ((double) power < 0.1D)
				return;

			if (power > 1.0F)
				power = 1.0F;

			EntityBLArrow entityarrow = new EntityBLArrow(world, player, power * 4.0F);

			if (power == 1.0F)
				entityarrow.setIsCritical(true);

			int powerEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (powerEnchant > 0)
				entityarrow.setDamage(entityarrow.getDamage() + (double) powerEnchant * 0.5D + 0.5D);

			int punchEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (punchEnchant > 0)
				entityarrow.setKnockbackStrength(punchEnchant);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				entityarrow.setFire(100);

			stack.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

			if (canShoot)
				entityarrow.canBePickedUp = 2;
			else
				player.inventory.consumeInventoryItem(Items.arrow);

			if (!world.isRemote)
				world.spawnEntityInWorld(entityarrow);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onUpdateFOV(FOVUpdateEvent event) {
		float fov = event.fov;
		if(event.entity.isUsingItem() && event.entity.getItemInUse().getItem() == this ) {
			int duration = event.entity.getItemInUseDuration();
			float multiplier = duration / 10.0F;
			if( multiplier > 1.0F ) {
				multiplier = 1.0F;
			} else {
				multiplier *= multiplier;
			}
			fov = 1.0F - multiplier * 0.15F;
		}
	event.newfov = fov;
	}
}

