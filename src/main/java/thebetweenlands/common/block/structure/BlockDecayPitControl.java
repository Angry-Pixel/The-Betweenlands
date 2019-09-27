package thebetweenlands.common.block.structure;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.tile.TileEntityDecayPitControl;

public class BlockDecayPitControl extends BasicBlock implements ITileEntityProvider {
	public BlockDecayPitControl() {
		super(Material.ROCK);
		setLightLevel(0.5F);
		this.setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDecayPitControl();
	}

}
