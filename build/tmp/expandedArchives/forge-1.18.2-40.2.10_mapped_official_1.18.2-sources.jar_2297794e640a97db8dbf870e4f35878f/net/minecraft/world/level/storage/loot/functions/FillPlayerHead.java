package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead extends LootItemConditionalFunction {
   final LootContext.EntityTarget entityTarget;

   public FillPlayerHead(LootItemCondition[] p_80604_, LootContext.EntityTarget p_80605_) {
      super(p_80604_);
      this.entityTarget = p_80605_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.FILL_PLAYER_HEAD;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public ItemStack run(ItemStack p_80608_, LootContext p_80609_) {
      if (p_80608_.is(Items.PLAYER_HEAD)) {
         Entity entity = p_80609_.getParamOrNull(this.entityTarget.getParam());
         if (entity instanceof Player) {
            GameProfile gameprofile = ((Player)entity).getGameProfile();
            p_80608_.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
         }
      }

      return p_80608_;
   }

   public static LootItemConditionalFunction.Builder<?> fillPlayerHead(LootContext.EntityTarget p_165208_) {
      return simpleBuilder((p_165211_) -> {
         return new FillPlayerHead(p_165211_, p_165208_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<FillPlayerHead> {
      public void serialize(JsonObject p_80619_, FillPlayerHead p_80620_, JsonSerializationContext p_80621_) {
         super.serialize(p_80619_, p_80620_, p_80621_);
         p_80619_.add("entity", p_80621_.serialize(p_80620_.entityTarget));
      }

      public FillPlayerHead deserialize(JsonObject p_80615_, JsonDeserializationContext p_80616_, LootItemCondition[] p_80617_) {
         LootContext.EntityTarget lootcontext$entitytarget = GsonHelper.getAsObject(p_80615_, "entity", p_80616_, LootContext.EntityTarget.class);
         return new FillPlayerHead(p_80617_, lootcontext$entitytarget);
      }
   }
}