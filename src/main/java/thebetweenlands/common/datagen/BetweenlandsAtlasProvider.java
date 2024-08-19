package thebetweenlands.common.datagen;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import thebetweenlands.common.TheBetweenlands;

import java.util.concurrent.CompletableFuture;

public class BetweenlandsAtlasProvider extends SpriteSourceProvider {
	public BetweenlandsAtlasProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
		super(output, lookupProvider, TheBetweenlands.ID, helper);
	}

	@Override
	protected void gather() {
		this.atlas(TheBetweenlands.prefix("aspect_icons")).addSource(new DirectoryLister("aspect", ""));
	}
}
