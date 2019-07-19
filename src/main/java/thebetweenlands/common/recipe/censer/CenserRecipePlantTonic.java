package thebetweenlands.common.recipe.censer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipePlantTonic extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "plant_tonic");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.BL_BUCKET_PLANT_TONIC;
	}

	@Override
	public ItemStack consumeInput(ItemStack stack) {
		return new ItemStack(ItemRegistry.BL_BUCKET, 1, stack.getMetadata());
	}

	@Override
	public int update(Void context, int amountLeft, TileEntity censer) {
		World world = censer.getWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			BlockPos pos = censer.getPos();

			//TODO
		}

		return 2;
	}
}
