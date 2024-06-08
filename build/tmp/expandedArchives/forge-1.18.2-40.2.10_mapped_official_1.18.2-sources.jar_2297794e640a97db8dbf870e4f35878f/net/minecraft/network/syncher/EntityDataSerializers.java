package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
   public static final EntityDataSerializer<Byte> BYTE = new EntityDataSerializer<Byte>() {
      public void write(FriendlyByteBuf p_135062_, Byte p_135063_) {
         p_135062_.writeByte(p_135063_);
      }

      public Byte read(FriendlyByteBuf p_135068_) {
         return p_135068_.readByte();
      }

      public Byte copy(Byte p_135056_) {
         return p_135056_;
      }
   };
   public static final EntityDataSerializer<Integer> INT = new EntityDataSerializer<Integer>() {
      public void write(FriendlyByteBuf p_135107_, Integer p_135108_) {
         p_135107_.writeVarInt(p_135108_);
      }

      public Integer read(FriendlyByteBuf p_135113_) {
         return p_135113_.readVarInt();
      }

      public Integer copy(Integer p_135101_) {
         return p_135101_;
      }
   };
   public static final EntityDataSerializer<Float> FLOAT = new EntityDataSerializer<Float>() {
      public void write(FriendlyByteBuf p_135122_, Float p_135123_) {
         p_135122_.writeFloat(p_135123_);
      }

      public Float read(FriendlyByteBuf p_135128_) {
         return p_135128_.readFloat();
      }

      public Float copy(Float p_135116_) {
         return p_135116_;
      }
   };
   public static final EntityDataSerializer<String> STRING = new EntityDataSerializer<String>() {
      public void write(FriendlyByteBuf p_135140_, String p_135141_) {
         p_135140_.writeUtf(p_135141_);
      }

      public String read(FriendlyByteBuf p_135143_) {
         return p_135143_.readUtf();
      }

      public String copy(String p_135133_) {
         return p_135133_;
      }
   };
   public static final EntityDataSerializer<Component> COMPONENT = new EntityDataSerializer<Component>() {
      public void write(FriendlyByteBuf p_135153_, Component p_135154_) {
         p_135153_.writeComponent(p_135154_);
      }

      public Component read(FriendlyByteBuf p_135158_) {
         return p_135158_.readComponent();
      }

      public Component copy(Component p_135156_) {
         return p_135156_;
      }
   };
   public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = new EntityDataSerializer<Optional<Component>>() {
      public void write(FriendlyByteBuf p_135170_, Optional<Component> p_135171_) {
         if (p_135171_.isPresent()) {
            p_135170_.writeBoolean(true);
            p_135170_.writeComponent(p_135171_.get());
         } else {
            p_135170_.writeBoolean(false);
         }

      }

      public Optional<Component> read(FriendlyByteBuf p_135173_) {
         return p_135173_.readBoolean() ? Optional.of(p_135173_.readComponent()) : Optional.empty();
      }

      public Optional<Component> copy(Optional<Component> p_135163_) {
         return p_135163_;
      }
   };
   public static final EntityDataSerializer<ItemStack> ITEM_STACK = new EntityDataSerializer<ItemStack>() {
      public void write(FriendlyByteBuf p_135182_, ItemStack p_135183_) {
         p_135182_.writeItem(p_135183_);
      }

      public ItemStack read(FriendlyByteBuf p_135188_) {
         return p_135188_.readItem();
      }

      public ItemStack copy(ItemStack p_135176_) {
         return p_135176_.copy();
      }
   };
   public static final EntityDataSerializer<Optional<BlockState>> BLOCK_STATE = new EntityDataSerializer<Optional<BlockState>>() {
      public void write(FriendlyByteBuf p_135200_, Optional<BlockState> p_135201_) {
         if (p_135201_.isPresent()) {
            p_135200_.writeVarInt(Block.getId(p_135201_.get()));
         } else {
            p_135200_.writeVarInt(0);
         }

      }

      public Optional<BlockState> read(FriendlyByteBuf p_135203_) {
         int i = p_135203_.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.stateById(i));
      }

      public Optional<BlockState> copy(Optional<BlockState> p_135193_) {
         return p_135193_;
      }
   };
   public static final EntityDataSerializer<Boolean> BOOLEAN = new EntityDataSerializer<Boolean>() {
      public void write(FriendlyByteBuf p_135212_, Boolean p_135213_) {
         p_135212_.writeBoolean(p_135213_);
      }

      public Boolean read(FriendlyByteBuf p_135218_) {
         return p_135218_.readBoolean();
      }

      public Boolean copy(Boolean p_135206_) {
         return p_135206_;
      }
   };
   public static final EntityDataSerializer<ParticleOptions> PARTICLE = new EntityDataSerializer<ParticleOptions>() {
      public void write(FriendlyByteBuf p_135227_, ParticleOptions p_135228_) {
         p_135227_.writeVarInt(Registry.PARTICLE_TYPE.getId(p_135228_.getType()));
         p_135228_.writeToNetwork(p_135227_);
      }

      public ParticleOptions read(FriendlyByteBuf p_135236_) {
         return this.readParticle(p_135236_, Registry.PARTICLE_TYPE.byId(p_135236_.readVarInt()));
      }

      private <T extends ParticleOptions> T readParticle(FriendlyByteBuf p_135230_, ParticleType<T> p_135231_) {
         return p_135231_.getDeserializer().fromNetwork(p_135231_, p_135230_);
      }

      public ParticleOptions copy(ParticleOptions p_135221_) {
         return p_135221_;
      }
   };
   public static final EntityDataSerializer<Rotations> ROTATIONS = new EntityDataSerializer<Rotations>() {
      public void write(FriendlyByteBuf p_135245_, Rotations p_135246_) {
         p_135245_.writeFloat(p_135246_.getX());
         p_135245_.writeFloat(p_135246_.getY());
         p_135245_.writeFloat(p_135246_.getZ());
      }

      public Rotations read(FriendlyByteBuf p_135251_) {
         return new Rotations(p_135251_.readFloat(), p_135251_.readFloat(), p_135251_.readFloat());
      }

      public Rotations copy(Rotations p_135239_) {
         return p_135239_;
      }
   };
   public static final EntityDataSerializer<BlockPos> BLOCK_POS = new EntityDataSerializer<BlockPos>() {
      public void write(FriendlyByteBuf p_135260_, BlockPos p_135261_) {
         p_135260_.writeBlockPos(p_135261_);
      }

      public BlockPos read(FriendlyByteBuf p_135266_) {
         return p_135266_.readBlockPos();
      }

      public BlockPos copy(BlockPos p_135254_) {
         return p_135254_;
      }
   };
   public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new EntityDataSerializer<Optional<BlockPos>>() {
      public void write(FriendlyByteBuf p_135278_, Optional<BlockPos> p_135279_) {
         p_135278_.writeBoolean(p_135279_.isPresent());
         if (p_135279_.isPresent()) {
            p_135278_.writeBlockPos(p_135279_.get());
         }

      }

      public Optional<BlockPos> read(FriendlyByteBuf p_135281_) {
         return !p_135281_.readBoolean() ? Optional.empty() : Optional.of(p_135281_.readBlockPos());
      }

      public Optional<BlockPos> copy(Optional<BlockPos> p_135271_) {
         return p_135271_;
      }
   };
   public static final EntityDataSerializer<Direction> DIRECTION = new EntityDataSerializer<Direction>() {
      public void write(FriendlyByteBuf p_135290_, Direction p_135291_) {
         p_135290_.writeEnum(p_135291_);
      }

      public Direction read(FriendlyByteBuf p_135296_) {
         return p_135296_.readEnum(Direction.class);
      }

      public Direction copy(Direction p_135284_) {
         return p_135284_;
      }
   };
   public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = new EntityDataSerializer<Optional<UUID>>() {
      public void write(FriendlyByteBuf p_135308_, Optional<UUID> p_135309_) {
         p_135308_.writeBoolean(p_135309_.isPresent());
         if (p_135309_.isPresent()) {
            p_135308_.writeUUID(p_135309_.get());
         }

      }

      public Optional<UUID> read(FriendlyByteBuf p_135311_) {
         return !p_135311_.readBoolean() ? Optional.empty() : Optional.of(p_135311_.readUUID());
      }

      public Optional<UUID> copy(Optional<UUID> p_135301_) {
         return p_135301_;
      }
   };
   public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
      public void write(FriendlyByteBuf p_135323_, CompoundTag p_135324_) {
         p_135323_.writeNbt(p_135324_);
      }

      public CompoundTag read(FriendlyByteBuf p_135326_) {
         return p_135326_.readNbt();
      }

      public CompoundTag copy(CompoundTag p_135316_) {
         return p_135316_.copy();
      }
   };
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = new EntityDataSerializer<VillagerData>() {
      public void write(FriendlyByteBuf p_135335_, VillagerData p_135336_) {
         p_135335_.writeVarInt(Registry.VILLAGER_TYPE.getId(p_135336_.getType()));
         p_135335_.writeVarInt(Registry.VILLAGER_PROFESSION.getId(p_135336_.getProfession()));
         p_135335_.writeVarInt(p_135336_.getLevel());
      }

      public VillagerData read(FriendlyByteBuf p_135341_) {
         return new VillagerData(Registry.VILLAGER_TYPE.byId(p_135341_.readVarInt()), Registry.VILLAGER_PROFESSION.byId(p_135341_.readVarInt()), p_135341_.readVarInt());
      }

      public VillagerData copy(VillagerData p_135329_) {
         return p_135329_;
      }
   };
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new EntityDataSerializer<OptionalInt>() {
      public void write(FriendlyByteBuf p_135080_, OptionalInt p_135081_) {
         p_135080_.writeVarInt(p_135081_.orElse(-1) + 1);
      }

      public OptionalInt read(FriendlyByteBuf p_135083_) {
         int i = p_135083_.readVarInt();
         return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
      }

      public OptionalInt copy(OptionalInt p_135073_) {
         return p_135073_;
      }
   };
   public static final EntityDataSerializer<Pose> POSE = new EntityDataSerializer<Pose>() {
      public void write(FriendlyByteBuf p_135092_, Pose p_135093_) {
         p_135092_.writeEnum(p_135093_);
      }

      public Pose read(FriendlyByteBuf p_135098_) {
         return p_135098_.readEnum(Pose.class);
      }

      public Pose copy(Pose p_135086_) {
         return p_135086_;
      }
   };

   public static void registerSerializer(EntityDataSerializer<?> p_135051_) {
      if (SERIALIZERS.add(p_135051_) >= 256) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
   }

   @Nullable
   public static EntityDataSerializer<?> getSerializer(int p_135049_) {
      return net.minecraftforge.common.ForgeHooks.getSerializer(p_135049_, SERIALIZERS);
   }

   public static int getSerializedId(EntityDataSerializer<?> p_135053_) {
      return net.minecraftforge.common.ForgeHooks.getSerializerId(p_135053_, SERIALIZERS);
   }

   private EntityDataSerializers() {
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
   }
}
