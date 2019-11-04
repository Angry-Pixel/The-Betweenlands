package thebetweenlands.common.block.structure;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityDecayPitControl;

public class BlockDecayPitControl extends BasicBlock implements ITileEntityProvider, ICustomItemBlock {
	public BlockDecayPitControl() {
		super(Material.ROCK);
		setLightLevel(0.5F);
		this.setBlockUnbreakable();
		this.setResistance(2000.0F);
		this.setSoundType2(SoundType.STONE);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDecayPitControl();
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
