package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.gen.feature.WorldGenRubberTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSapTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.AdvancedStateMap;

public class BlockSaplingBetweenlands extends BlockSapling implements IStateMappedBlock  {
	private String TREE_TYPE;

	public BlockSaplingBetweenlands(String type) {
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		this.TREE_TYPE = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(item));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote) {
			this.checkAndDropBlock(world, pos, state);

			if (rand.nextInt(7) == 0) {
				this.grow(world, pos, state, rand);
			}
		}
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == BlockRegistry.SWAMP_DIRT || state.getBlock() == BlockRegistry.SWAMP_GRASS || state.getBlock() == BlockRegistry.DEAD_GRASS || super.canSustainBush(state);
	}

	@Override
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!TerrainGen.saplingGrowTree(world, rand, pos)) {
			return;
		}

		WorldGenerator worldGen = null;

		switch (TREE_TYPE) {
		case "WEEDWOOD":
			worldGen = new WorldGenWeedwoodTree();
			break;
		case "SAP":
			worldGen = new WorldGenSapTree();
			break;
		case "RUBBER":
			worldGen = new WorldGenRubberTree();
			break; 
		default:
			break;
		}

		if (worldGen == null) {
			return;
		}

		world.setBlockToAir(pos);

		if (!worldGen.generate(world, rand, pos)) {
			world.setBlockState(pos, state);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(TYPE);
	}
}