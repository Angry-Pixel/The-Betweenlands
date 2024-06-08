package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;

public class SystemReport {
   public static final long BYTES_PER_MEBIBYTE = 1048576L;
   private static final long ONE_GIGA = 1000000000L;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String OPERATING_SYSTEM = System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
   private static final String JAVA_VERSION = System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
   private static final String JAVA_VM_VERSION = System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
   private final Map<String, String> entries = Maps.newLinkedHashMap();

   public SystemReport() {
      this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().getName());
      this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().getId());
      this.setDetail("Operating System", OPERATING_SYSTEM);
      this.setDetail("Java Version", JAVA_VERSION);
      this.setDetail("Java VM Version", JAVA_VM_VERSION);
      this.setDetail("Memory", () -> {
         Runtime runtime = Runtime.getRuntime();
         long i = runtime.maxMemory();
         long j = runtime.totalMemory();
         long k = runtime.freeMemory();
         long l = i / 1048576L;
         long i1 = j / 1048576L;
         long j1 = k / 1048576L;
         return k + " bytes (" + j1 + " MiB) / " + j + " bytes (" + i1 + " MiB) up to " + i + " bytes (" + l + " MiB)";
      });
      this.setDetail("CPUs", () -> {
         return String.valueOf(Runtime.getRuntime().availableProcessors());
      });
      this.ignoreErrors("hardware", () -> {
         this.putHardware(new SystemInfo());
      });
      this.setDetail("JVM Flags", () -> {
         List<String> list = Util.getVmArguments().collect(Collectors.toList());
         return String.format("%d total; %s", list.size(), String.join(" ", list));
      });
   }

   public void setDetail(String p_143520_, String p_143521_) {
      this.entries.put(p_143520_, p_143521_);
   }

   public void setDetail(String p_143523_, Supplier<String> p_143524_) {
      try {
         this.setDetail(p_143523_, p_143524_.get());
      } catch (Exception exception) {
         LOGGER.warn("Failed to get system info for {}", p_143523_, exception);
         this.setDetail(p_143523_, "ERR");
      }

   }

   private void putHardware(SystemInfo p_143536_) {
      HardwareAbstractionLayer hardwareabstractionlayer = p_143536_.getHardware();
      this.ignoreErrors("processor", () -> {
         this.putProcessor(hardwareabstractionlayer.getProcessor());
      });
      this.ignoreErrors("graphics", () -> {
         this.putGraphics(hardwareabstractionlayer.getGraphicsCards());
      });
      this.ignoreErrors("memory", () -> {
         this.putMemory(hardwareabstractionlayer.getMemory());
      });
   }

   private void ignoreErrors(String p_143517_, Runnable p_143518_) {
      try {
         p_143518_.run();
      } catch (Throwable throwable) {
         LOGGER.warn("Failed retrieving info for group {}", p_143517_, throwable);
      }

   }

   private void putPhysicalMemory(List<PhysicalMemory> p_143532_) {
      int i = 0;

      for(PhysicalMemory physicalmemory : p_143532_) {
         String s = String.format("Memory slot #%d ", i++);
         this.setDetail(s + "capacity (MB)", () -> {
            return String.format("%.2f", (float)physicalmemory.getCapacity() / 1048576.0F);
         });
         this.setDetail(s + "clockSpeed (GHz)", () -> {
            return String.format("%.2f", (float)physicalmemory.getClockSpeed() / 1.0E9F);
         });
         this.setDetail(s + "type", physicalmemory::getMemoryType);
      }

   }

   private void putVirtualMemory(VirtualMemory p_143550_) {
      this.setDetail("Virtual memory max (MB)", () -> {
         return String.format("%.2f", (float)p_143550_.getVirtualMax() / 1048576.0F);
      });
      this.setDetail("Virtual memory used (MB)", () -> {
         return String.format("%.2f", (float)p_143550_.getVirtualInUse() / 1048576.0F);
      });
      this.setDetail("Swap memory total (MB)", () -> {
         return String.format("%.2f", (float)p_143550_.getSwapTotal() / 1048576.0F);
      });
      this.setDetail("Swap memory used (MB)", () -> {
         return String.format("%.2f", (float)p_143550_.getSwapUsed() / 1048576.0F);
      });
   }

   private void putMemory(GlobalMemory p_143542_) {
      this.ignoreErrors("physical memory", () -> {
         this.putPhysicalMemory(p_143542_.getPhysicalMemory());
      });
      this.ignoreErrors("virtual memory", () -> {
         this.putVirtualMemory(p_143542_.getVirtualMemory());
      });
   }

   private void putGraphics(List<GraphicsCard> p_143553_) {
      int i = 0;

      for(GraphicsCard graphicscard : p_143553_) {
         String s = String.format("Graphics card #%d ", i++);
         this.setDetail(s + "name", graphicscard::getName);
         this.setDetail(s + "vendor", graphicscard::getVendor);
         this.setDetail(s + "VRAM (MB)", () -> {
            return String.format("%.2f", (float)graphicscard.getVRam() / 1048576.0F);
         });
         this.setDetail(s + "deviceId", graphicscard::getDeviceId);
         this.setDetail(s + "versionInfo", graphicscard::getVersionInfo);
      }

   }

   private void putProcessor(CentralProcessor p_143540_) {
      ProcessorIdentifier processoridentifier = p_143540_.getProcessorIdentifier();
      this.setDetail("Processor Vendor", processoridentifier::getVendor);
      this.setDetail("Processor Name", processoridentifier::getName);
      this.setDetail("Identifier", processoridentifier::getIdentifier);
      this.setDetail("Microarchitecture", processoridentifier::getMicroarchitecture);
      this.setDetail("Frequency (GHz)", () -> {
         return String.format("%.2f", (float)processoridentifier.getVendorFreq() / 1.0E9F);
      });
      this.setDetail("Number of physical packages", () -> {
         return String.valueOf(p_143540_.getPhysicalPackageCount());
      });
      this.setDetail("Number of physical CPUs", () -> {
         return String.valueOf(p_143540_.getPhysicalProcessorCount());
      });
      this.setDetail("Number of logical CPUs", () -> {
         return String.valueOf(p_143540_.getLogicalProcessorCount());
      });
   }

   public void appendToCrashReportString(StringBuilder p_143526_) {
      p_143526_.append("-- ").append("System Details").append(" --\n");
      p_143526_.append("Details:");
      this.entries.forEach((p_143529_, p_143530_) -> {
         p_143526_.append("\n\t");
         p_143526_.append(p_143529_);
         p_143526_.append(": ");
         p_143526_.append(p_143530_);
      });
   }

   public String toLineSeparatedString() {
      return this.entries.entrySet().stream().map((p_143534_) -> {
         return (String)p_143534_.getKey() + ": " + (String)p_143534_.getValue();
      }).collect(Collectors.joining(System.lineSeparator()));
   }
}