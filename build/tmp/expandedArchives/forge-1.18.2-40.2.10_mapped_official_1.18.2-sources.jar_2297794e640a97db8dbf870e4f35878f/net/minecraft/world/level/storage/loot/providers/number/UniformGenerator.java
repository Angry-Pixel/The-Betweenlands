package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class UniformGenerator implements NumberProvider {
   final NumberProvider min;
   final NumberProvider max;

   UniformGenerator(NumberProvider p_165777_, NumberProvider p_165778_) {
      this.min = p_165777_;
      this.max = p_165778_;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.UNIFORM;
   }

   public static UniformGenerator between(float p_165781_, float p_165782_) {
      return new UniformGenerator(ConstantValue.exactly(p_165781_), ConstantValue.exactly(p_165782_));
   }

   public int getInt(LootContext p_165784_) {
      return Mth.nextInt(p_165784_.getRandom(), this.min.getInt(p_165784_), this.max.getInt(p_165784_));
   }

   public float getFloat(LootContext p_165787_) {
      return Mth.nextFloat(p_165787_.getRandom(), this.min.getFloat(p_165787_), this.max.getFloat(p_165787_));
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.min.getReferencedContextParams(), this.max.getReferencedContextParams());
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<UniformGenerator> {
      public UniformGenerator deserialize(JsonObject p_165801_, JsonDeserializationContext p_165802_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_165801_, "min", p_165802_, NumberProvider.class);
         NumberProvider numberprovider1 = GsonHelper.getAsObject(p_165801_, "max", p_165802_, NumberProvider.class);
         return new UniformGenerator(numberprovider, numberprovider1);
      }

      public void serialize(JsonObject p_165793_, UniformGenerator p_165794_, JsonSerializationContext p_165795_) {
         p_165793_.add("min", p_165795_.serialize(p_165794_.min));
         p_165793_.add("max", p_165795_.serialize(p_165794_.max));
      }
   }
}