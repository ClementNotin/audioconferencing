#!/bin/sh

if [ $1 ]
then
	ROOM=$1
else
	echo "Gimme a room number"
	exit
fi;

if [ $2 ]
then
	FREQ=$1
else
	FREQ=440;
fi;



gst-launch -v --gst-debug=2 gstrtpbin name=rtpbin \
    audiotestsrc freq=$FREQ ! audioconvert ! \
    mulawenc ! rtppcmupay ! rtpbin.send_rtp_sink_0 \
          rtpbin.send_rtp_src_0 ! udpsink host=224.1.42.$ROOM port=5000
