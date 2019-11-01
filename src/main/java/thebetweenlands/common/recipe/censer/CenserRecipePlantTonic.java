package thebetweenlands.common.recipe.censer;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.clientbound.MessageCureDecayParticles;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;

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
	public int getConsumptionAmount(Void context, ICenser censer) {
		return 0;
	}

	@Override
	public int update(Void context, ICenser censer) {
		World world = censer.getCenserWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			BlockPos pos = censer.getCenserPos();

			final int verticalRange = 4;
			final int range = 16;

			int sx = MathHelper.floor(pos.getX() - range) >> 4;
			int sz = MathHelper.floor(pos.getZ() - range) >> 4;
			int ex = MathHelper.floor(pos.getX() + range) >> 4;
			int ez = MathHelper.floor(pos.getZ() + range) >> 4;

			int cost = 0;

			for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					Chunk chunk = world.getChunkProvider().getLoadedChunk(cx, cz);

					if(chunk != null) {
						Map<BlockPos, TileEntity> tiles = chunk.getTileEntityMap();

						for(Entry<BlockPos, TileEntity> entry : tiles.entrySet()) {
							BlockPos tePos = entry.getKey();
							TileEntity te = entry.getValue();

							if(te instanceof TileEntityDugSoil && tePos.getX() >= pos.getX() - range && tePos.getX() <= pos.getX() + range &&
									tePos.getY() >= pos.getY() - verticalRange && tePos.getY() <= pos.getY() + verticalRange &&
									tePos.getZ() >= pos.getZ() - range && tePos.getZ() <= pos.getZ() + range) {

								if(((TileEntityDugSoil) te).getDecay() > 5) {
									((TileEntityDugSoil) te).setDecay(0);

									TheBetweenlands.networkWrapper.sendToAllAround(new MessageCureDecayParticles(tePos.up()), new TargetPoint(world.provider.getDimension(), tePos.getX(), tePos.getY(), tePos.getZ(), 32));

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

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(Void context, ICenser censer, EffectColorType type) {
		return 0xFFEDBC40;
	}
}
