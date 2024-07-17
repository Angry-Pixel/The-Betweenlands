package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface Censer {

	@Nullable
	Level getLevel();

	BlockPos getBlockPos();

	int getCurrentMaxInputAmount();

	int getCurrentRemainingInputAmount();

	boolean isRecipeRunning();

	float getEffectStrength(float partialTicks);

	ItemStack getInputStack();
}
