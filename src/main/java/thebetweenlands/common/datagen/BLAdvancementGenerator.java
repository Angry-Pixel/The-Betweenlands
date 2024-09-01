package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.datagen.advancements.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BLAdvancementGenerator extends AdvancementProvider {

	public BLAdvancementGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
		super(output, registries, helper, List.of(
			new AdventurerAdvancementProvider(),
			new CraftsmanAdvancementProvider(),
			new FarmerAdvancementProvider(),
			new FighterAdvancementProvider(),
			new HerbalistAdvancementProvider(),
			new MinerAdvancementProvider(),
			new SurvivalistAdvancementProvider()));
	}
}
