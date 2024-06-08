package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Condition extends Supplier<JsonElement> {
   void validate(StateDefinition<?, ?> p_125136_);

   static Condition.TerminalCondition condition() {
      return new Condition.TerminalCondition();
   }

   static Condition and(Condition... p_176294_) {
      return new Condition.CompositeCondition(Condition.Operation.AND, Arrays.asList(p_176294_));
   }

   static Condition or(Condition... p_125138_) {
      return new Condition.CompositeCondition(Condition.Operation.OR, Arrays.asList(p_125138_));
   }

   public static class CompositeCondition implements Condition {
      private final Condition.Operation operation;
      private final List<Condition> subconditions;

      CompositeCondition(Condition.Operation p_125142_, List<Condition> p_125143_) {
         this.operation = p_125142_;
         this.subconditions = p_125143_;
      }

      public void validate(StateDefinition<?, ?> p_125149_) {
         this.subconditions.forEach((p_125152_) -> {
            p_125152_.validate(p_125149_);
         });
      }

      public JsonElement get() {
         JsonArray jsonarray = new JsonArray();
         this.subconditions.stream().map(Supplier::get).forEach(jsonarray::add);
         JsonObject jsonobject = new JsonObject();
         jsonobject.add(this.operation.id, jsonarray);
         return jsonobject;
      }
   }

   public static enum Operation {
      AND("AND"),
      OR("OR");

      final String id;

      private Operation(String p_125163_) {
         this.id = p_125163_;
      }
   }

   public static class TerminalCondition implements Condition {
      private final Map<Property<?>, String> terms = Maps.newHashMap();

      private static <T extends Comparable<T>> String joinValues(Property<T> p_125187_, Stream<T> p_125188_) {
         return p_125188_.map(p_125187_::getName).collect(Collectors.joining("|"));
      }

      private static <T extends Comparable<T>> String getTerm(Property<T> p_125195_, T p_125196_, T[] p_125197_) {
         return joinValues(p_125195_, Stream.concat(Stream.of(p_125196_), Stream.of(p_125197_)));
      }

      private <T extends Comparable<T>> void putValue(Property<T> p_125184_, String p_125185_) {
         String s = this.terms.put(p_125184_, p_125185_);
         if (s != null) {
            throw new IllegalStateException("Tried to replace " + p_125184_ + " value from " + s + " to " + p_125185_);
         }
      }

      public final <T extends Comparable<T>> Condition.TerminalCondition term(Property<T> p_125177_, T p_125178_) {
         this.putValue(p_125177_, p_125177_.getName(p_125178_));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> Condition.TerminalCondition term(Property<T> p_125180_, T p_125181_, T... p_125182_) {
         this.putValue(p_125180_, getTerm(p_125180_, p_125181_, p_125182_));
         return this;
      }

      public final <T extends Comparable<T>> Condition.TerminalCondition negatedTerm(Property<T> p_176297_, T p_176298_) {
         this.putValue(p_176297_, "!" + p_176297_.getName(p_176298_));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> Condition.TerminalCondition negatedTerm(Property<T> p_176300_, T p_176301_, T... p_176302_) {
         this.putValue(p_176300_, "!" + getTerm(p_176300_, p_176301_, p_176302_));
         return this;
      }

      public JsonElement get() {
         JsonObject jsonobject = new JsonObject();
         this.terms.forEach((p_125191_, p_125192_) -> {
            jsonobject.addProperty(p_125191_.getName(), p_125192_);
         });
         return jsonobject;
      }

      public void validate(StateDefinition<?, ?> p_125172_) {
         List<Property<?>> list = this.terms.keySet().stream().filter((p_125175_) -> {
            return p_125172_.getProperty(p_125175_.getName()) != p_125175_;
         }).collect(Collectors.toList());
         if (!list.isEmpty()) {
            throw new IllegalStateException("Properties " + list + " are missing from " + p_125172_);
         }
      }
   }
}