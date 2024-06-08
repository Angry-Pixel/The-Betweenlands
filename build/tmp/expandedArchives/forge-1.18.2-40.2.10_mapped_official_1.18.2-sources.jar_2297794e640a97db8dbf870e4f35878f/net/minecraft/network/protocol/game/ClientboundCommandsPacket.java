package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandsPacket implements Packet<ClientGamePacketListener> {
   private static final byte MASK_TYPE = 3;
   private static final byte FLAG_EXECUTABLE = 4;
   private static final byte FLAG_REDIRECT = 8;
   private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
   private static final byte TYPE_ROOT = 0;
   private static final byte TYPE_LITERAL = 1;
   private static final byte TYPE_ARGUMENT = 2;
   private final RootCommandNode<SharedSuggestionProvider> root;

   public ClientboundCommandsPacket(RootCommandNode<SharedSuggestionProvider> p_131861_) {
      this.root = p_131861_;
   }

   public ClientboundCommandsPacket(FriendlyByteBuf p_178805_) {
      List<ClientboundCommandsPacket.Entry> list = p_178805_.readList(ClientboundCommandsPacket::readNode);
      resolveEntries(list);
      int i = p_178805_.readVarInt();
      this.root = (RootCommandNode)(list.get(i)).node;
   }

   public void write(FriendlyByteBuf p_131886_) {
      Object2IntMap<CommandNode<SharedSuggestionProvider>> object2intmap = enumerateNodes(this.root);
      List<CommandNode<SharedSuggestionProvider>> list = getNodesInIdOrder(object2intmap);
      p_131886_.writeCollection(list, (p_178810_, p_178811_) -> {
         writeNode(p_178810_, p_178811_, object2intmap);
      });
      p_131886_.writeVarInt(object2intmap.get(this.root));
   }

   private static void resolveEntries(List<ClientboundCommandsPacket.Entry> p_178813_) {
      List<ClientboundCommandsPacket.Entry> list = Lists.newArrayList(p_178813_);

      while(!list.isEmpty()) {
         boolean flag = list.removeIf((p_178816_) -> {
            return p_178816_.build(p_178813_);
         });
         if (!flag) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

   }

   private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> p_131863_) {
      Object2IntMap<CommandNode<SharedSuggestionProvider>> object2intmap = new Object2IntOpenHashMap<>();
      Queue<CommandNode<SharedSuggestionProvider>> queue = Queues.newArrayDeque();
      queue.add(p_131863_);

      CommandNode<SharedSuggestionProvider> commandnode;
      while((commandnode = queue.poll()) != null) {
         if (!object2intmap.containsKey(commandnode)) {
            int i = object2intmap.size();
            object2intmap.put(commandnode, i);
            queue.addAll(commandnode.getChildren());
            if (commandnode.getRedirect() != null) {
               queue.add(commandnode.getRedirect());
            }
         }
      }

      return object2intmap;
   }

   private static List<CommandNode<SharedSuggestionProvider>> getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>> p_178807_) {
      ObjectArrayList<CommandNode<SharedSuggestionProvider>> objectarraylist = new ObjectArrayList<>(p_178807_.size());
      objectarraylist.size(p_178807_.size());

      for(Object2IntMap.Entry<CommandNode<SharedSuggestionProvider>> entry : Object2IntMaps.fastIterable(p_178807_)) {
         objectarraylist.set(entry.getIntValue(), entry.getKey());
      }

      return objectarraylist;
   }

   private static ClientboundCommandsPacket.Entry readNode(FriendlyByteBuf p_131888_) {
      byte b0 = p_131888_.readByte();
      int[] aint = p_131888_.readVarIntArray();
      int i = (b0 & 8) != 0 ? p_131888_.readVarInt() : 0;
      ArgumentBuilder<SharedSuggestionProvider, ?> argumentbuilder = createBuilder(p_131888_, b0);
      return new ClientboundCommandsPacket.Entry(argumentbuilder, b0, i, aint);
   }

   @Nullable
   private static ArgumentBuilder<SharedSuggestionProvider, ?> createBuilder(FriendlyByteBuf p_131869_, byte p_131870_) {
      int i = p_131870_ & 3;
      if (i == 2) {
         String s = p_131869_.readUtf();
         ArgumentType<?> argumenttype = ArgumentTypes.deserialize(p_131869_);
         if (argumenttype == null) {
            return null;
         } else {
            RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);
            if ((p_131870_ & 16) != 0) {
               requiredargumentbuilder.suggests(SuggestionProviders.getProvider(p_131869_.readResourceLocation()));
            }

            return requiredargumentbuilder;
         }
      } else {
         return i == 1 ? LiteralArgumentBuilder.literal(p_131869_.readUtf()) : null;
      }
   }

   private static void writeNode(FriendlyByteBuf p_131872_, CommandNode<SharedSuggestionProvider> p_131873_, Map<CommandNode<SharedSuggestionProvider>, Integer> p_131874_) {
      byte b0 = 0;
      if (p_131873_.getRedirect() != null) {
         b0 = (byte)(b0 | 8);
      }

      if (p_131873_.getCommand() != null) {
         b0 = (byte)(b0 | 4);
      }

      if (p_131873_ instanceof RootCommandNode) {
         b0 = (byte)(b0 | 0);
      } else if (p_131873_ instanceof ArgumentCommandNode) {
         b0 = (byte)(b0 | 2);
         if (((ArgumentCommandNode)p_131873_).getCustomSuggestions() != null) {
            b0 = (byte)(b0 | 16);
         }
      } else {
         if (!(p_131873_ instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + p_131873_);
         }

         b0 = (byte)(b0 | 1);
      }

      p_131872_.writeByte(b0);
      p_131872_.writeVarInt(p_131873_.getChildren().size());

      for(CommandNode<SharedSuggestionProvider> commandnode : p_131873_.getChildren()) {
         p_131872_.writeVarInt(p_131874_.get(commandnode));
      }

      if (p_131873_.getRedirect() != null) {
         p_131872_.writeVarInt(p_131874_.get(p_131873_.getRedirect()));
      }

      if (p_131873_ instanceof ArgumentCommandNode) {
         ArgumentCommandNode<SharedSuggestionProvider, ?> argumentcommandnode = (ArgumentCommandNode)p_131873_;
         p_131872_.writeUtf(argumentcommandnode.getName());
         ArgumentTypes.serialize(p_131872_, argumentcommandnode.getType());
         if (argumentcommandnode.getCustomSuggestions() != null) {
            p_131872_.writeResourceLocation(SuggestionProviders.getName(argumentcommandnode.getCustomSuggestions()));
         }
      } else if (p_131873_ instanceof LiteralCommandNode) {
         p_131872_.writeUtf(((LiteralCommandNode)p_131873_).getLiteral());
      }

   }

   public void handle(ClientGamePacketListener p_131878_) {
      p_131878_.handleCommands(this);
   }

   public RootCommandNode<SharedSuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<SharedSuggestionProvider, ?> builder;
      private final byte flags;
      private final int redirect;
      private final int[] children;
      @Nullable
      CommandNode<SharedSuggestionProvider> node;

      Entry(@Nullable ArgumentBuilder<SharedSuggestionProvider, ?> p_131895_, byte p_131896_, int p_131897_, int[] p_131898_) {
         this.builder = p_131895_;
         this.flags = p_131896_;
         this.redirect = p_131897_;
         this.children = p_131898_;
      }

      public boolean build(List<ClientboundCommandsPacket.Entry> p_178818_) {
         if (this.node == null) {
            if (this.builder == null) {
               this.node = new RootCommandNode<>();
            } else {
               if ((this.flags & 8) != 0) {
                  if ((p_178818_.get(this.redirect)).node == null) {
                     return false;
                  }

                  this.builder.redirect((p_178818_.get(this.redirect)).node);
               }

               if ((this.flags & 4) != 0) {
                  this.builder.executes((p_131906_) -> {
                     return 0;
                  });
               }

               this.node = this.builder.build();
            }
         }

         for(int i : this.children) {
            if ((p_178818_.get(i)).node == null) {
               return false;
            }
         }

         for(int j : this.children) {
            CommandNode<SharedSuggestionProvider> commandnode = (p_178818_.get(j)).node;
            if (!(commandnode instanceof RootCommandNode)) {
               this.node.addChild(commandnode);
            }
         }

         return true;
      }
   }
}