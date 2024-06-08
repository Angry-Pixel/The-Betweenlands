package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public final class BinomialDistributionGenerator implements NumberProvider {
   final NumberProvider n;
   final NumberProvider p;

   BinomialDistributionGenerator(NumberProvider p_165656_, NumberProvider p_165657_) {
      this.n = p_165656_;
      this.p = p_165657_;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.BINOMIAL;
   }

   public int getInt(LootContext p_165663_) {
      int i = this.n.getInt(p_165663_);
      float f = this.p.getFloat(p_165663_);
      Random random = p_165663_.getRandom();
      int j = 0;

      for(int k = 0; k < i; ++k) {
         if (random.nextFloat() < f) {
            ++j;
         }
      }

      return j;
   }

   public float getFloat(LootContext p_165666_) {
      return (float)this.getInt(p_165666_);
   }

   public static BinomialDistributionGenerator binomial(int p_165660_, float p_165661_) {
      return new BinomialDistributionGenerator(ConstantValue.exactly((float)p_165660_), ConstantValue.exactly(p_165661_));
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.n.getReferencedContextParams(), this.p.getReferencedContextParams());
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BinomialDistributionGenerator> {
      public BinomialDistributionGenerator deserialize(JsonObject p_165680_, JsonDeserializationContext p_165681_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_165680_, "n", p_165681_, NumberProvider.class);
         NumberProvider numberprovider1 = GsonHelper.getAsObject(p_165680_, "p", p_165681_, NumberProvider.class);
         return new BinomialDistributionGenerator(numberprovider, numberprovider1);
      }

      public void serialize(JsonObject p_165672_, BinomialDistributionGenerator p_165673_, JsonSerializationContext p_165674_) {
         p_165672_.add("n", p_165674_.serialize(p_165673_.n));
         p_165672_.add("p", p_165674_.serialize(p_165673_.p));
      }
   }
}