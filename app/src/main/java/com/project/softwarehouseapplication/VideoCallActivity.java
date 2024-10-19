package com.project.softwarehouseapplication;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;
public class VideoCallActivity extends AppCompatActivity {
    private RtcEngine mRtcEngine;
    private FrameLayout mLocalContainer;
    private SurfaceView mLocalView;
    private static final String APP_ID = "c855614dbc034e92bbd779636df03011";
    private static final String TOKEN = "007eJxTYDD0LnFPn+keuPnqgVlrFJZ1LK3nL9n27F0YS99q/uvhdjMVGJISzY0NU8yMzE1TLE0SLZMtzS0MUixSkk0NUo2TjMyNbjkJpzcEMjLIvtBhYIRCEF+CoTg/raQ8sSg1I7+0ODWxoCAnMzmxJDM/j4EBAPUXJwc=";
    private static final String CHANNEL_NAME = "softwarehouseapplication";
    // Agora event handler
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            runOnUiThread(() -> {
                // Channel joined successfully
            });
        }
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> {
                // Remote user joined
                setupRemoteVideo(uid);
            });
        }
        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> {
                // Remote user offline
                removeRemoteVideo();
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        mLocalContainer = findViewById(R.id.local_video_view_container);
        initializeAgoraEngine();
        setupLocalVideo();
        joinChannel();
    }
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), APP_ID, mRtcEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setupLocalVideo() {
        mLocalView = new SurfaceView(getBaseContext());
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }
    private void setupRemoteVideo(int uid) {
        SurfaceView remoteView = new SurfaceView(getBaseContext());  // Corrected line
        mLocalContainer.addView(remoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }
    private void removeRemoteVideo() {
        // Remove the remote video view when the user leaves the channel
        mLocalContainer.removeAllViews();
    }
    private void joinChannel() {
        mRtcEngine.joinChannel(TOKEN, CHANNEL_NAME, "", 0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtcEngine.destroy();
    }
}