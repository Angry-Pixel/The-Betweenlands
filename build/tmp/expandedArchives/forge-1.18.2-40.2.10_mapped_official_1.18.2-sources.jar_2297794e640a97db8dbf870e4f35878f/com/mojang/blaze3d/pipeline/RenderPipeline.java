package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPipeline {
   private final List<ConcurrentLinkedQueue<RenderCall>> renderCalls = ImmutableList.of(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
   private volatile boolean isRecording;
   private volatile int recordingBuffer;
   private volatile boolean isProcessing;
   private volatile int processedBuffer;
   private volatile int renderingBuffer;

   public RenderPipeline() {
      this.recordingBuffer = this.processedBuffer = this.renderingBuffer + 1;
   }

   public boolean canBeginRecording() {
      return !this.isRecording && this.recordingBuffer == this.processedBuffer;
   }

   public boolean beginRecording() {
      if (this.isRecording) {
         throw new RuntimeException("ALREADY RECORDING !!!");
      } else if (this.canBeginRecording()) {
         this.recordingBuffer = (this.processedBuffer + 1) % this.renderCalls.size();
         this.isRecording = true;
         return true;
      } else {
         return false;
      }
   }

   public void recordRenderCall(RenderCall p_166184_) {
      if (!this.isRecording) {
         throw new RuntimeException("NOT RECORDING !!!");
      } else {
         ConcurrentLinkedQueue<RenderCall> concurrentlinkedqueue = this.getRecordingQueue();
         concurrentlinkedqueue.add(p_166184_);
      }
   }

   public void endRecording() {
      if (this.isRecording) {
         this.isRecording = false;
      } else {
         throw new RuntimeException("NOT RECORDING !!!");
      }
   }

   public boolean canBeginProcessing() {
      return !this.isProcessing && this.recordingBuffer != this.processedBuffer;
   }

   public boolean beginProcessing() {
      if (this.isProcessing) {
         throw new RuntimeException("ALREADY PROCESSING !!!");
      } else if (this.canBeginProcessing()) {
         this.isProcessing = true;
         return true;
      } else {
         return false;
      }
   }

   public void processRecordedQueue() {
      if (!this.isProcessing) {
         throw new RuntimeException("NOT PROCESSING !!!");
      }
   }

   public void endProcessing() {
      if (this.isProcessing) {
         this.isProcessing = false;
         this.renderingBuffer = this.processedBuffer;
         this.processedBuffer = this.recordingBuffer;
      } else {
         throw new RuntimeException("NOT PROCESSING !!!");
      }
   }

   public ConcurrentLinkedQueue<RenderCall> startRendering() {
      return this.renderCalls.get(this.renderingBuffer);
   }

   public ConcurrentLinkedQueue<RenderCall> getRecordingQueue() {
      return this.renderCalls.get(this.recordingBuffer);
   }

   public ConcurrentLinkedQueue<RenderCall> getProcessedQueue() {
      return this.renderCalls.get(this.processedBuffer);
   }
}