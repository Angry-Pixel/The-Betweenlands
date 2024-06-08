package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public RegistryDumpReport(DataGenerator p_124053_) {
      this.generator = p_124053_;
   }

   public void run(HashCache p_124061_) throws IOException {
      JsonObject jsonobject = new JsonObject();
      Registry.REGISTRY.holders().forEach((p_211088_) -> {
         jsonobject.add(p_211088_.key().location().toString(), dumpRegistry(p_211088_.value()));
      });
      Path path = this.generator.getOutputFolder().resolve("reports/registries.json");
      DataProvider.save(GSON, p_124061_, jsonobject, path);
   }

   private static <T> JsonElement dumpRegistry(Registry<T> p_124059_) {
      JsonObject jsonobject = new JsonObject();
      if (p_124059_ instanceof DefaultedRegistry) {
         ResourceLocation resourcelocation = ((DefaultedRegistry)p_124059_).getDefaultKey();
         jsonobject.addProperty("default", resourcelocation.toString());
      }

      int i = ((Registry)Registry.REGISTRY).getId(p_124059_);
      jsonobject.addProperty("protocol_id", i);
      JsonObject jsonobject1 = new JsonObject();
      p_124059_.holders().forEach((p_211092_) -> {
         T t = p_211092_.value();
         int j = p_124059_.getId(t);
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("protocol_id", j);
         jsonobject1.add(p_211092_.key().location().toString(), jsonobject2);
      });
      jsonobject.add("entries", jsonobject1);
      return jsonobject;
   }

   public String getName() {
      return "Registry Dump";
   }
}