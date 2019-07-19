package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeSapBall extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "sap_ball");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.SAP_BALL;
	}

	@Override
	public int update(Void context, int amountLeft, TileEntity censer) {
		World world = censer.getWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			BlockPos pos = censer.getPos();

			List<EntityLivingBase> affected = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(32, 1, 32).expand(0, 16, 0));
			for(EntityLivingBase living : affected) {
				IDecayCapability cap = living.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				if(cap != null) {
					DecayStats stats = cap.getDecayStats();
					stats.addStats(-1, 0);
				}
			}
		}
		
		return 0;
	}
}
