package thebetweenlands.common.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BLDamageTagProvider extends TagsProvider<DamageType> {

	public static final TagKey<DamageType> DREADFUL_PEAT_MUMMY_IMMUNE = TagKey.create(Registries.DAMAGE_TYPE, TheBetweenlands.prefix("dreadful_peat_mummy_immune"));

	public BLDamageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.DAMAGE_TYPE, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(DREADFUL_PEAT_MUMMY_IMMUNE).add(DamageTypes.FALLING_BLOCK, DamageTypes.IN_WALL).addTag(DamageTypeTags.IS_FIRE).addTag(DamageTypeTags.IS_DROWNING);
	}
}
