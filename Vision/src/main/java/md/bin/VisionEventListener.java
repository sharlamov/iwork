package md.bin;

import java.awt.image.BufferedImage;

public interface VisionEventListener {

    void frameObtained(BufferedImage image);
}