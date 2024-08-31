package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.datagen.advancements.AdventurerAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdvancementCompiler extends AdvancementProvider {

	public AdvancementCompiler(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
		super(output, registries, helper, List.of(new AdventurerAdvancementProvider()));
	}
}
