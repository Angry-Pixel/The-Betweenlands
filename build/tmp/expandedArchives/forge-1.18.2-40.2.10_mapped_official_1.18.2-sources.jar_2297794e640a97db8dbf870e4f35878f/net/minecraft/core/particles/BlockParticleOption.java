package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<BlockParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<BlockParticleOption>() {
      public BlockParticleOption fromCommand(ParticleType<BlockParticleOption> p_123645_, StringReader p_123646_) throws CommandSyntaxException {
         p_123646_.expect(' ');
         return new BlockParticleOption(p_123645_, (new BlockStateParser(p_123646_, false)).parse(false).getState());
      }

      public BlockParticleOption fromNetwork(ParticleType<BlockParticleOption> p_123648_, FriendlyByteBuf p_123649_) {
         return new BlockParticleOption(p_123648_, Block.BLOCK_STATE_REGISTRY.byId(p_123649_.readVarInt()));
      }
   };
   private final ParticleType<BlockParticleOption> type;
   private final BlockState state;

   public static Codec<BlockParticleOption> codec(ParticleType<BlockParticleOption> p_123635_) {
      return BlockState.CODEC.xmap((p_123638_) -> {
         return new BlockParticleOption(p_123635_, p_123638_);
      }, (p_123633_) -> {
         return p_123633_.state;
      });
   }

   public BlockParticleOption(ParticleType<BlockParticleOption> p_123629_, BlockState p_123630_) {
      this.type = p_123629_;
      this.state = p_123630_;
   }

   public void writeToNetwork(FriendlyByteBuf p_123640_) {
      p_123640_.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(this.state));
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + BlockStateParser.serialize(this.state);
   }

   public ParticleType<BlockParticleOption> getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }

   //FORGE: Add a source pos property, so we can provide models with additional model data
   private net.minecraft.core.BlockPos pos;
   public BlockParticleOption setPos(net.minecraft.core.BlockPos pos) {
      this.pos = pos;
      return this;
   }

   public net.minecraft.core.BlockPos getPos() {
      return pos;
   }
}
