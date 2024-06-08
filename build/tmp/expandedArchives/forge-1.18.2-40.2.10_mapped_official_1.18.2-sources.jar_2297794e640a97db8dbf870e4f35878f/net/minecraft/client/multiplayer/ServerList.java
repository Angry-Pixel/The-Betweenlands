package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final List<ServerData> serverList = Lists.newArrayList();

   public ServerList(Minecraft p_105430_) {
      this.minecraft = p_105430_;
      this.load();
   }

   public void load() {
      try {
         this.serverList.clear();
         CompoundTag compoundtag = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
         if (compoundtag == null) {
            return;
         }

         ListTag listtag = compoundtag.getList("servers", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            this.serverList.add(ServerData.read(listtag.getCompound(i)));
         }
      } catch (Exception exception) {
         LOGGER.error("Couldn't load server list", (Throwable)exception);
      }

   }

   public void save() {
      try {
         ListTag listtag = new ListTag();

         for(ServerData serverdata : this.serverList) {
            listtag.add(serverdata.write());
         }

         CompoundTag compoundtag = new CompoundTag();
         compoundtag.put("servers", listtag);
         File file3 = File.createTempFile("servers", ".dat", this.minecraft.gameDirectory);
         NbtIo.write(compoundtag, file3);
         File file1 = new File(this.minecraft.gameDirectory, "servers.dat_old");
         File file2 = new File(this.minecraft.gameDirectory, "servers.dat");
         Util.safeReplaceFile(file2, file3, file1);
      } catch (Exception exception) {
         LOGGER.error("Couldn't save server list", (Throwable)exception);
      }

   }

   public ServerData get(int p_105433_) {
      return this.serverList.get(p_105433_);
   }

   public void remove(ServerData p_105441_) {
      this.serverList.remove(p_105441_);
   }

   public void add(ServerData p_105444_) {
      this.serverList.add(p_105444_);
   }

   public int size() {
      return this.serverList.size();
   }

   public void swap(int p_105435_, int p_105436_) {
      ServerData serverdata = this.get(p_105435_);
      this.serverList.set(p_105435_, this.get(p_105436_));
      this.serverList.set(p_105436_, serverdata);
      this.save();
   }

   public void replace(int p_105438_, ServerData p_105439_) {
      this.serverList.set(p_105438_, p_105439_);
   }

   public static void saveSingleServer(ServerData p_105447_) {
      ServerList serverlist = new ServerList(Minecraft.getInstance());
      serverlist.load();

      for(int i = 0; i < serverlist.size(); ++i) {
         ServerData serverdata = serverlist.get(i);
         if (serverdata.name.equals(p_105447_.name) && serverdata.ip.equals(p_105447_.ip)) {
            serverlist.replace(i, p_105447_);
            break;
         }
      }

      serverlist.save();
   }
}