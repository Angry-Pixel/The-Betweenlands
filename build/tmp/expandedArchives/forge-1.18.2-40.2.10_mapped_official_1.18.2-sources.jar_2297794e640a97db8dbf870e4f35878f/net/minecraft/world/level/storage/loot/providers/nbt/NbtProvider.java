package net.minecraft.world.level.storage.loot.providers.nbt;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface NbtProvider {
   @Nullable
   Tag get(LootContext p_165622_);

   Set<LootContextParam<?>> getReferencedContextParams();

   LootNbtProviderType getType();
}