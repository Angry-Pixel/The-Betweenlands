package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class TileEntityHopperBetweenlands extends TileEntityHopper {
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock(); //Urgh why is this even a thing
	}
	
	@Override
    public String getName() {
        return "container.bl.syrmorite_hopper";
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }
}
