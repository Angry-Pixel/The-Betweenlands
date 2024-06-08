package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   final Map<String, CriterionProgress> criteria;
   private String[][] requirements = new String[0][];

   private AdvancementProgress(Map<String, CriterionProgress> p_144358_) {
      this.criteria = p_144358_;
   }

   public AdvancementProgress() {
      this.criteria = Maps.newHashMap();
   }

   public void update(Map<String, Criterion> p_8199_, String[][] p_8200_) {
      Set<String> set = p_8199_.keySet();
      this.criteria.entrySet().removeIf((p_8203_) -> {
         return !set.contains(p_8203_.getKey());
      });

      for(String s : set) {
         if (!this.criteria.containsKey(s)) {
            this.criteria.put(s, new CriterionProgress());
         }
      }

      this.requirements = p_8200_;
   }

   public boolean isDone() {
      if (this.requirements.length == 0) {
         return false;
      } else {
         for(String[] astring : this.requirements) {
            boolean flag = false;

            for(String s : astring) {
               CriterionProgress criterionprogress = this.getCriterion(s);
               if (criterionprogress != null && criterionprogress.isDone()) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasProgress() {
      for(CriterionProgress criterionprogress : this.criteria.values()) {
         if (criterionprogress.isDone()) {
            return true;
         }
      }

      return false;
   }

   public boolean grantProgress(String p_8197_) {
      CriterionProgress criterionprogress = this.criteria.get(p_8197_);
      if (criterionprogress != null && !criterionprogress.isDone()) {
         criterionprogress.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String p_8210_) {
      CriterionProgress criterionprogress = this.criteria.get(p_8210_);
      if (criterionprogress != null && criterionprogress.isDone()) {
         criterionprogress.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf p_8205_) {
      p_8205_.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (p_144360_, p_144361_) -> {
         p_144361_.serializeToNetwork(p_144360_);
      });
   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf p_8212_) {
      Map<String, CriterionProgress> map = p_8212_.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
      return new AdvancementProgress(map);
   }

   @Nullable
   public CriterionProgress getCriterion(String p_8215_) {
      return this.criteria.get(p_8215_);
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float f = (float)this.requirements.length;
         float f1 = (float)this.countCompletedRequirements();
         return f1 / f;
      }
   }

   @Nullable
   public String getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int i = this.requirements.length;
         if (i <= 1) {
            return null;
         } else {
            int j = this.countCompletedRequirements();
            return j + "/" + i;
         }
      }
   }

   private int countCompletedRequirements() {
      int i = 0;

      for(String[] astring : this.requirements) {
         boolean flag = false;

         for(String s : astring) {
            CriterionProgress criterionprogress = this.getCriterion(s);
            if (criterionprogress != null && criterionprogress.isDone()) {
               flag = true;
               break;
            }
         }

         if (flag) {
            ++i;
         }
      }

      return i;
   }

   public Iterable<String> getRemainingCriteria() {
      List<String> list = Lists.newArrayList();

      for(Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
         if (!entry.getValue().isDone()) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public Iterable<String> getCompletedCriteria() {
      List<String> list = Lists.newArrayList();

      for(Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
         if (entry.getValue().isDone()) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   @Nullable
   public Date getFirstProgressDate() {
      Date date = null;

      for(CriterionProgress criterionprogress : this.criteria.values()) {
         if (criterionprogress.isDone() && (date == null || criterionprogress.getObtained().before(date))) {
            date = criterionprogress.getObtained();
         }
      }

      return date;
   }

   public int compareTo(AdvancementProgress p_8195_) {
      Date date = this.getFirstProgressDate();
      Date date1 = p_8195_.getFirstProgressDate();
      if (date == null && date1 != null) {
         return 1;
      } else if (date != null && date1 == null) {
         return -1;
      } else {
         return date == null && date1 == null ? 0 : date.compareTo(date1);
      }
   }

   public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
      public JsonElement serialize(AdvancementProgress p_8226_, Type p_8227_, JsonSerializationContext p_8228_) {
         JsonObject jsonobject = new JsonObject();
         JsonObject jsonobject1 = new JsonObject();

         for(Entry<String, CriterionProgress> entry : p_8226_.criteria.entrySet()) {
            CriterionProgress criterionprogress = entry.getValue();
            if (criterionprogress.isDone()) {
               jsonobject1.add(entry.getKey(), criterionprogress.serializeToJson());
            }
         }

         if (!jsonobject1.entrySet().isEmpty()) {
            jsonobject.add("criteria", jsonobject1);
         }

         jsonobject.addProperty("done", p_8226_.isDone());
         return jsonobject;
      }

      public AdvancementProgress deserialize(JsonElement p_8230_, Type p_8231_, JsonDeserializationContext p_8232_) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_8230_, "advancement");
         JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "criteria", new JsonObject());
         AdvancementProgress advancementprogress = new AdvancementProgress();

         for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
            String s = entry.getKey();
            advancementprogress.criteria.put(s, CriterionProgress.fromJson(GsonHelper.convertToString(entry.getValue(), s)));
         }

         return advancementprogress;
      }
   }
}