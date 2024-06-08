package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerMenu> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

   public SmokerScreen(SmokerMenu p_99300_, Inventory p_99301_, Component p_99302_) {
      super(p_99300_, new SmokingRecipeBookComponent(), p_99301_, p_99302_, TEXTURE);
   }
}