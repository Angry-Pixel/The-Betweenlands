package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemNibblestick extends ItemBLFood {
	public ItemNibblestick() {
		super(1, 0.1F, false);
	}

	@Override
	public boolean canGetSickOf(@Nullable EntityPlayer player, ItemStack stack) {
		return true; //Keep food sickness always enabled
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if(cap != null && FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
			int xp = worldIn.rand.nextInt(4);
			if(xp > 0) {
				if(!worldIn.isRemote) {
					cap.increaseFoodHatred(this, 4, 0); //Increased food sickness speed
					player.addExperience(xp);
					worldIn.playSound(null, player.posX, player.posY + 0.5D, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
				} else {
					this.addFinishEatingParticles(stack, worldIn, player);
				}
			}
		}

		super.onFoodEaten(stack, worldIn, player);
	}

	@SideOnly(Side.CLIENT)
	public void addFinishEatingParticles(ItemStack stack, World world, EntityPlayer player) {
		for(int i = 0; i < 20; i++) {
			BLParticles.XP_PIECES.spawn(world, player.posX + world.rand.nextFloat() * 0.6F - 0.3F, player.posY + player.getEyeHeight() - 0.1F + world.rand.nextFloat() * 0.6F - 0.3F, player.posZ + world.rand.nextFloat() * 0.6F - 0.3F);
		}
	}
}
