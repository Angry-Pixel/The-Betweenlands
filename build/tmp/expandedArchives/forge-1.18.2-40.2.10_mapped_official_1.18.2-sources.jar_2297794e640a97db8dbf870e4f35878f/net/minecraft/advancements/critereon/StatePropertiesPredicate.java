package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public class StatePropertiesPredicate {
   public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate(ImmutableList.of());
   private final List<StatePropertiesPredicate.PropertyMatcher> properties;

   private static StatePropertiesPredicate.PropertyMatcher fromJson(String p_67687_, JsonElement p_67688_) {
      if (p_67688_.isJsonPrimitive()) {
         String s2 = p_67688_.getAsString();
         return new StatePropertiesPredicate.ExactPropertyMatcher(p_67687_, s2);
      } else {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_67688_, "value");
         String s = jsonobject.has("min") ? getStringOrNull(jsonobject.get("min")) : null;
         String s1 = jsonobject.has("max") ? getStringOrNull(jsonobject.get("max")) : null;
         return (StatePropertiesPredicate.PropertyMatcher)(s != null && s.equals(s1) ? new StatePropertiesPredicate.ExactPropertyMatcher(p_67687_, s) : new StatePropertiesPredicate.RangedPropertyMatcher(p_67687_, s, s1));
      }
   }

   @Nullable
   private static String getStringOrNull(JsonElement p_67690_) {
      return p_67690_.isJsonNull() ? null : p_67690_.getAsString();
   }

   StatePropertiesPredicate(List<StatePropertiesPredicate.PropertyMatcher> p_67662_) {
      this.properties = ImmutableList.copyOf(p_67662_);
   }

   public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> p_67670_, S p_67671_) {
      for(StatePropertiesPredicate.PropertyMatcher statepropertiespredicate$propertymatcher : this.properties) {
         if (!statepropertiespredicate$propertymatcher.match(p_67670_, p_67671_)) {
            return false;
         }
      }

      return true;
   }

   public boolean matches(BlockState p_67668_) {
      return this.matches(p_67668_.getBlock().getStateDefinition(), p_67668_);
   }

   public boolean matches(FluidState p_67685_) {
      return this.matches(p_67685_.getType().getStateDefinition(), p_67685_);
   }

   public void checkState(StateDefinition<?, ?> p_67673_, Consumer<String> p_67674_) {
      this.properties.forEach((p_67678_) -> {
         p_67678_.checkState(p_67673_, p_67674_);
      });
   }

   public static StatePropertiesPredicate fromJson(@Nullable JsonElement p_67680_) {
      if (p_67680_ != null && !p_67680_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_67680_, "properties");
         List<StatePropertiesPredicate.PropertyMatcher> list = Lists.newArrayList();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            list.add(fromJson(entry.getKey(), entry.getValue()));
         }

         return new StatePropertiesPredicate(list);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.properties.isEmpty()) {
            this.properties.forEach((p_67683_) -> {
               jsonobject.add(p_67683_.getName(), p_67683_.toJson());
            });
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private final List<StatePropertiesPredicate.PropertyMatcher> matchers = Lists.newArrayList();

      private Builder() {
      }

      public static StatePropertiesPredicate.Builder properties() {
         return new StatePropertiesPredicate.Builder();
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<?> p_67701_, String p_67702_) {
         this.matchers.add(new StatePropertiesPredicate.ExactPropertyMatcher(p_67701_.getName(), p_67702_));
         return this;
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Integer> p_67695_, int p_67696_) {
         return this.hasProperty(p_67695_, Integer.toString(p_67696_));
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Boolean> p_67704_, boolean p_67705_) {
         return this.hasProperty(p_67704_, Boolean.toString(p_67705_));
      }

      public <T extends Comparable<T> & StringRepresentable> StatePropertiesPredicate.Builder hasProperty(Property<T> p_67698_, T p_67699_) {
         return this.hasProperty(p_67698_, p_67699_.getSerializedName());
      }

      public StatePropertiesPredicate build() {
         return new StatePropertiesPredicate(this.matchers);
      }
   }

   static class ExactPropertyMatcher extends StatePropertiesPredicate.PropertyMatcher {
      private final String value;

      public ExactPropertyMatcher(String p_67709_, String p_67710_) {
         super(p_67709_);
         this.value = p_67710_;
      }

      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_67713_, Property<T> p_67714_) {
         T t = p_67713_.getValue(p_67714_);
         Optional<T> optional = p_67714_.getValue(this.value);
         return optional.isPresent() && t.compareTo(optional.get()) == 0;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.value);
      }
   }

   abstract static class PropertyMatcher {
      private final String name;

      public PropertyMatcher(String p_67717_) {
         this.name = p_67717_;
      }

      public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> p_67719_, S p_67720_) {
         Property<?> property = p_67719_.getProperty(this.name);
         return property == null ? false : this.match(p_67720_, property);
      }

      protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_67724_, Property<T> p_67725_);

      public abstract JsonElement toJson();

      public String getName() {
         return this.name;
      }

      public void checkState(StateDefinition<?, ?> p_67722_, Consumer<String> p_67723_) {
         Property<?> property = p_67722_.getProperty(this.name);
         if (property == null) {
            p_67723_.accept(this.name);
         }

      }
   }

   static class RangedPropertyMatcher extends StatePropertiesPredicate.PropertyMatcher {
      @Nullable
      private final String minValue;
      @Nullable
      private final String maxValue;

      public RangedPropertyMatcher(String p_67730_, @Nullable String p_67731_, @Nullable String p_67732_) {
         super(p_67730_);
         this.minValue = p_67731_;
         this.maxValue = p_67732_;
      }

      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_67735_, Property<T> p_67736_) {
         T t = p_67735_.getValue(p_67736_);
         if (this.minValue != null) {
            Optional<T> optional = p_67736_.getValue(this.minValue);
            if (!optional.isPresent() || t.compareTo(optional.get()) < 0) {
               return false;
            }
         }

         if (this.maxValue != null) {
            Optional<T> optional1 = p_67736_.getValue(this.maxValue);
            if (!optional1.isPresent() || t.compareTo(optional1.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      public JsonElement toJson() {
         JsonObject jsonobject = new JsonObject();
         if (this.minValue != null) {
            jsonobject.addProperty("min", this.minValue);
         }

         if (this.maxValue != null) {
            jsonobject.addProperty("max", this.maxValue);
         }

         return jsonobject;
      }
   }
}