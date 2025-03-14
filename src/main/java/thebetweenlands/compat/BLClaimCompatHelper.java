package thebetweenlands.compat;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thebetweenlands.compat.flan.BetweenlandsFlanCompat;

public class BLClaimCompatHelper {

	// What to default to if we can't connect to the claim mod
	public static final boolean RESTRICT_BLOCK_BREAK_DEFAULT = false;
	public static final boolean RESTRICT_BLOCK_PLACE_DEFAULT = false;
	public static final boolean ARE_SAME_CLAIM_DEFAULT = true; 
	
	
	// Will have more checks as compatibility for more mods is added (e.g. FTB Chunks, Open Parties and Claims, etc.)
	public static boolean restrictBlockBreak(Level level, BlockPos pos, @Nullable Entity entity) {
		return BetweenlandsFlanCompat.restrictBlockBreak(level, pos, entity);
	}

	public static boolean restrictBlockPlace(Level level, BlockPos pos, @Nullable Entity entity) {
		return BetweenlandsFlanCompat.restrictBlockPlace(level, pos, entity);
	}
}
