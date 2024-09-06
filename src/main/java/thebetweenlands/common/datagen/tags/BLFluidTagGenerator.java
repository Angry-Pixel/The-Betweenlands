package thebetweenlands.common.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.FluidRegistry;

import java.util.concurrent.CompletableFuture;

public class BLFluidTagGenerator extends FluidTagsProvider {

	public static final TagKey<Fluid> SWAMP_WATER = tag("swamp_water");
	public static final TagKey<Fluid> STAGNANT_WATER = tag("stagnant_water");
	public static final TagKey<Fluid> TAR = tag("tar");
	public static final TagKey<Fluid> RUBBER = tag("rubber");
	public static final TagKey<Fluid> FOG = tag("fog");
	public static final TagKey<Fluid> SHALLOWBREATH = tag("shallowbreath");
	public static final TagKey<Fluid> CLEAN_WATER = tag("clean_water");
	public static final TagKey<Fluid> FISH_OIL = tag("fish_oil");

	public static final TagKey<Fluid> NETTLE_SOUP = tag("nettle_soup");
	public static final TagKey<Fluid> NETTLE_TEA = tag("nettle_tea");
	public static final TagKey<Fluid> PHEROMONE_EXTRACT = tag("pheromone_extract");
	public static final TagKey<Fluid> SWAMP_BROTH = tag("swamp_broth");
	public static final TagKey<Fluid> STURDY_STOCK = tag("sturdy_stock");
	public static final TagKey<Fluid> PEAR_CORDIAL = tag("pear_cordial");
	public static final TagKey<Fluid> SHAMANS_BREW = tag("shamans_brew");
	public static final TagKey<Fluid> LAKE_BROTH = tag("lake_broth");
	public static final TagKey<Fluid> SHELL_STOCK = tag("shell_stock");
	public static final TagKey<Fluid> FROG_LEG_EXTRACT = tag("frog_leg_extract");
	public static final TagKey<Fluid> WITCH_TEA =  tag("witch_tea");

	public BLFluidTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(SWAMP_WATER).add(FluidRegistry.SWAMP_WATER_STILL.get(), FluidRegistry.SWAMP_WATER_FLOW.get());
		this.tag(STAGNANT_WATER).add(FluidRegistry.STAGNANT_WATER_STILL.get(), FluidRegistry.STAGNANT_WATER_FLOW.get());
		this.tag(TAR).add(FluidRegistry.TAR_STILL.get(), FluidRegistry.TAR_FLOW.get());
		this.tag(RUBBER).add(FluidRegistry.RUBBER_STILL.get(), FluidRegistry.RUBBER_FLOW.get());
		this.tag(FOG).add(FluidRegistry.FOG_STILL.get(), FluidRegistry.FOG_FLOW.get());
		this.tag(SHALLOWBREATH).add(FluidRegistry.SHALLOWBREATH_STILL.get(), FluidRegistry.SHALLOWBREATH_FLOW.get());
		this.tag(CLEAN_WATER).add(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.CLEAN_WATER_FLOW.get());
		this.tag(FISH_OIL).add(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.FISH_OIL_FLOW.get());

		this.tag(NETTLE_SOUP).add(FluidRegistry.NETTLE_SOUP_STILL.get(), FluidRegistry.NETTLE_SOUP_FLOW.get());
		this.tag(NETTLE_TEA).add(FluidRegistry.NETTLE_TEA_STILL.get(), FluidRegistry.NETTLE_TEA_FLOW.get());
		this.tag(PHEROMONE_EXTRACT).add(FluidRegistry.PHEROMONE_EXTRACT_STILL.get(), FluidRegistry.PHEROMONE_EXTRACT_FLOW.get());
		this.tag(SWAMP_BROTH).add(FluidRegistry.SWAMP_BROTH_STILL.get(), FluidRegistry.SWAMP_BROTH_FLOW.get());
		this.tag(STURDY_STOCK).add(FluidRegistry.STURDY_STOCK_STILL.get(), FluidRegistry.STURDY_STOCK_FLOW.get());
		this.tag(PEAR_CORDIAL).add(FluidRegistry.PEAR_CORDIAL_STILL.get(), FluidRegistry.PEAR_CORDIAL_FLOW.get());
		this.tag(SHAMANS_BREW).add(FluidRegistry.SHAMANS_BREW_STILL.get(), FluidRegistry.SHAMANS_BREW_FLOW.get());
		this.tag(LAKE_BROTH).add(FluidRegistry.LAKE_BROTH_STILL.get(), FluidRegistry.LAKE_BROTH_FLOW.get());
		this.tag(SHELL_STOCK).add(FluidRegistry.SHELL_STOCK_STILL.get(), FluidRegistry.SHELL_STOCK_FLOW.get());
		this.tag(FROG_LEG_EXTRACT).add(FluidRegistry.FROG_LEG_EXTRACT_STILL.get(), FluidRegistry.FROG_LEG_EXTRACT_FLOW.get());
		this.tag(WITCH_TEA).add(FluidRegistry.WITCH_TEA_STILL.get(), FluidRegistry.WITCH_TEA_FLOW.get());
	}

	private static TagKey<Fluid> tag(String tagName) {
		return FluidTags.create(TheBetweenlands.prefix(tagName));
	}
}
