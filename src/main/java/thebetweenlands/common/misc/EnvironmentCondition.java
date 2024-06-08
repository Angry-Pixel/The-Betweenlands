package thebetweenlands.common.misc;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import thebetweenlands.common.features.BetweenlandsCaveVegetationConfig;

// Check area around a point and find a place to translate placed feature
public class EnvironmentCondition {
	public static final Codec<EnvironmentCondition> CODEC = RecordCodecBuilder.create((p_67849_) -> {
		return p_67849_.group(Codec.list(BetweenlandsCaveVegetationConfig.TargetBlockState.CODEC).fieldOf("targets").forGetter((p_161027_) -> {
			return p_161027_.targetStates;
		}),
		Codec.BOOL.fieldOf("active").forGetter((p_161028_) -> {
			return p_161028_.active;
		}),
		Codec.INT.fieldOf("sizex").forGetter((p_161028_) -> {
			return p_161028_.sizex;
		}),
		Codec.INT.fieldOf("sizey").forGetter((p_161028_) -> {
			return p_161028_.sizey;
		}),
		Codec.INT.fieldOf("sizez").forGetter((p_161028_) -> {
			return p_161028_.sizez;
		}),
		Codec.STRING.fieldOf("scanStyle").forGetter((p_161028_) -> {
			return p_161028_.rawstyle;
		})).apply(p_67849_, EnvironmentCondition::new);
	});
	
	// Blockstates to look for when translating
	public final List<BetweenlandsCaveVegetationConfig.TargetBlockState> targetStates;
	public final String rawstyle;
	public final int style;
	public final int sizex;
	public final int sizey;
	public final int sizez;
	public final boolean active;
	
	// Init the variable
	public EnvironmentCondition(List<BetweenlandsCaveVegetationConfig.TargetBlockState> targets, boolean active, int x, int y, int z, String style) {
		this.targetStates = targets;
		this.rawstyle = style;
		this.active = active;
		
		// Get type
		if (this.rawstyle.compareTo("fast") == 0)
		{
			// On first condition match return
			this.style = 0;
		}
		else if (this.rawstyle.compareTo("hybrid") == 0)
		{
			// Return closest to point
			this.style = 1;
		}
		else if (this.rawstyle.compareTo("smart") == 0)
		{
			// Return closest to point by exact hypotonuse
			this.style = 2;
		}
		else
		{
			// defalt to fast
			this.style = 0;
		}
		
		this.sizex = x;
		this.sizey = y;
		this.sizez = z;
	}
	
	// find the closest 
	public int[] sample(WorldGenLevel worldgenlevel, int inX, int inY, int inZ) {
		int[] output = {0,0,0}; // X Y Z
		
		if (!this.active)
		{
			// return null to inform caller of a fail
			return null;
		}
		
		for (int x = inX - this.sizex; x <= inX + this.sizex; x++) {
			for (int y = inY - this.sizey; y <= inY + this.sizey; y++) {
				for (int z = inZ - this.sizez; z <= inZ + this.sizez; z++) {
					if (worldgenlevel.getBlockState(new BlockPos(x, y, z)) == Blocks.AIR.defaultBlockState())
					{
						// send array
						output[0] = x;
						output[1] = y;
						output[2] = z;
						return output;
					}
				}
			}
		}
		
		// return null to inform caller of a fail
		return null;
	}
	
}
