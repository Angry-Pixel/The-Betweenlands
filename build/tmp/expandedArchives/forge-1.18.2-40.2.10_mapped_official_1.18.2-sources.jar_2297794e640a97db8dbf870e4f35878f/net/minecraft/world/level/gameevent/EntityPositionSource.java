package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityPositionSource implements PositionSource {
   public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create((p_157735_) -> {
      return p_157735_.group(Codec.INT.fieldOf("source_entity_id").forGetter((p_157737_) -> {
         return p_157737_.sourceEntityId;
      })).apply(p_157735_, EntityPositionSource::new);
   });
   final int sourceEntityId;
   private Optional<Entity> sourceEntity = Optional.empty();

   public EntityPositionSource(int p_157730_) {
      this.sourceEntityId = p_157730_;
   }

   public Optional<BlockPos> getPosition(Level p_157733_) {
      if (!this.sourceEntity.isPresent()) {
         this.sourceEntity = Optional.ofNullable(p_157733_.getEntity(this.sourceEntityId));
      }

      return this.sourceEntity.map(Entity::blockPosition);
   }

   public PositionSourceType<?> getType() {
      return PositionSourceType.ENTITY;
   }

   public static class Type implements PositionSourceType<EntityPositionSource> {
      public EntityPositionSource read(FriendlyByteBuf p_157741_) {
         return new EntityPositionSource(p_157741_.readVarInt());
      }

      public void write(FriendlyByteBuf p_157743_, EntityPositionSource p_157744_) {
         p_157743_.writeVarInt(p_157744_.sourceEntityId);
      }

      public Codec<EntityPositionSource> codec() {
         return EntityPositionSource.CODEC;
      }
   }
}