package net.minecraft.world.phys.shapes;

public interface BooleanOp {
   BooleanOp FALSE = (p_82747_, p_82748_) -> {
      return false;
   };
   BooleanOp NOT_OR = (p_82744_, p_82745_) -> {
      return !p_82744_ && !p_82745_;
   };
   BooleanOp ONLY_SECOND = (p_82741_, p_82742_) -> {
      return p_82742_ && !p_82741_;
   };
   BooleanOp NOT_FIRST = (p_82738_, p_82739_) -> {
      return !p_82738_;
   };
   BooleanOp ONLY_FIRST = (p_82735_, p_82736_) -> {
      return p_82735_ && !p_82736_;
   };
   BooleanOp NOT_SECOND = (p_82732_, p_82733_) -> {
      return !p_82733_;
   };
   BooleanOp NOT_SAME = (p_82729_, p_82730_) -> {
      return p_82729_ != p_82730_;
   };
   BooleanOp NOT_AND = (p_82726_, p_82727_) -> {
      return !p_82726_ || !p_82727_;
   };
   BooleanOp AND = (p_82723_, p_82724_) -> {
      return p_82723_ && p_82724_;
   };
   BooleanOp SAME = (p_82720_, p_82721_) -> {
      return p_82720_ == p_82721_;
   };
   BooleanOp SECOND = (p_82717_, p_82718_) -> {
      return p_82718_;
   };
   BooleanOp CAUSES = (p_82714_, p_82715_) -> {
      return !p_82714_ || p_82715_;
   };
   BooleanOp FIRST = (p_82711_, p_82712_) -> {
      return p_82711_;
   };
   BooleanOp CAUSED_BY = (p_82708_, p_82709_) -> {
      return p_82708_ || !p_82709_;
   };
   BooleanOp OR = (p_82705_, p_82706_) -> {
      return p_82705_ || p_82706_;
   };
   BooleanOp TRUE = (p_82699_, p_82700_) -> {
      return true;
   };

   boolean apply(boolean p_82702_, boolean p_82703_);
}