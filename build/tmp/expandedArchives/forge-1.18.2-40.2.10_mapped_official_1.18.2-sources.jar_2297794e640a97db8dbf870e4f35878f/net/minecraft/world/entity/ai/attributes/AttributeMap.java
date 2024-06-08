package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AttributeMap {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Attribute, AttributeInstance> attributes = Maps.newHashMap();
   private final Set<AttributeInstance> dirtyAttributes = Sets.newHashSet();
   private final AttributeSupplier supplier;

   public AttributeMap(AttributeSupplier p_22144_) {
      this.supplier = p_22144_;
   }

   private void onAttributeModified(AttributeInstance p_22158_) {
      if (p_22158_.getAttribute().isClientSyncable()) {
         this.dirtyAttributes.add(p_22158_);
      }

   }

   public Set<AttributeInstance> getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection<AttributeInstance> getSyncableAttributes() {
      return this.attributes.values().stream().filter((p_22184_) -> {
         return p_22184_.getAttribute().isClientSyncable();
      }).collect(Collectors.toList());
   }

   @Nullable
   public AttributeInstance getInstance(Attribute p_22147_) {
      return this.attributes.computeIfAbsent(p_22147_, (p_22188_) -> {
         return this.supplier.createInstance(this::onAttributeModified, p_22188_);
      });
   }

   public boolean hasAttribute(Attribute p_22172_) {
      return this.attributes.get(p_22172_) != null || this.supplier.hasAttribute(p_22172_);
   }

   public boolean hasModifier(Attribute p_22155_, UUID p_22156_) {
      AttributeInstance attributeinstance = this.attributes.get(p_22155_);
      return attributeinstance != null ? attributeinstance.getModifier(p_22156_) != null : this.supplier.hasModifier(p_22155_, p_22156_);
   }

   public double getValue(Attribute p_22182_) {
      AttributeInstance attributeinstance = this.attributes.get(p_22182_);
      return attributeinstance != null ? attributeinstance.getValue() : this.supplier.getValue(p_22182_);
   }

   public double getBaseValue(Attribute p_22186_) {
      AttributeInstance attributeinstance = this.attributes.get(p_22186_);
      return attributeinstance != null ? attributeinstance.getBaseValue() : this.supplier.getBaseValue(p_22186_);
   }

   public double getModifierValue(Attribute p_22174_, UUID p_22175_) {
      AttributeInstance attributeinstance = this.attributes.get(p_22174_);
      return attributeinstance != null ? attributeinstance.getModifier(p_22175_).getAmount() : this.supplier.getModifierValue(p_22174_, p_22175_);
   }

   public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> p_22162_) {
      p_22162_.asMap().forEach((p_22152_, p_22153_) -> {
         AttributeInstance attributeinstance = this.attributes.get(p_22152_);
         if (attributeinstance != null) {
            p_22153_.forEach(attributeinstance::removeModifier);
         }

      });
   }

   public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> p_22179_) {
      p_22179_.forEach((p_22149_, p_22150_) -> {
         AttributeInstance attributeinstance = this.getInstance(p_22149_);
         if (attributeinstance != null) {
            attributeinstance.removeModifier(p_22150_);
            attributeinstance.addTransientModifier(p_22150_);
         }

      });
   }

   public void assignValues(AttributeMap p_22160_) {
      p_22160_.attributes.values().forEach((p_22177_) -> {
         AttributeInstance attributeinstance = this.getInstance(p_22177_.getAttribute());
         if (attributeinstance != null) {
            attributeinstance.replaceFrom(p_22177_);
         }

      });
   }

   public ListTag save() {
      ListTag listtag = new ListTag();

      for(AttributeInstance attributeinstance : this.attributes.values()) {
         listtag.add(attributeinstance.save());
      }

      return listtag;
   }

   public void load(ListTag p_22169_) {
      for(int i = 0; i < p_22169_.size(); ++i) {
         CompoundTag compoundtag = p_22169_.getCompound(i);
         String s = compoundtag.getString("Name");
         Util.ifElse(Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(s)), (p_22167_) -> {
            AttributeInstance attributeinstance = this.getInstance(p_22167_);
            if (attributeinstance != null) {
               attributeinstance.load(compoundtag);
            }

         }, () -> {
            LOGGER.warn("Ignoring unknown attribute '{}'", (Object)s);
         });
      }

   }
}