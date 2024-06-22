package thebetweenlands.common.features;

import java.util.List;

import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class BetweenlandsOreConfiguration extends OreConfiguration {

	boolean update = false;

	public BetweenlandsOreConfiguration(List<TargetBlockState> p_161016_, int p_161017_, float p_161018_, boolean scheduleUpdateOnPlace) {
		super(p_161016_, p_161017_, p_161018_);
		update = scheduleUpdateOnPlace;
	}

	public BetweenlandsOreConfiguration(List<TargetBlockState> p_161016_, int p_161017_, float p_161018_) {
		super(p_161016_, p_161017_, p_161018_);
	}

}
