package net.minecraft.world.level.storage.loot.providers.score;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface ScoreboardNameProvider {
   @Nullable
   String getScoreboardName(LootContext p_165867_);

   LootScoreProviderType getType();

   Set<LootContextParam<?>> getReferencedContextParams();
}