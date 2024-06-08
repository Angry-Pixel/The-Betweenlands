package net.minecraft.client.searchtree;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SearchTree<T> {
   List<T> search(String p_119955_);
}