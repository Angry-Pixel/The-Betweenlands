package thebetweenlands.api.rune;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IRuneEffect {
	public boolean apply(World world, IRuneChainUser user);
	
	public boolean apply(World world, Entity entity);
	
	public boolean apply(World world, Vec3d position);
	
	public boolean apply(World world, BlockPos pos);
}
