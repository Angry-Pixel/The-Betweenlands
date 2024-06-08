package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final CommandFunction.CacheableFunction function;

   public AdvancementRewards(int p_9985_, ResourceLocation[] p_9986_, ResourceLocation[] p_9987_, CommandFunction.CacheableFunction p_9988_) {
      this.experience = p_9985_;
      this.loot = p_9986_;
      this.recipes = p_9987_;
      this.function = p_9988_;
   }

   public ResourceLocation[] getRecipes() {
      return this.recipes;
   }

   public void grant(ServerPlayer p_9990_) {
      p_9990_.giveExperiencePoints(this.experience);
      LootContext lootcontext = (new LootContext.Builder(p_9990_.getLevel())).withParameter(LootContextParams.THIS_ENTITY, p_9990_).withParameter(LootContextParams.ORIGIN, p_9990_.position()).withRandom(p_9990_.getRandom()).withLuck(p_9990_.getLuck()).create(LootContextParamSets.ADVANCEMENT_REWARD); // FORGE: luck to LootContext
      boolean flag = false;

      for(ResourceLocation resourcelocation : this.loot) {
         for(ItemStack itemstack : p_9990_.server.getLootTables().get(resourcelocation).getRandomItems(lootcontext)) {
            if (p_9990_.addItem(itemstack)) {
               p_9990_.level.playSound((Player)null, p_9990_.getX(), p_9990_.getY(), p_9990_.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((p_9990_.getRandom().nextFloat() - p_9990_.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               ItemEntity itementity = p_9990_.drop(itemstack, false);
               if (itementity != null) {
                  itementity.setNoPickUpDelay();
                  itementity.setOwner(p_9990_.getUUID());
               }
            }
         }
      }

      if (flag) {
         p_9990_.containerMenu.broadcastChanges();
      }

      if (this.recipes.length > 0) {
         p_9990_.awardRecipesByKey(this.recipes);
      }

      MinecraftServer minecraftserver = p_9990_.server;
      this.function.get(minecraftserver.getFunctions()).ifPresent((p_9996_) -> {
         minecraftserver.getFunctions().execute(p_9996_, p_9990_.createCommandSourceStack().withSuppressedOutput().withPermission(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + "}";
   }

   public JsonElement serializeToJson() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         if (this.loot.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ResourceLocation resourcelocation : this.loot) {
               jsonarray.add(resourcelocation.toString());
            }

            jsonobject.add("loot", jsonarray);
         }

         if (this.recipes.length > 0) {
            JsonArray jsonarray1 = new JsonArray();

            for(ResourceLocation resourcelocation1 : this.recipes) {
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   public static AdvancementRewards deserialize(JsonObject p_9992_) throws JsonParseException {
      int i = GsonHelper.getAsInt(p_9992_, "experience", 0);
      JsonArray jsonarray = GsonHelper.getAsJsonArray(p_9992_, "loot", new JsonArray());
      ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

      for(int j = 0; j < aresourcelocation.length; ++j) {
         aresourcelocation[j] = new ResourceLocation(GsonHelper.convertToString(jsonarray.get(j), "loot[" + j + "]"));
      }

      JsonArray jsonarray1 = GsonHelper.getAsJsonArray(p_9992_, "recipes", new JsonArray());
      ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

      for(int k = 0; k < aresourcelocation1.length; ++k) {
         aresourcelocation1[k] = new ResourceLocation(GsonHelper.convertToString(jsonarray1.get(k), "recipes[" + k + "]"));
      }

      CommandFunction.CacheableFunction commandfunction$cacheablefunction;
      if (p_9992_.has("function")) {
         commandfunction$cacheablefunction = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(p_9992_, "function")));
      } else {
         commandfunction$cacheablefunction = CommandFunction.CacheableFunction.NONE;
      }

      return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, commandfunction$cacheablefunction);
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int p_10006_) {
         return (new AdvancementRewards.Builder()).addExperience(p_10006_);
      }

      public AdvancementRewards.Builder addExperience(int p_10008_) {
         this.experience += p_10008_;
         return this;
      }

      public static AdvancementRewards.Builder loot(ResourceLocation p_144823_) {
         return (new AdvancementRewards.Builder()).addLootTable(p_144823_);
      }

      public AdvancementRewards.Builder addLootTable(ResourceLocation p_144825_) {
         this.loot.add(p_144825_);
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation p_10010_) {
         return (new AdvancementRewards.Builder()).addRecipe(p_10010_);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation p_10012_) {
         this.recipes.add(p_10012_);
         return this;
      }

      public static AdvancementRewards.Builder function(ResourceLocation p_144827_) {
         return (new AdvancementRewards.Builder()).runs(p_144827_);
      }

      public AdvancementRewards.Builder runs(ResourceLocation p_144829_) {
         this.function = p_144829_;
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.toArray(new ResourceLocation[0]), this.recipes.toArray(new ResourceLocation[0]), this.function == null ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
      }
   }
}
