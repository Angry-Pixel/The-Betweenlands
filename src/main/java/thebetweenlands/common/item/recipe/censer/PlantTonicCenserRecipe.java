package thebetweenlands.common.item.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.block.Censer;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.ItemRegistry;

public class PlantTonicCenserRecipe extends AbstractCenserRecipe<Void> {

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.is(ItemRegistry.PLANT_TONIC_BUCKET);
	}

	@Override
	public ItemStack consumeInput(ItemStack stack) {
		return null;//new ItemStack(ItemRegistry.BL_BUCKET);
	}

	@Override
	public int getConsumptionAmount(Void context, Censer censer) {
		return 0;
	}

	@Override
	public int update(Void context, Censer censer) {
		Level level = censer.getLevel();

		if(!level.isClientSide() && level.getGameTime() % 100 == 0) {
			BlockPos pos = censer.getBlockPos();

			final int verticalRange = 4;
			final int range = 16;

			int sx = Mth.floor(pos.getX() - range) >> 4;
			int sz = Mth.floor(pos.getZ() - range) >> 4;
			int ex = Mth.floor(pos.getX() + range) >> 4;
			int ez = Mth.floor(pos.getZ() + range) >> 4;

			int cost = 0;

			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					ChunkAccess chunk = level.getChunkSource().getChunk(cx, cz, false);

					if(chunk != null) {

						for(BlockPos tePos : chunk.getBlockEntitiesPos()) {
							BlockEntity te = level.getBlockEntity(pos);

							if(te instanceof DugSoilBlockEntity soil && tePos.getX() >= pos.getX() - range && tePos.getX() <= pos.getX() + range &&
									tePos.getY() >= pos.getY() - verticalRange && tePos.getY() <= pos.getY() + verticalRange &&
									tePos.getZ() >= pos.getZ() - range && tePos.getZ() <= pos.getZ() + range) {

								if(soil.getDecay() > 5) {
									soil.setDecay(level, tePos, 0);

//									PacketDistributor.sendToPlayersNear(level, null, tePos.getX(), tePos.getY(), tePos.getZ(), 32, new MessageCureDecayParticles(tePos.above()));

									cost += 10;
								}
							}
						}
					}
				}
			}

			return cost;
		}

		return 0;
	}

	@Override
	public int getEffectColor(Void context, Censer censer, EffectColorType type) {
		return 0xFFEDBC40;
	}
}
