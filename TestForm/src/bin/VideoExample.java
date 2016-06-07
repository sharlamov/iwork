package bin;// Fig. 21.7: MediaTest.java

import java.awt.Dimension;
import java.io.IOException;
import javax.media.Format;

import javax.media.PlugInManager;
import javax.media.format.VideoFormat;
import javax.swing.*;
import javax.media.bean.playerbean.MediaPlayer;

public class VideoExample extends JFrame{
   MediaPlayer player;//наш плеер

   public VideoExample(String path){
      super("Простой видео плеер");

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      setSize(new Dimension(640, 480));//устанавливаем размер окна

      Format[] inFormats = { new VideoFormat("MPEG") };

      PlugInManager.addPlugIn("net.sourceforge.jffmpeg.VideoDecoder", inFormats, null, PlugInManager.CODEC);

      try {
         PlugInManager.commit();
      } catch (IOException e) {
         e.printStackTrace();
      }

      player = new MediaPlayer();
//path - путь к файлу
      player.setMediaLocation("file:///" + path);
      player.setPlaybackLoop(false);//Повтор видео
      player.prefetch ();//предварительная обработка плеера (без неё плеер не появится)
//добавляем на фрейм
      add(player);
//player.start (); - сразу запустить плеер

      setVisible(true);
   }

   public static void main(String []args){
      new VideoExample("C:/Users/sharlamov/Desktop/1/swipe.avi");
   }
}