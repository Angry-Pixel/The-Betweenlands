package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface Censer {
	Level getLevel();

	BlockPos getBlockPos();

	int getCurrentMaxInputAmount();

	int getCurrentRemainingInputAmount();

	boolean isRecipeRunning();

	float getEffectStrength(float partialTicks);

	ItemStack getInputStack();
}
