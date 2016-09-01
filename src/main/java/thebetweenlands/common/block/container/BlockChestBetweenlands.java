package thebetweenlands.common.block.container;

import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;

public class BlockChestBetweenlands extends BlockChest {
	public static BlockChest.Type WEEDWOOD_CHEST = EnumHelper.addEnum(BlockChest.Type.class, "WEEDWOOD_CHEST", new Class[0], new Object[0]);

	public BlockChestBetweenlands(Type chestTypeIn) {
		super(chestTypeIn);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChestBetweenlands();
	}
}
