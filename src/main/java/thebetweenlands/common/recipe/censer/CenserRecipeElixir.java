package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeElixir extends AbstractCenserRecipe<CenserRecipeElixirContext> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "elixir");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		if(stack.getItem() == ItemRegistry.ELIXIR) {
			ElixirEffect effect = ItemRegistry.ELIXIR.getElixirFromItem(stack);
			return !effect.getPotionEffect().isInstant();
		}
		return false;
	}

	@Override
	public CenserRecipeElixirContext createContext(ItemStack stack) {
		return new CenserRecipeElixirContext(stack);
	}

	@Override
	public int update(CenserRecipeElixirContext context, int amountLeft, TileEntity censer) {
		World world = censer.getWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			Potion potion = ItemRegistry.ELIXIR.getElixirFromItem(context.elixir).getPotionEffect();

			BlockPos pos = censer.getPos();

			List<EntityLivingBase> affected = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(32, 1, 32).expand(0, 16, 0));
			for(EntityLivingBase living : affected) {
				living.addPotionEffect(new PotionEffect(potion, 200, 0, true, false));
			}
		}

		return 0;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeElixirContext context, int amountLeft, TileEntity censer) {
		PotionEffect effect = ItemRegistry.ELIXIR.createPotionEffect(context.elixir, 1.0D);
		return 5 + effect.getDuration() * 5;
	}
}
