package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeSwampWater extends AbstractCenserRecipe<CenserRecipeSwampWaterContext> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "swamp_water");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.getFluid() == FluidRegistry.SWAMP_WATER;
	}

	@Override
	public boolean matchesSecondaryInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.BARK_AMULET;
	}

	@Override
	public CenserRecipeSwampWaterContext createContext(FluidStack stack) {
		return new CenserRecipeSwampWaterContext();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(CenserRecipeSwampWaterContext context, ICenser censer, EffectColorType type) {
		return 0xFFEEEEEE;
	}

	private List<EntityPlayer> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(45, 1, 45).expand(0, 16, 0));
	}

	@Override
	public int update(CenserRecipeSwampWaterContext context, ICenser censer) {
		ItemStack inputStack = censer.getInputStack();

		if(!inputStack.isEmpty() && inputStack.getItem() == ItemRegistry.BARK_AMULET) {
			World world = censer.getWorld();

			if(world.getTotalWorldTime() % 100 == 0) {
				BlockPos pos = censer.getPos();

				List<EntityPlayer> affected = this.getAffectedEntities(world, pos);

				if(!world.isRemote) {
					for(EntityPlayer player : affected) {
						player.addPotionEffect(new PotionEffect(ElixirEffectRegistry.ENLIGHTENED, 200, 0, true, false));
					}
				}

				context.setConsuming(!affected.isEmpty());
			}
		} else {
			context.setConsuming(false);
		}

		return 0;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeSwampWaterContext context, ICenser censer) {
		//7.5min / bucket
		return 9;
	}

	@Override
	public int getConsumptionAmount(CenserRecipeSwampWaterContext context, ICenser censer) {
		return context.isConsuming() ? 1 : 0;
	}
}
