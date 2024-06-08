package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
   TextFilter DUMMY = new TextFilter() {
      public void join() {
      }

      public void leave() {
      }

      public CompletableFuture<TextFilter.FilteredText> processStreamMessage(String p_143708_) {
         return CompletableFuture.completedFuture(TextFilter.FilteredText.passThrough(p_143708_));
      }

      public CompletableFuture<List<TextFilter.FilteredText>> processMessageBundle(List<String> p_143710_) {
         return CompletableFuture.completedFuture(p_143710_.stream().map(TextFilter.FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
      }
   };

   void join();

   void leave();

   CompletableFuture<TextFilter.FilteredText> processStreamMessage(String p_10096_);

   CompletableFuture<List<TextFilter.FilteredText>> processMessageBundle(List<String> p_10097_);

   public static class FilteredText {
      public static final TextFilter.FilteredText EMPTY = new TextFilter.FilteredText("", "");
      private final String raw;
      private final String filtered;

      public FilteredText(String p_143717_, String p_143718_) {
         this.raw = p_143717_;
         this.filtered = p_143718_;
      }

      public String getRaw() {
         return this.raw;
      }

      public String getFiltered() {
         return this.filtered;
      }

      public static TextFilter.FilteredText passThrough(String p_143721_) {
         return new TextFilter.FilteredText(p_143721_, p_143721_);
      }

      public static TextFilter.FilteredText fullyFiltered(String p_143724_) {
         return new TextFilter.FilteredText(p_143724_, "");
      }
   }
}