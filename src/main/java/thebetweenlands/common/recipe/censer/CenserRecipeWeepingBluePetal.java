package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeWeepingBluePetal extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "weeping_blue_petal");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.WEEPING_BLUE_PETAL;
	}

	private List<EntityLivingBase> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(32, 1, 32).expand(0, 16, 0));
	}

	@Override
	public int update(Void context, ICenser censer) {
		World world = censer.getCenserWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			boolean applied = false;

			BlockPos pos = censer.getCenserPos();

			List<EntityLivingBase> affected = this.getAffectedEntities(world, pos);
			for(EntityLivingBase living : affected) {
				IDecayCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				if(cap != null) {
					DecayStats stats = cap.getDecayStats();

					if(stats.getDecayLevel() > 0) {
						stats.addStats(-2, 0);

						applied = true;
					}
				}
			}

			if(applied) {
				return 150;
			}
		}

		return 0;
	}

	@Override
	public int getConsumptionAmount(Void context, ICenser censer) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(Void context, ICenser censer, EffectColorType type) {
		return 0xFF1030AA;
	}
}
