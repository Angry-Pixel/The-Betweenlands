package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class ItemDrinkableBrew extends ItemBLFood {
	boolean hasBuff;
	public int buffType;
	public int buffDuration;

	public ItemDrinkableBrew(int healAmount, float saturationModifier) {
		super(healAmount, saturationModifier, false); //no need for wolf food :P
		setAlwaysEdible();
	}

	public ItemDrinkableBrew(int healAmount, float saturationModifier, boolean hasBuffIn, int buffTypeIn, int buffDurationIn) {
		this(healAmount, saturationModifier);
		this.hasBuff = hasBuffIn;
		this.buffType = buffTypeIn;
		this.buffDuration = buffDurationIn;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	public boolean canGetSickOf(@Nullable EntityPlayer player, ItemStack stack) {
		return false;  // immune to food sickness mechanic atm :P
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		if (stack.getCount() != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
		if (hasBuff)
			applyBuffToPlayer(player, buffType);
	}

	private void applyBuffToPlayer(EntityPlayer player, int buffTypeIn) {
		switch (buffTypeIn) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, buffDuration, 1)); //test
			break;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			break;
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}
}
