package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JigsawBlockEditScreen extends Screen {
   private static final int MAX_LEVELS = 7;
   private static final Component JOINT_LABEL = new TranslatableComponent("jigsaw_block.joint_label");
   private static final Component POOL_LABEL = new TranslatableComponent("jigsaw_block.pool");
   private static final Component NAME_LABEL = new TranslatableComponent("jigsaw_block.name");
   private static final Component TARGET_LABEL = new TranslatableComponent("jigsaw_block.target");
   private static final Component FINAL_STATE_LABEL = new TranslatableComponent("jigsaw_block.final_state");
   private final JigsawBlockEntity jigsawEntity;
   private EditBox nameEdit;
   private EditBox targetEdit;
   private EditBox poolEdit;
   private EditBox finalStateEdit;
   int levels;
   private boolean keepJigsaws = true;
   private CycleButton<JigsawBlockEntity.JointType> jointButton;
   private Button doneButton;
   private Button generateButton;
   private JigsawBlockEntity.JointType joint;

   public JigsawBlockEditScreen(JigsawBlockEntity p_98949_) {
      super(NarratorChatListener.NO_TITLE);
      this.jigsawEntity = p_98949_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.targetEdit.tick();
      this.poolEdit.tick();
      this.finalStateEdit.tick();
   }

   private void onDone() {
      this.sendToServer();
      this.minecraft.setScreen((Screen)null);
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void sendToServer() {
      this.minecraft.getConnection().send(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint));
   }

   private void sendGenerate() {
      this.minecraft.getConnection().send(new ServerboundJigsawGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws));
   }

   public void onClose() {
      this.onCancel();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.poolEdit = new EditBox(this.font, this.width / 2 - 152, 20, 300, 20, new TranslatableComponent("jigsaw_block.pool"));
      this.poolEdit.setMaxLength(128);
      this.poolEdit.setValue(this.jigsawEntity.getPool().toString());
      this.poolEdit.setResponder((p_98986_) -> {
         this.updateValidity();
      });
      this.addWidget(this.poolEdit);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 55, 300, 20, new TranslatableComponent("jigsaw_block.name"));
      this.nameEdit.setMaxLength(128);
      this.nameEdit.setValue(this.jigsawEntity.getName().toString());
      this.nameEdit.setResponder((p_98981_) -> {
         this.updateValidity();
      });
      this.addWidget(this.nameEdit);
      this.targetEdit = new EditBox(this.font, this.width / 2 - 152, 90, 300, 20, new TranslatableComponent("jigsaw_block.target"));
      this.targetEdit.setMaxLength(128);
      this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
      this.targetEdit.setResponder((p_98977_) -> {
         this.updateValidity();
      });
      this.addWidget(this.targetEdit);
      this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, new TranslatableComponent("jigsaw_block.final_state"));
      this.finalStateEdit.setMaxLength(256);
      this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
      this.addWidget(this.finalStateEdit);
      this.joint = this.jigsawEntity.getJoint();
      int i = this.font.width(JOINT_LABEL) + 10;
      this.jointButton = this.addRenderableWidget(CycleButton.builder(JigsawBlockEntity.JointType::getTranslatedName).withValues(JigsawBlockEntity.JointType.values()).withInitialValue(this.joint).displayOnlyValue().create(this.width / 2 - 152 + i, 150, 300 - i, 20, JOINT_LABEL, (p_169765_, p_169766_) -> {
         this.joint = p_169766_;
      }));
      boolean flag = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
      this.jointButton.active = flag;
      this.jointButton.visible = flag;
      this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, TextComponent.EMPTY, 0.0D) {
         {
            this.updateMessage();
         }

         protected void updateMessage() {
            this.setMessage(new TranslatableComponent("jigsaw_block.levels", JigsawBlockEditScreen.this.levels));
         }

         protected void applyValue() {
            JigsawBlockEditScreen.this.levels = Mth.floor(Mth.clampedLerp(0.0D, 7.0D, this.value));
         }
      });
      this.addRenderableWidget(CycleButton.onOffBuilder(this.keepJigsaws).create(this.width / 2 - 50, 180, 100, 20, new TranslatableComponent("jigsaw_block.keep_jigsaws"), (p_169768_, p_169769_) -> {
         this.keepJigsaws = p_169769_;
      }));
      this.generateButton = this.addRenderableWidget(new Button(this.width / 2 + 54, 180, 100, 20, new TranslatableComponent("jigsaw_block.generate"), (p_98979_) -> {
         this.onDone();
         this.sendGenerate();
      }));
      this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 4 - 150, 210, 150, 20, CommonComponents.GUI_DONE, (p_98973_) -> {
         this.onDone();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, 210, 150, 20, CommonComponents.GUI_CANCEL, (p_98964_) -> {
         this.onCancel();
      }));
      this.setInitialFocus(this.poolEdit);
      this.updateValidity();
   }

   private void updateValidity() {
      boolean flag = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
      this.doneButton.active = flag;
      this.generateButton.active = flag;
   }

   public void resize(Minecraft p_98960_, int p_98961_, int p_98962_) {
      String s = this.nameEdit.getValue();
      String s1 = this.targetEdit.getValue();
      String s2 = this.poolEdit.getValue();
      String s3 = this.finalStateEdit.getValue();
      int i = this.levels;
      JigsawBlockEntity.JointType jigsawblockentity$jointtype = this.joint;
      this.init(p_98960_, p_98961_, p_98962_);
      this.nameEdit.setValue(s);
      this.targetEdit.setValue(s1);
      this.poolEdit.setValue(s2);
      this.finalStateEdit.setValue(s3);
      this.levels = i;
      this.joint = jigsawblockentity$jointtype;
      this.jointButton.setValue(jigsawblockentity$jointtype);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_98951_, int p_98952_, int p_98953_) {
      if (super.keyPressed(p_98951_, p_98952_, p_98953_)) {
         return true;
      } else if (!this.doneButton.active || p_98951_ != 257 && p_98951_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(PoseStack p_98955_, int p_98956_, int p_98957_, float p_98958_) {
      this.renderBackground(p_98955_);
      drawString(p_98955_, this.font, POOL_LABEL, this.width / 2 - 153, 10, 10526880);
      this.poolEdit.render(p_98955_, p_98956_, p_98957_, p_98958_);
      drawString(p_98955_, this.font, NAME_LABEL, this.width / 2 - 153, 45, 10526880);
      this.nameEdit.render(p_98955_, p_98956_, p_98957_, p_98958_);
      drawString(p_98955_, this.font, TARGET_LABEL, this.width / 2 - 153, 80, 10526880);
      this.targetEdit.render(p_98955_, p_98956_, p_98957_, p_98958_);
      drawString(p_98955_, this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
      this.finalStateEdit.render(p_98955_, p_98956_, p_98957_, p_98958_);
      if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
         drawString(p_98955_, this.font, JOINT_LABEL, this.width / 2 - 153, 156, 16777215);
      }

      super.render(p_98955_, p_98956_, p_98957_, p_98958_);
   }
}