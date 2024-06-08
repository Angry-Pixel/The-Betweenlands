package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvOutput {
   private static final String LINE_SEPARATOR = "\r\n";
   private static final String FIELD_SEPARATOR = ",";
   private final Writer output;
   private final int columnCount;

   CsvOutput(Writer p_13613_, List<String> p_13614_) throws IOException {
      this.output = p_13613_;
      this.columnCount = p_13614_.size();
      this.writeLine(p_13614_.stream());
   }

   public static CsvOutput.Builder builder() {
      return new CsvOutput.Builder();
   }

   public void writeRow(Object... p_13625_) throws IOException {
      if (p_13625_.length != this.columnCount) {
         throw new IllegalArgumentException("Invalid number of columns, expected " + this.columnCount + ", but got " + p_13625_.length);
      } else {
         this.writeLine(Stream.of(p_13625_));
      }
   }

   private void writeLine(Stream<?> p_13623_) throws IOException {
      this.output.write((String)p_13623_.map(CsvOutput::getStringValue).collect(Collectors.joining(",")) + "\r\n");
   }

   private static String getStringValue(@Nullable Object p_13621_) {
      return StringEscapeUtils.escapeCsv(p_13621_ != null ? p_13621_.toString() : "[null]");
   }

   public static class Builder {
      private final List<String> headers = Lists.newArrayList();

      public CsvOutput.Builder addColumn(String p_13631_) {
         this.headers.add(p_13631_);
         return this;
      }

      public CsvOutput build(Writer p_13629_) throws IOException {
         return new CsvOutput(p_13629_, this.headers);
      }
   }
}