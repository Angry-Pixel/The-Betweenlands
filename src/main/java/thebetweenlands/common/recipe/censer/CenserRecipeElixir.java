package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.herblore.ItemElixir;
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

	private List<EntityLivingBase> getAffectedEntities(World world, BlockPos pos, CenserRecipeElixirContext context) {
		int amplifier = ItemRegistry.ELIXIR.createPotionEffect(context.elixir, 1).getAmplifier();

		int xzRange = 35 + amplifier * 15;
		int yRange = 12 + amplifier * 4;

		return world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(xzRange, 1, xzRange).expand(0, yRange, 0));
	}

	@Override
	public int update(CenserRecipeElixirContext context, ICenser censer) {
		World world = censer.getWorld();

		if(world.getTotalWorldTime() % 100 == 0) {
			Potion potion = ItemRegistry.ELIXIR.getElixirFromItem(context.elixir).getPotionEffect();

			int maxDuration = ItemRegistry.ELIXIR.createPotionEffect(context.elixir, 0.25D).getDuration();

			BlockPos pos = censer.getPos();

			List<EntityLivingBase> affected = this.getAffectedEntities(world, pos, context);

			if(!world.isRemote) {
				for(EntityLivingBase living : affected) {
					living.addPotionEffect(new PotionEffect(potion, Math.min(maxDuration, 300), 0, true, false));
				}
			}

			context.setConsuming(!affected.isEmpty());
		}

		return 0;
	}

	private int getEffectiveDuration(CenserRecipeElixirContext context) {
		PotionEffect effect = ItemRegistry.ELIXIR.createPotionEffect(context.elixir, 1.0D);
		return 20 + effect.getDuration() * 8;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeElixirContext context, ICenser censer) {
		return Math.max(1, MathHelper.floor(this.getEffectiveDuration(context) / 1000.0f));
	}

	@Override
	public int getConsumptionAmount(CenserRecipeElixirContext context, ICenser censer) {
		if(!context.isConsuming()) {
			return 0;
		}
		return 1 + MathHelper.floor(1000.0f / this.getEffectiveDuration(context));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(CenserRecipeElixirContext context, ICenser censer, EffectColorType type) {
		return ((ItemElixir) context.elixir.getItem()).getColorMultiplier(context.elixir, 0);
	}
}
