package thebetweenlands.api.block;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICenser {
	public World getCenserWorld();

	public BlockPos getPos();

	public int getCurrentMaxInputAmount();

	public int getCurrentRemainingInputAmount();

	public boolean isRecipeRunning();

	public float getEffectStrength(float partialTicks);

	public ItemStack getInputStack();
}
