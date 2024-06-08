package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SavedDataFeaturePoolElementFix extends DataFix {
   private static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+)\\]");
   private static final Set<String> PIECE_TYPE = Sets.newHashSet("minecraft:jigsaw", "minecraft:nvi", "minecraft:pcp", "minecraft:bastionremnant", "minecraft:runtime");
   private static final Set<String> FEATURES = Sets.newHashSet("minecraft:tree", "minecraft:flower", "minecraft:block_pile", "minecraft:random_patch");

   public SavedDataFeaturePoolElementFix(Schema p_145646_) {
      super(p_145646_, false);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataFeaturePoolElementFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), SavedDataFeaturePoolElementFix::fixTag);
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> p_145663_) {
      return p_145663_.update("Children", SavedDataFeaturePoolElementFix::updateChildren);
   }

   private static <T> Dynamic<T> updateChildren(Dynamic<T> p_145665_) {
      return p_145665_.asStreamOpt().map(SavedDataFeaturePoolElementFix::updateChildren).map(p_145665_::createList).result().orElse(p_145665_);
   }

   private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> p_145661_) {
      return p_145661_.map((p_145667_) -> {
         String s = p_145667_.get("id").asString("");
         if (!PIECE_TYPE.contains(s)) {
            return p_145667_;
         } else {
            OptionalDynamic<?> optionaldynamic = p_145667_.get("pool_element");
            return !optionaldynamic.get("element_type").asString("").equals("minecraft:feature_pool_element") ? p_145667_ : p_145667_.update("pool_element", (p_145669_) -> {
               return p_145669_.update("feature", SavedDataFeaturePoolElementFix::fixFeature);
            });
         }
      });
   }

   private static <T> OptionalDynamic<T> get(Dynamic<T> p_145650_, String... p_145651_) {
      if (p_145651_.length == 0) {
         throw new IllegalArgumentException("Missing path");
      } else {
         OptionalDynamic<T> optionaldynamic = p_145650_.get(p_145651_[0]);

         for(int i = 1; i < p_145651_.length; ++i) {
            String s = p_145651_[i];
            Matcher matcher = INDEX_PATTERN.matcher(s);
            if (matcher.matches()) {
               int j = Integer.parseInt(matcher.group(1));
               List<? extends Dynamic<T>> list = optionaldynamic.asList(Function.identity());
               if (j >= 0 && j < list.size()) {
                  optionaldynamic = new OptionalDynamic<>(p_145650_.getOps(), DataResult.success(list.get(j)));
               } else {
                  optionaldynamic = new OptionalDynamic<>(p_145650_.getOps(), DataResult.error("Missing id:" + j));
               }
            } else {
               optionaldynamic = optionaldynamic.get(s);
            }
         }

         return optionaldynamic;
      }
   }

   @VisibleForTesting
   protected static Dynamic<?> fixFeature(Dynamic<?> p_145648_) {
      Optional<String> optional = getReplacement(get(p_145648_, "type").asString(""), get(p_145648_, "name").asString(""), get(p_145648_, "config", "state_provider", "type").asString(""), get(p_145648_, "config", "state_provider", "state", "Name").asString(""), get(p_145648_, "config", "state_provider", "entries", "[0]", "data", "Name").asString(""), get(p_145648_, "config", "foliage_placer", "type").asString(""), get(p_145648_, "config", "leaves_provider", "state", "Name").asString(""));
      return optional.isPresent() ? p_145648_.createString(optional.get()) : p_145648_;
   }

   private static Optional<String> getReplacement(String p_145653_, String p_145654_, String p_145655_, String p_145656_, String p_145657_, String p_145658_, String p_145659_) {
      String s;
      if (!p_145653_.isEmpty()) {
         s = p_145653_;
      } else {
         if (p_145654_.isEmpty()) {
            return Optional.empty();
         }

         if ("minecraft:normal_tree".equals(p_145654_)) {
            s = "minecraft:tree";
         } else {
            s = p_145654_;
         }
      }

      if (FEATURES.contains(s)) {
         if ("minecraft:random_patch".equals(s)) {
            if ("minecraft:simple_state_provider".equals(p_145655_)) {
               if ("minecraft:sweet_berry_bush".equals(p_145656_)) {
                  return Optional.of("minecraft:patch_berry_bush");
               }

               if ("minecraft:cactus".equals(p_145656_)) {
                  return Optional.of("minecraft:patch_cactus");
               }
            } else if ("minecraft:weighted_state_provider".equals(p_145655_) && ("minecraft:grass".equals(p_145657_) || "minecraft:fern".equals(p_145657_))) {
               return Optional.of("minecraft:patch_taiga_grass");
            }
         } else if ("minecraft:block_pile".equals(s)) {
            if (!"minecraft:simple_state_provider".equals(p_145655_) && !"minecraft:rotated_block_provider".equals(p_145655_)) {
               if ("minecraft:weighted_state_provider".equals(p_145655_)) {
                  if ("minecraft:packed_ice".equals(p_145657_) || "minecraft:blue_ice".equals(p_145657_)) {
                     return Optional.of("minecraft:pile_ice");
                  }

                  if ("minecraft:jack_o_lantern".equals(p_145657_) || "minecraft:pumpkin".equals(p_145657_)) {
                     return Optional.of("minecraft:pile_pumpkin");
                  }
               }
            } else {
               if ("minecraft:hay_block".equals(p_145656_)) {
                  return Optional.of("minecraft:pile_hay");
               }

               if ("minecraft:melon".equals(p_145656_)) {
                  return Optional.of("minecraft:pile_melon");
               }

               if ("minecraft:snow".equals(p_145656_)) {
                  return Optional.of("minecraft:pile_snow");
               }
            }
         } else {
            if ("minecraft:flower".equals(s)) {
               return Optional.of("minecraft:flower_plain");
            }

            if ("minecraft:tree".equals(s)) {
               if ("minecraft:acacia_foliage_placer".equals(p_145658_)) {
                  return Optional.of("minecraft:acacia");
               }

               if ("minecraft:blob_foliage_placer".equals(p_145658_) && "minecraft:oak_leaves".equals(p_145659_)) {
                  return Optional.of("minecraft:oak");
               }

               if ("minecraft:pine_foliage_placer".equals(p_145658_)) {
                  return Optional.of("minecraft:pine");
               }

               if ("minecraft:spruce_foliage_placer".equals(p_145658_)) {
                  return Optional.of("minecraft:spruce");
               }
            }
         }
      }

      return Optional.empty();
   }
}