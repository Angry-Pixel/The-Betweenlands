package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPartGenerator implements BlockStateGenerator {
   private final Block block;
   private final List<MultiPartGenerator.Entry> parts = Lists.newArrayList();

   private MultiPartGenerator(Block p_125202_) {
      this.block = p_125202_;
   }

   public Block getBlock() {
      return this.block;
   }

   public static MultiPartGenerator multiPart(Block p_125205_) {
      return new MultiPartGenerator(p_125205_);
   }

   public MultiPartGenerator with(List<Variant> p_125221_) {
      this.parts.add(new MultiPartGenerator.Entry(p_125221_));
      return this;
   }

   public MultiPartGenerator with(Variant p_125219_) {
      return this.with(ImmutableList.of(p_125219_));
   }

   public MultiPartGenerator with(Condition p_125213_, List<Variant> p_125214_) {
      this.parts.add(new MultiPartGenerator.ConditionalEntry(p_125213_, p_125214_));
      return this;
   }

   public MultiPartGenerator with(Condition p_125216_, Variant... p_125217_) {
      return this.with(p_125216_, ImmutableList.copyOf(p_125217_));
   }

   public MultiPartGenerator with(Condition p_125210_, Variant p_125211_) {
      return this.with(p_125210_, ImmutableList.of(p_125211_));
   }

   public JsonElement get() {
      StateDefinition<Block, BlockState> statedefinition = this.block.getStateDefinition();
      this.parts.forEach((p_125208_) -> {
         p_125208_.validate(statedefinition);
      });
      JsonArray jsonarray = new JsonArray();
      this.parts.stream().map(MultiPartGenerator.Entry::get).forEach(jsonarray::add);
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("multipart", jsonarray);
      return jsonobject;
   }

   static class ConditionalEntry extends MultiPartGenerator.Entry {
      private final Condition condition;

      ConditionalEntry(Condition p_125226_, List<Variant> p_125227_) {
         super(p_125227_);
         this.condition = p_125226_;
      }

      public void validate(StateDefinition<?, ?> p_125233_) {
         this.condition.validate(p_125233_);
      }

      public void decorate(JsonObject p_125235_) {
         p_125235_.add("when", this.condition.get());
      }
   }

   static class Entry implements Supplier<JsonElement> {
      private final List<Variant> variants;

      Entry(List<Variant> p_125238_) {
         this.variants = p_125238_;
      }

      public void validate(StateDefinition<?, ?> p_125243_) {
      }

      public void decorate(JsonObject p_125244_) {
      }

      public JsonElement get() {
         JsonObject jsonobject = new JsonObject();
         this.decorate(jsonobject);
         jsonobject.add("apply", Variant.convertList(this.variants));
         return jsonobject;
      }
   }
}