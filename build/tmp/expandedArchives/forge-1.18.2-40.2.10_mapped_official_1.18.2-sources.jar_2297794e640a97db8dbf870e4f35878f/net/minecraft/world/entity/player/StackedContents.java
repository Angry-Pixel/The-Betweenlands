package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class StackedContents {
   private static final int EMPTY = 0;
   public final Int2IntMap contents = new Int2IntOpenHashMap();

   public void accountSimpleStack(ItemStack p_36467_) {
      if (!p_36467_.isDamaged() && !p_36467_.isEnchanted() && !p_36467_.hasCustomHoverName()) {
         this.accountStack(p_36467_);
      }

   }

   public void accountStack(ItemStack p_36492_) {
      this.accountStack(p_36492_, 64);
   }

   public void accountStack(ItemStack p_36469_, int p_36470_) {
      if (!p_36469_.isEmpty()) {
         int i = getStackingIndex(p_36469_);
         int j = Math.min(p_36470_, p_36469_.getCount());
         this.put(i, j);
      }

   }

   public static int getStackingIndex(ItemStack p_36497_) {
      return Registry.ITEM.getId(p_36497_.getItem());
   }

   boolean has(int p_36483_) {
      return this.contents.get(p_36483_) > 0;
   }

   int take(int p_36457_, int p_36458_) {
      int i = this.contents.get(p_36457_);
      if (i >= p_36458_) {
         this.contents.put(p_36457_, i - p_36458_);
         return p_36457_;
      } else {
         return 0;
      }
   }

   void put(int p_36485_, int p_36486_) {
      this.contents.put(p_36485_, this.contents.get(p_36485_) + p_36486_);
   }

   public boolean canCraft(Recipe<?> p_36476_, @Nullable IntList p_36477_) {
      return this.canCraft(p_36476_, p_36477_, 1);
   }

   public boolean canCraft(Recipe<?> p_36479_, @Nullable IntList p_36480_, int p_36481_) {
      return (new StackedContents.RecipePicker(p_36479_)).tryPick(p_36481_, p_36480_);
   }

   public int getBiggestCraftableStack(Recipe<?> p_36494_, @Nullable IntList p_36495_) {
      return this.getBiggestCraftableStack(p_36494_, Integer.MAX_VALUE, p_36495_);
   }

   public int getBiggestCraftableStack(Recipe<?> p_36472_, int p_36473_, @Nullable IntList p_36474_) {
      return (new StackedContents.RecipePicker(p_36472_)).tryPickAll(p_36473_, p_36474_);
   }

   public static ItemStack fromStackingIndex(int p_36455_) {
      return p_36455_ == 0 ? ItemStack.EMPTY : new ItemStack(Item.byId(p_36455_));
   }

   public void clear() {
      this.contents.clear();
   }

   class RecipePicker {
      private final Recipe<?> recipe;
      private final List<Ingredient> ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] items;
      private final int itemCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(Recipe<?> p_36508_) {
         this.recipe = p_36508_;
         this.ingredients.addAll(p_36508_.getIngredients());
         this.ingredients.removeIf(Ingredient::isEmpty);
         this.ingredientCount = this.ingredients.size();
         this.items = this.getUniqueAvailableIngredientItems();
         this.itemCount = this.items.length;
         this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

         for(int i = 0; i < this.ingredients.size(); ++i) {
            IntList intlist = this.ingredients.get(i).getStackingIds();

            for(int j = 0; j < this.itemCount; ++j) {
               if (intlist.contains(this.items[j])) {
                  this.data.set(this.getIndex(true, j, i));
               }
            }
         }

      }

      public boolean tryPick(int p_36513_, @Nullable IntList p_36514_) {
         if (p_36513_ <= 0) {
            return true;
         } else {
            int i;
            for(i = 0; this.dfs(p_36513_); ++i) {
               StackedContents.this.take(this.items[this.path.getInt(0)], p_36513_);
               int j = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(j));

               for(int k = 0; k < j; ++k) {
                  this.toggleResidual((k & 1) == 0, this.path.get(k), this.path.get(k + 1));
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.itemCount);
            }

            boolean flag = i == this.ingredientCount;
            boolean flag1 = flag && p_36514_ != null;
            if (flag1) {
               p_36514_.clear();
            }

            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int l = 0;
            List<Ingredient> list = this.recipe.getIngredients();

            for(int i1 = 0; i1 < list.size(); ++i1) {
               if (flag1 && list.get(i1).isEmpty()) {
                  p_36514_.add(0);
               } else {
                  for(int j1 = 0; j1 < this.itemCount; ++j1) {
                     if (this.hasResidual(false, l, j1)) {
                        this.toggleResidual(true, j1, l);
                        StackedContents.this.put(this.items[j1], p_36513_);
                        if (flag1) {
                           p_36514_.add(this.items[j1]);
                        }
                     }
                  }

                  ++l;
               }
            }

            return flag;
         }
      }

      private int[] getUniqueAvailableIngredientItems() {
         IntCollection intcollection = new IntAVLTreeSet();

         for(Ingredient ingredient : this.ingredients) {
            intcollection.addAll(ingredient.getStackingIds());
         }

         IntIterator intiterator = intcollection.iterator();

         while(intiterator.hasNext()) {
            if (!StackedContents.this.has(intiterator.nextInt())) {
               intiterator.remove();
            }
         }

         return intcollection.toIntArray();
      }

      private boolean dfs(int p_36511_) {
         int i = this.itemCount;

         for(int j = 0; j < i; ++j) {
            if (StackedContents.this.contents.get(this.items[j]) >= p_36511_) {
               this.visit(false, j);

               while(!this.path.isEmpty()) {
                  int k = this.path.size();
                  boolean flag = (k & 1) == 1;
                  int l = this.path.getInt(k - 1);
                  if (!flag && !this.isSatisfied(l)) {
                     break;
                  }

                  int i1 = flag ? this.ingredientCount : i;

                  for(int j1 = 0; j1 < i1; ++j1) {
                     if (!this.hasVisited(flag, j1) && this.hasConnection(flag, l, j1) && this.hasResidual(flag, l, j1)) {
                        this.visit(flag, j1);
                        break;
                     }
                  }

                  int k1 = this.path.size();
                  if (k1 == k) {
                     this.path.removeInt(k1 - 1);
                  }
               }

               if (!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int p_36524_) {
         return this.data.get(this.getSatisfiedIndex(p_36524_));
      }

      private void setSatisfied(int p_36536_) {
         this.data.set(this.getSatisfiedIndex(p_36536_));
      }

      private int getSatisfiedIndex(int p_36545_) {
         return this.ingredientCount + this.itemCount + p_36545_;
      }

      private boolean hasConnection(boolean p_36519_, int p_36520_, int p_36521_) {
         return this.data.get(this.getIndex(p_36519_, p_36520_, p_36521_));
      }

      private boolean hasResidual(boolean p_36532_, int p_36533_, int p_36534_) {
         return p_36532_ != this.data.get(1 + this.getIndex(p_36532_, p_36533_, p_36534_));
      }

      private void toggleResidual(boolean p_36541_, int p_36542_, int p_36543_) {
         this.data.flip(1 + this.getIndex(p_36541_, p_36542_, p_36543_));
      }

      private int getIndex(boolean p_36547_, int p_36548_, int p_36549_) {
         int i = p_36547_ ? p_36548_ * this.ingredientCount + p_36549_ : p_36549_ * this.ingredientCount + p_36548_;
         return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * i;
      }

      private void visit(boolean p_36516_, int p_36517_) {
         this.data.set(this.getVisitedIndex(p_36516_, p_36517_));
         this.path.add(p_36517_);
      }

      private boolean hasVisited(boolean p_36529_, int p_36530_) {
         return this.data.get(this.getVisitedIndex(p_36529_, p_36530_));
      }

      private int getVisitedIndex(boolean p_36538_, int p_36539_) {
         return (p_36538_ ? 0 : this.ingredientCount) + p_36539_;
      }

      public int tryPickAll(int p_36526_, @Nullable IntList p_36527_) {
         int i = 0;
         int j = Math.min(p_36526_, this.getMinIngredientCount()) + 1;

         while(true) {
            int k = (i + j) / 2;
            if (this.tryPick(k, (IntList)null)) {
               if (j - i <= 1) {
                  if (k > 0) {
                     this.tryPick(k, p_36527_);
                  }

                  return k;
               }

               i = k;
            } else {
               j = k;
            }
         }
      }

      private int getMinIngredientCount() {
         int i = Integer.MAX_VALUE;

         for(Ingredient ingredient : this.ingredients) {
            int j = 0;

            for(int k : ingredient.getStackingIds()) {
               j = Math.max(j, StackedContents.this.contents.get(k));
            }

            if (i > 0) {
               i = Math.min(i, j);
            }
         }

         return i;
      }
   }
}