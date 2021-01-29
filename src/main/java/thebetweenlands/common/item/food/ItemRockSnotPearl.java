package thebetweenlands.common.item.food;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;

public class ItemRockSnotPearl extends ItemBLFood {
	public boolean pearledPear;
	public ItemRockSnotPearl(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat, boolean isPearledPear) {
		super(healAmount, saturationModifier, isWolfsFavoriteMeat);
		setAlwaysEdible();
		this.pearledPear = isPearledPear;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		if (!pearledPear)
			list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.rock_snot_pearl"), 0));
		else
			list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.pearled_pear"), 0));
	}

	@Override
	public boolean canGetSickOf(@Nullable EntityPlayer player, ItemStack stack) {
		return false;
	}

	@Override
    public int getMaxItemUseDuration(ItemStack stack) {
		if (pearledPear)
			return 16;
        return 32;
    }

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		int xp = !pearledPear ? 10 : 80;
		if (xp > 0)
			if (!worldIn.isRemote) {
				player.addExperience(xp);
				worldIn.playSound(null, player.posX, player.posY + 0.5D, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
			} else
				addFinishEatingParticles(stack, worldIn, player);
		super.onFoodEaten(stack, worldIn, player);
	}

	@SideOnly(Side.CLIENT)
	public void addFinishEatingParticles(ItemStack stack, World world, EntityPlayer player) {
		for(int i = 0; i < 20; i++) {
			BLParticles.XP_PIECES.spawn(world, player.posX + world.rand.nextFloat() * 0.6F - 0.3F, player.posY + player.getEyeHeight() - 0.1F + world.rand.nextFloat() * 0.6F - 0.3F, player.posZ + world.rand.nextFloat() * 0.6F - 0.3F);
		}
	}
}