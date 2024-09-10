package thebetweenlands.common.world;

import net.minecraft.world.level.block.grower.TreeGrower;
import thebetweenlands.common.registries.ConfiguredFeatureRegistry;

import java.util.Optional;

public class BLTreeGrowers {
	public static final TreeGrower WEEDWOOD = new TreeGrower("weedwood", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.WEEDWOOD_TREE), Optional.empty());
	public static final TreeGrower SAP = new TreeGrower("sap", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.SAP_TREE), Optional.empty());
	public static final TreeGrower RUBBER = new TreeGrower("rubber", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.RUBBER_TREE), Optional.empty());
	public static final TreeGrower NIBBLETWIG = new TreeGrower("nibbletwig", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.NIBBLETWIG_TREE), Optional.empty());
	public static final TreeGrower HEARTHGROVE = new TreeGrower("hearthgrove", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.HEARTHGROVE_TREE), Optional.empty());
	public static final TreeGrower SPIRIT_TREE = new TreeGrower("spirit_tree", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.SPIRIT_TREE), Optional.empty());
	public static final TreeGrower ROOT_POD = new TreeGrower("root_pod", Optional.empty(), Optional.of(ConfiguredFeatureRegistry.ROOT_POD), Optional.empty());
}
