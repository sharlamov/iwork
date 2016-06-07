package bin;// Fig. 21.7: MediaTest.java

import java.awt.Dimension;
import java.io.IOException;
import javax.media.Format;

import javax.media.PlugInManager;
import javax.media.format.VideoFormat;
import javax.swing.*;
import javax.media.bean.playerbean.MediaPlayer;

public class VideoExample extends JFrame{
   MediaPlayer player;//��� �����

   public VideoExample(String path){
      super("������� ����� �����");

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      setSize(new Dimension(640, 480));//������������� ������ ����

      Format[] inFormats = { new VideoFormat("MPEG") };

      PlugInManager.addPlugIn("net.sourceforge.jffmpeg.VideoDecoder", inFormats, null, PlugInManager.CODEC);

      try {
         PlugInManager.commit();
      } catch (IOException e) {
         e.printStackTrace();
      }

      player = new MediaPlayer();
//path - ���� � �����
      player.setMediaLocation("file:///" + path);
      player.setPlaybackLoop(false);//������ �����
      player.prefetch ();//��������������� ��������� ������ (��� �� ����� �� ��������)
//��������� �� �����
      add(player);
//player.start (); - ����� ��������� �����

      setVisible(true);
   }

   public static void main(String []args){
      new VideoExample("C:/Users/sharlamov/Desktop/1/swipe.avi");
   }
}