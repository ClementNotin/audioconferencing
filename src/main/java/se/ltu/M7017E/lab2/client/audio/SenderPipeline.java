package se.ltu.M7017E.lab2.client.audio;

import java.util.HashMap;
import java.util.Map;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.BaseSrc;

import se.ltu.M7017E.lab2.client.Config;
import se.ltu.M7017E.lab2.client.Tool;

public class SenderPipeline extends Pipeline {
	private Map<Integer, RoomSender> rooms = new HashMap<Integer, RoomSender>();
	private final BaseSrc src = (BaseSrc) ElementFactory.make("alsasrc", null);
	private final Element tee = ElementFactory.make("tee", null);

	public SenderPipeline() {
		super("sender");

		// live source => drop stream when in paused state
		src.setLive(true);

		addMany(src, tee);
		Tool.successOrDie("src-tee", linkMany(src, tee));
	}

	public void joinRoom(int roomId) {
		// don't join if already joined
		if (!rooms.containsKey(roomId)) {
			if (isPlaying()) {
				pause();
			}

			// create the sender bin
			RoomSender room = new RoomSender("room" + roomId, Config.BASE_IP
					+ roomId, Config.RTP_MULTICAST_PORT);
			rooms.put(roomId, room);
			// add it to this
			add(room);

			// connect its input to the tee
			Tool.successOrDie("tee-roomSender", tee.getRequestPad("src%d")
					.link(room.getStaticPad("sink")).equals(PadLinkReturn.OK));

			play();
		}
	}

	public void leaveRoom(int roomId) {
		// can't leave if not joined
		if (rooms.containsKey(roomId)) {

		}
	}
}
