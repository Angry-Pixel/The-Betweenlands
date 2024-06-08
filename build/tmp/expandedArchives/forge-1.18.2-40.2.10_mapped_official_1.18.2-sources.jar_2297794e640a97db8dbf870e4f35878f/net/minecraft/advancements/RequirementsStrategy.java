package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
   RequirementsStrategy AND = (p_15984_) -> {
      String[][] astring = new String[p_15984_.size()][];
      int i = 0;

      for(String s : p_15984_) {
         astring[i++] = new String[]{s};
      }

      return astring;
   };
   RequirementsStrategy OR = (p_15982_) -> {
      return new String[][]{p_15982_.toArray(new String[0])};
   };

   String[][] createRequirements(Collection<String> p_15986_);
}