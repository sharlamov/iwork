package md.bin;

import java.net.URI;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Bin.ELEMENT_ADDED;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.lowlevel.GObjectAPI.GParamSpec;
import org.gstreamer.lowlevel.GstAPI;

import com.sun.jna.Pointer;

public class GstPlayBin2Test {
	public static void main(String [] args) throws Exception {
		Gst.init("GstPlayBin2Test", args);
		
		URI uri = new URI("rtsp://dev:dev@192.168.1.251");
		PlayBin2 playbin2 = new PlayBin2("GstPlayBin2Test", uri);
		
		Element videoSink = ElementFactory.make("autovideosink", "videosink");
		playbin2.setVideoSink(videoSink);
		
		playbin2.connect("notify::source", Object.class, null, new NotifySourceCallback());
		
		playbin2.play();
		
		Gst.main();
		
		playbin2.stop();
	}
	
	static class NotifySourceCallback implements GstAPI.GstCallback {
		public void callback(Element element, GParamSpec spec, Pointer user_data) {
			Element source = (Element) element.get("source");
			String sourceFactory = source.getFactory().getName();
			if ("rtspsrc".equals(sourceFactory)) {
				source.set("latency", 10000);
			}
		}
	}
}