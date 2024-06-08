package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity extends BlockEntity {
   public static final int LINES = 4;
   private static final String[] RAW_TEXT_FIELD_NAMES = new String[]{"Text1", "Text2", "Text3", "Text4"};
   private static final String[] FILTERED_TEXT_FIELD_NAMES = new String[]{"FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4"};
   private final Component[] messages = new Component[]{TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY};
   private final Component[] filteredMessages = new Component[]{TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY};
   private boolean isEditable = true;
   @Nullable
   private UUID playerWhoMayEdit;
   @Nullable
   private FormattedCharSequence[] renderMessages;
   private boolean renderMessagedFiltered;
   private DyeColor color = DyeColor.BLACK;
   private boolean hasGlowingText;

   public SignBlockEntity(BlockPos p_155700_, BlockState p_155701_) {
      super(BlockEntityType.SIGN, p_155700_, p_155701_);
   }

   protected void saveAdditional(CompoundTag p_187515_) {
      super.saveAdditional(p_187515_);

      for(int i = 0; i < 4; ++i) {
         Component component = this.messages[i];
         String s = Component.Serializer.toJson(component);
         p_187515_.putString(RAW_TEXT_FIELD_NAMES[i], s);
         Component component1 = this.filteredMessages[i];
         if (!component1.equals(component)) {
            p_187515_.putString(FILTERED_TEXT_FIELD_NAMES[i], Component.Serializer.toJson(component1));
         }
      }

      p_187515_.putString("Color", this.color.getName());
      p_187515_.putBoolean("GlowingText", this.hasGlowingText);
   }

   public void load(CompoundTag p_155716_) {
      this.isEditable = false;
      super.load(p_155716_);
      this.color = DyeColor.byName(p_155716_.getString("Color"), DyeColor.BLACK);

      for(int i = 0; i < 4; ++i) {
         String s = p_155716_.getString(RAW_TEXT_FIELD_NAMES[i]);
         Component component = this.loadLine(s);
         this.messages[i] = component;
         String s1 = FILTERED_TEXT_FIELD_NAMES[i];
         if (p_155716_.contains(s1, 8)) {
            this.filteredMessages[i] = this.loadLine(p_155716_.getString(s1));
         } else {
            this.filteredMessages[i] = component;
         }
      }

      this.renderMessages = null;
      this.hasGlowingText = p_155716_.getBoolean("GlowingText");
   }

   private Component loadLine(String p_155712_) {
      Component component = this.deserializeTextSafe(p_155712_);
      if (this.level instanceof ServerLevel) {
         try {
            return ComponentUtils.updateForEntity(this.createCommandSourceStack((ServerPlayer)null), component, (Entity)null, 0);
         } catch (CommandSyntaxException commandsyntaxexception) {
         }
      }

      return component;
   }

   private Component deserializeTextSafe(String p_155721_) {
      try {
         Component component = Component.Serializer.fromJson(p_155721_);
         if (component != null) {
            return component;
         }
      } catch (Exception exception) {
      }

      return TextComponent.EMPTY;
   }

   public Component getMessage(int p_155707_, boolean p_155708_) {
      return this.getMessages(p_155708_)[p_155707_];
   }

   public void setMessage(int p_59733_, Component p_59734_) {
      this.setMessage(p_59733_, p_59734_, p_59734_);
   }

   public void setMessage(int p_155703_, Component p_155704_, Component p_155705_) {
      this.messages[p_155703_] = p_155704_;
      this.filteredMessages[p_155703_] = p_155705_;
      this.renderMessages = null;
   }

   public FormattedCharSequence[] getRenderMessages(boolean p_155718_, Function<Component, FormattedCharSequence> p_155719_) {
      if (this.renderMessages == null || this.renderMessagedFiltered != p_155718_) {
         this.renderMessagedFiltered = p_155718_;
         this.renderMessages = new FormattedCharSequence[4];

         for(int i = 0; i < 4; ++i) {
            this.renderMessages[i] = p_155719_.apply(this.getMessage(i, p_155718_));
         }
      }

      return this.renderMessages;
   }

   private Component[] getMessages(boolean p_155725_) {
      return p_155725_ ? this.filteredMessages : this.messages;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean p_59747_) {
      this.isEditable = p_59747_;
      if (!p_59747_) {
         this.playerWhoMayEdit = null;
      }

   }

   public void setAllowedPlayerEditor(UUID p_155714_) {
      this.playerWhoMayEdit = p_155714_;
   }

   @Nullable
   public UUID getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   public boolean executeClickCommands(ServerPlayer p_155710_) {
      for(Component component : this.getMessages(p_155710_.isTextFilteringEnabled())) {
         Style style = component.getStyle();
         ClickEvent clickevent = style.getClickEvent();
         if (clickevent != null && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            p_155710_.getServer().getCommands().performCommand(this.createCommandSourceStack(p_155710_), clickevent.getValue());
         }
      }

      return true;
   }

   public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer p_59736_) {
      String s = p_59736_ == null ? "Sign" : p_59736_.getName().getString();
      Component component = (Component)(p_59736_ == null ? new TextComponent("Sign") : p_59736_.getDisplayName());
      return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel)this.level, 2, s, component, this.level.getServer(), p_59736_);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public boolean setColor(DyeColor p_59740_) {
      if (p_59740_ != this.getColor()) {
         this.color = p_59740_;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean hasGlowingText() {
      return this.hasGlowingText;
   }

   public boolean setHasGlowingText(boolean p_155723_) {
      if (this.hasGlowingText != p_155723_) {
         this.hasGlowingText = p_155723_;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   private void markUpdated() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }
}