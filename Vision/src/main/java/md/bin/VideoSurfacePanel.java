package md.bin;

import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class VideoSurfacePanel extends JPanel {

    private final BufferedImage image;
    private final DirectMediaPlayerComponent mediaPlayerComponent;
    private final int x;
    private final int y;
    private VideoSurfacePanel self;

    public VideoSurfacePanel(String source, final int x, final int y) {
        //System.setProperty("VLC_PLUGIN_PATH", "C:/Program Files/VideoLAN/VLC/plugins");
        //NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), new File("vlc/x64").getAbsolutePath());

        //LibC.INSTANCE._putenv(String.format("%s=%s", "VLC_PLUGIN_PATH", new File("vlc/plugins").getAbsolutePath()));
        new NativeDiscovery().discover();

        this.x = x;
        this.y = y;
        this.self = this;

        setBorder(new EtchedBorder(EtchedBorder.RAISED));
        setBackground(Color.black);
        setOpaque(true);
        Dimension d = new Dimension(x, y);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);

        image = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(x, y);

        BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(x, y);
            }
        };

        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new TutorialRenderCallbackAdapter();
            }
        };
        mediaPlayerComponent.getMediaPlayer().playMedia(source);
    }

    public static String relative( final File base, final File file ) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        final String relFileName = absFileName.substring(rootLength + 1);
        return relFileName;
    }

    public BufferedImage getSnapShot() {
        return mediaPlayerComponent.getMediaPlayer().isPlaying() ?
                mediaPlayerComponent.getMediaPlayer().getSnapshot() : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(image, null, 0, 0);
    }

    private class TutorialRenderCallbackAdapter extends RenderCallbackAdapter {

        private TutorialRenderCallbackAdapter() {
            super(new int[x * y]);
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
            // Simply copy buffer to the image and repaint
            image.setRGB(0, 0, x, y, rgbBuffer, 0, x);
            self.repaint();
        }
    }

}
