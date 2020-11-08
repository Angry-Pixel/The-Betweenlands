package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.common.block.misc.BlockOfferingTable;
import thebetweenlands.common.block.structure.BlockSimulacrum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntitySimulacrum;

public class WorldGenSimulacrum extends WorldGenerator {
	private final List<BlockSimulacrum> variants;
	private final ResourceLocation lootTableLocation;

	public WorldGenSimulacrum(List<BlockSimulacrum> variants, ResourceLocation lootTable) {
		this.variants = variants;
		this.lootTableLocation = lootTable;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(!world.isRemote && world.isAirBlock(position)) {
			for(int i = 0; i < 8 && world.isAirBlock(position); i++) {
				position = position.down();
			}

			position = position.up();

			if(world.isAirBlock(position) && world.isSideSolid(position.down(), EnumFacing.UP) && this.canGenerateHere(world, rand, position)) {
				BlockSimulacrum block = this.variants.get(rand.nextInt(this.variants.size()));

				EnumFacing facing = null;

				if(world instanceof WorldServer && this.shouldGenerateOfferingTable(rand)) {
					List<EnumFacing> offsets = new ArrayList<>(Arrays.asList(EnumFacing.HORIZONTALS));
					Collections.shuffle(offsets, rand);

					for(EnumFacing dir : offsets) {
						BlockPos offset = position.offset(dir);

						if(world.isAirBlock(offset) && world.isSideSolid(offset.down(), EnumFacing.UP)) {
							this.setBlockAndNotifyAdequately(world, offset, BlockRegistry.OFFERING_TABLE.getDefaultState().withProperty(BlockOfferingTable.FACING, dir.getOpposite()));

							facing = dir;

							TileEntity tile = world.getTileEntity(offset);
							if(tile instanceof TileEntityGroundItem) {
								LootTable lootTable = ((WorldServer) world).getLootTableManager().getLootTableFromLocation(this.lootTableLocation);

								if(lootTable != null) {
									LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);

									List<ItemStack> loot = lootTable.generateLootForPools(rand, lootBuilder.build());

									if(!loot.isEmpty()) {
										((TileEntityGroundItem) tile).setStack(loot.get(0));
									}
								}
							}

							break;
						}
					}
				}

				IBlockState state = block.getDefaultState()
						.withProperty(BlockSimulacrum.VARIANT, BlockSimulacrum.Variant.values()[rand.nextInt(BlockSimulacrum.Variant.values().length)])
						.withProperty(BlockSimulacrum.FACING, facing == null ? EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)] : facing);

				this.setBlockAndNotifyAdequately(world, position, state);

				TileEntity tile = world.getTileEntity(position);
				if(tile instanceof TileEntitySimulacrum) {
					((TileEntitySimulacrum) tile).setEffect(TileEntitySimulacrum.Effect.values()[rand.nextInt(TileEntitySimulacrum.Effect.values().length)]);
				}

				int torches = rand.nextInt(3);

				for(int i = 0; i < 32 && torches > 0; i++) {
					int rx = rand.nextInt(5) - 2;
					int rz = rand.nextInt(5) - 2;

					if((rx != 0 || rz != 0) && Math.abs(rx) + Math.abs(rz) <= 2) {
						BlockPos offset = position.add(rx, rand.nextInt(3) - 2, rz);

						if(world.isAirBlock(offset) && world.isSideSolid(offset.down(), EnumFacing.UP)) {
							this.setBlockAndNotifyAdequately(world, offset, BlockRegistry.MUD_FLOWER_POT_CANDLE.getDefaultState());
							torches--;
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	protected boolean shouldGenerateOfferingTable(Random rand) {
		return rand.nextInt(3) != 0;
	}

	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		return true;
	}
}
