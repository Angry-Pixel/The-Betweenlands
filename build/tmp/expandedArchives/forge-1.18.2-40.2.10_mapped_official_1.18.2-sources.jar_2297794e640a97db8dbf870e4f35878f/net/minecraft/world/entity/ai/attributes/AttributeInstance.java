package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class AttributeInstance {
   private final Attribute attribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap<>();
   private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet<>();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;
   private final Consumer<AttributeInstance> onDirty;

   public AttributeInstance(Attribute p_22097_, Consumer<AttributeInstance> p_22098_) {
      this.attribute = p_22097_;
      this.onDirty = p_22098_;
      this.baseValue = p_22097_.getDefaultValue();
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double p_22101_) {
      if (p_22101_ != this.baseValue) {
         this.baseValue = p_22101_;
         this.setDirty();
      }
   }

   public Set<AttributeModifier> getModifiers(AttributeModifier.Operation p_22105_) {
      return this.modifiersByOperation.computeIfAbsent(p_22105_, (p_22124_) -> {
         return Sets.newHashSet();
      });
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_22112_) {
      return this.modifierById.get(p_22112_);
   }

   public boolean hasModifier(AttributeModifier p_22110_) {
      return this.modifierById.get(p_22110_.getId()) != null;
   }

   private void addModifier(AttributeModifier p_22134_) {
      AttributeModifier attributemodifier = this.modifierById.putIfAbsent(p_22134_.getId(), p_22134_);
      if (attributemodifier != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(p_22134_.getOperation()).add(p_22134_);
         this.setDirty();
      }
   }

   public void addTransientModifier(AttributeModifier p_22119_) {
      this.addModifier(p_22119_);
   }

   public void addPermanentModifier(AttributeModifier p_22126_) {
      this.addModifier(p_22126_);
      this.permanentModifiers.add(p_22126_);
   }

   protected void setDirty() {
      this.dirty = true;
      this.onDirty.accept(this);
   }

   public void removeModifier(AttributeModifier p_22131_) {
      this.getModifiers(p_22131_.getOperation()).remove(p_22131_);
      this.modifierById.remove(p_22131_.getId());
      this.permanentModifiers.remove(p_22131_);
      this.setDirty();
   }

   public void removeModifier(UUID p_22121_) {
      AttributeModifier attributemodifier = this.getModifier(p_22121_);
      if (attributemodifier != null) {
         this.removeModifier(attributemodifier);
      }

   }

   public boolean removePermanentModifier(UUID p_22128_) {
      AttributeModifier attributemodifier = this.getModifier(p_22128_);
      if (attributemodifier != null && this.permanentModifiers.contains(attributemodifier)) {
         this.removeModifier(attributemodifier);
         return true;
      } else {
         return false;
      }
   }

   public void removeModifiers() {
      for(AttributeModifier attributemodifier : this.getModifiers()) {
         this.removeModifier(attributemodifier);
      }

   }

   public double getValue() {
      if (this.dirty) {
         this.cachedValue = this.calculateValue();
         this.dirty = false;
      }

      return this.cachedValue;
   }

   private double calculateValue() {
      double d0 = this.getBaseValue();

      for(AttributeModifier attributemodifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
         d0 += attributemodifier.getAmount();
      }

      double d1 = d0;

      for(AttributeModifier attributemodifier1 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
         d1 += d0 * attributemodifier1.getAmount();
      }

      for(AttributeModifier attributemodifier2 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         d1 *= 1.0D + attributemodifier2.getAmount();
      }

      return this.attribute.sanitizeValue(d1);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation p_22117_) {
      return this.modifiersByOperation.getOrDefault(p_22117_, Collections.emptySet());
   }

   public void replaceFrom(AttributeInstance p_22103_) {
      this.baseValue = p_22103_.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(p_22103_.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.addAll(p_22103_.permanentModifiers);
      this.modifiersByOperation.clear();
      p_22103_.modifiersByOperation.forEach((p_22107_, p_22108_) -> {
         this.getModifiers(p_22107_).addAll(p_22108_);
      });
      this.setDirty();
   }

   public CompoundTag save() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("Name", Registry.ATTRIBUTE.getKey(this.attribute).toString());
      compoundtag.putDouble("Base", this.baseValue);
      if (!this.permanentModifiers.isEmpty()) {
         ListTag listtag = new ListTag();

         for(AttributeModifier attributemodifier : this.permanentModifiers) {
            listtag.add(attributemodifier.save());
         }

         compoundtag.put("Modifiers", listtag);
      }

      return compoundtag;
   }

   public void load(CompoundTag p_22114_) {
      this.baseValue = p_22114_.getDouble("Base");
      if (p_22114_.contains("Modifiers", 9)) {
         ListTag listtag = p_22114_.getList("Modifiers", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            AttributeModifier attributemodifier = AttributeModifier.load(listtag.getCompound(i));
            if (attributemodifier != null) {
               this.modifierById.put(attributemodifier.getId(), attributemodifier);
               this.getModifiers(attributemodifier.getOperation()).add(attributemodifier);
               this.permanentModifiers.add(attributemodifier);
            }
         }
      }

      this.setDirty();
   }
}