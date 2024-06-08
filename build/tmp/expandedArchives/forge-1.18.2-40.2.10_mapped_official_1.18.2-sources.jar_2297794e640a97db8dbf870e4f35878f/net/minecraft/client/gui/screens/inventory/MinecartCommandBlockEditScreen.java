package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartCommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final BaseCommandBlock commandBlock;

   public MinecartCommandBlockEditScreen(BaseCommandBlock p_99216_) {
      this.commandBlock = p_99216_;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   int getPreviousY() {
      return 150;
   }

   protected void init() {
      super.init();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   protected void populateAndSendPacket(BaseCommandBlock p_99218_) {
      if (p_99218_ instanceof MinecartCommandBlock.MinecartCommandBase) {
         MinecartCommandBlock.MinecartCommandBase minecartcommandblock$minecartcommandbase = (MinecartCommandBlock.MinecartCommandBase)p_99218_;
         this.minecraft.getConnection().send(new ServerboundSetCommandMinecartPacket(minecartcommandblock$minecartcommandbase.getMinecart().getId(), this.commandEdit.getValue(), p_99218_.isTrackOutput()));
      }

   }
}