package thebetweenlands.common.features;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import thebetweenlands.common.registries.BlockRegistry;

// Edit ore feture to look for air to place blobs of vegitation
public class BetweenlandsCaveVegetationConfig implements FeatureConfiguration {

	public static final Codec<BetweenlandsCaveVegetationConfig> CODEC = RecordCodecBuilder.create((p_67849_) -> {
		return p_67849_.group(Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter((p_161027_) -> {
			return p_161027_.targetStates;
		}),
		Codec.BOOL.fieldOf("doscan").forGetter((p_161028_) -> {
			return p_161028_.active;
		}),
		Codec.INT.fieldOf("scansizex").forGetter((p_161028_) -> {
			return p_161028_.scansizex;
		}),
		Codec.INT.fieldOf("scansizey").forGetter((p_161028_) -> {
			return p_161028_.scansizey;
		}),
		Codec.INT.fieldOf("scansizez").forGetter((p_161028_) -> {
			return p_161028_.scansizez;
		}),
		Codec.BOOL.fieldOf("up").forGetter((p_161028_) -> {
			return p_161028_.up;
		}),
		Codec.BOOL.fieldOf("down").forGetter((p_161028_) -> {
			return p_161028_.down;
		}),
		Codec.BOOL.fieldOf("north").forGetter((p_161028_) -> {
			return p_161028_.north;
		}),
		Codec.BOOL.fieldOf("south").forGetter((p_161028_) -> {
			return p_161028_.south;
		}),
		Codec.BOOL.fieldOf("east").forGetter((p_161028_) -> {
			return p_161028_.east;
		}),
		Codec.BOOL.fieldOf("west").forGetter((p_161028_) -> {
			return p_161028_.west;
		}),
		Codec.STRING.fieldOf("scanStyle").forGetter((p_161028_) -> {
			return p_161028_.rawstyle;
		}),
		FloatProvider.CODEC.fieldOf("sizex").forGetter((p_161028_) -> {
			return p_161028_.sizex;
		}),
		FloatProvider.CODEC.fieldOf("sizey").forGetter((p_161028_) -> {
			return p_161028_.sizey;
		}),
		FloatProvider.CODEC.fieldOf("sizez").forGetter((p_161028_) -> {
			return p_161028_.sizez;
		}),
		Codec.STRING.fieldOf("type").forGetter((p_161028_) -> {
			return p_161028_.rawtype;
		})).apply(p_67849_, BetweenlandsCaveVegetationConfig::new);
	});
	public final List<TargetBlockState> targetStates;
	public final FloatProvider sizex;
	public final FloatProvider sizey;
	public final FloatProvider sizez;
	protected final String rawtype;
	public final int type;
	
	// Scaner
	public final String rawstyle;
	public final int scanstyle;
	public final int scansizex;
	public final int scansizey;
	public final int scansizez;
	public final boolean active;
	
	// State directions
	public final boolean up;
	public final boolean down;
	public final boolean west;
	public final boolean east;
	public final boolean north;
	public final boolean south;
	
	// should i cache this?
	public static List<TargetBlockState> CaveMossTargets() {
		List<TargetBlockState> outlist = new ArrayList<TargetBlockState>();
		outlist.add(new TargetBlockState(new BlockMatchTest(BlockRegistry.BETWEENSTONE.get()), BlockRegistry.BETWEENSTONE.get().defaultBlockState()));
		outlist.add(new TargetBlockState(new BlockMatchTest(BlockRegistry.PITSTONE.get()), BlockRegistry.PITSTONE.get().defaultBlockState()));
		return outlist;
	}
	
	public BetweenlandsCaveVegetationConfig(List<TargetBlockState> targetstates, boolean active, int x, int y, int z, boolean up, boolean down, boolean north, boolean south, boolean east, boolean west, String style, FloatProvider sizex, FloatProvider sizey, FloatProvider sizez, String type) {
		this.targetStates = targetstates;
		this.sizex = sizex;
		this.sizey = sizey;
		this.sizez = sizez;
		
		// Directions
		this.up = up;
		this.down = down;
		this.west = west;
		this.east = east;
		this.north = north;
		this.south = south;
		
		// i dont yet fully understand codec so this will just be here
		this.rawtype = type;
		
		// Get type
		if (this.rawtype.compareTo("cube") == 0)
		{
			this.type = 0;
		}
		else if (this.rawtype.compareTo("sphere") == 0)
		{
			this.type = 1;
		}
		else if (this.rawtype.compareTo("ellipse") == 0)
		{
			this.type = 2;
		}
		else
		{
			// defalt to cube
			this.type = 0;
		}
		
		// Get scan type
		this.rawstyle = style;
		this.scansizex = x;
		this.scansizey = y;
		this.scansizez = z;
		this.active = active;
		
		if (this.rawstyle.compareTo("fast") == 0)
		{
			// On first condition match return
			this.scanstyle = 0;
		}
		else if (this.rawstyle.compareTo("hybrid") == 0)
		{
			// Return closest to point
			this.scanstyle = 1;
		}
		else if (this.rawstyle.compareTo("smart") == 0)
		{
			// Return closest to point by exact hypotonuse
			this.scanstyle = 2;
		}
		else
		{
			// defalt to fast
			this.scanstyle = 0;
		}
	}
	
	// Blocks set to place vegitation onto (reusing names to make it fimilier to use)
	public static class TargetBlockState {
		public static final Codec<TargetBlockState> CODEC = RecordCodecBuilder.create((p_161039_) -> {
			return p_161039_.group(RuleTest.CODEC.fieldOf("target").forGetter((p_161043_) -> {
				return p_161043_.target;
			}), BlockState.CODEC.fieldOf("state").forGetter((p_161041_) -> {
				return p_161041_.state;
			})).apply(p_161039_, TargetBlockState::new);
		});
		public final RuleTest target;
		public final BlockState state;

		public TargetBlockState(RuleTest p_161036_, BlockState p_161037_) {
			this.target = p_161036_;
			this.state = p_161037_;
		}
	}
	
	// Check area around a point and find a place to translate placed feature
	public int[] scansample(WorldGenLevel worldgenlevel, int inX, int inY, int inZ) {
		int[] output = {0,0,0}; // X Y Z
		
		if (!this.active)
		{
			// return null to inform caller of a fail
			return null;
		}
		
		for (int x = inX - this.scansizex; x <= inX + this.scansizex; x++) {
			for (int y = inY - this.scansizey; y <= inY + this.scansizey; y++) {
				for (int z = inZ - this.scansizez; z <= inZ + this.scansizez; z++) {
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
