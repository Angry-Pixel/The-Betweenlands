package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(int p_133580_, Collection<AttributeInstance> p_133581_) {
      this.entityId = p_133580_;
      this.attributes = Lists.newArrayList();

      for(AttributeInstance attributeinstance : p_133581_) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(attributeinstance.getAttribute(), attributeinstance.getBaseValue(), attributeinstance.getModifiers()));
      }

   }

   public ClientboundUpdateAttributesPacket(FriendlyByteBuf p_179447_) {
      this.entityId = p_179447_.readVarInt();
      this.attributes = p_179447_.readList((p_179455_) -> {
         ResourceLocation resourcelocation = p_179455_.readResourceLocation();
         Attribute attribute = Registry.ATTRIBUTE.get(resourcelocation);
         double d0 = p_179455_.readDouble();
         List<AttributeModifier> list = p_179455_.readList((p_179457_) -> {
            return new AttributeModifier(p_179457_.readUUID(), "Unknown synced attribute modifier", p_179457_.readDouble(), AttributeModifier.Operation.fromValue(p_179457_.readByte()));
         });
         return new ClientboundUpdateAttributesPacket.AttributeSnapshot(attribute, d0, list);
      });
   }

   public void write(FriendlyByteBuf p_133590_) {
      p_133590_.writeVarInt(this.entityId);
      p_133590_.writeCollection(this.attributes, (p_179452_, p_179453_) -> {
         p_179452_.writeResourceLocation(Registry.ATTRIBUTE.getKey(p_179453_.getAttribute()));
         p_179452_.writeDouble(p_179453_.getBase());
         p_179452_.writeCollection(p_179453_.getModifiers(), (p_179449_, p_179450_) -> {
            p_179449_.writeUUID(p_179450_.getId());
            p_179449_.writeDouble(p_179450_.getAmount());
            p_179449_.writeByte(p_179450_.getOperation().toValue());
         });
      });
   }

   public void handle(ClientGamePacketListener p_133587_) {
      p_133587_.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<ClientboundUpdateAttributesPacket.AttributeSnapshot> getValues() {
      return this.attributes;
   }

   public static class AttributeSnapshot {
      private final Attribute attribute;
      private final double base;
      private final Collection<AttributeModifier> modifiers;

      public AttributeSnapshot(Attribute p_179459_, double p_179460_, Collection<AttributeModifier> p_179461_) {
         this.attribute = p_179459_;
         this.base = p_179460_;
         this.modifiers = p_179461_;
      }

      public Attribute getAttribute() {
         return this.attribute;
      }

      public double getBase() {
         return this.base;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}