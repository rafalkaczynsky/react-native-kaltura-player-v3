//  Created by react-native-create-bridge

package com.xxx.supermodule;

import com.xxx.R;

import android.app.Activity;
import android.widget.Button;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup ;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;


import com.kaltura.dtg.ContentManager;
import com.kaltura.dtg.DownloadItem;
import com.kaltura.dtg.DownloadStateListener;
import com.kaltura.playkit.LocalAssetsManager;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;

import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class supermoduleManager extends ViewGroupManager<ViewGroup> {
    public static final String REACT_CLASS = "supermodule";
    private static final String TAG = "MyActivity";

    private ThemedReactContext mContext;
    private View view;
    private Activity mActivity;
    private AppCompatActivity mAppCompatActivity;

    private static final int START_POSITION = 0; // one minute.
    private static final long MIN_EXP_SEC = 10;
    /**
    const config = [
      '2358011',partnerId
      '41441941',configId
      '1_tyok377y',entryId
      'http://cdnapi.kaltura.com', url */

    // PROPS NEEDED FOR SIMPLE PLAYER 
    private static final String SOURCE_URL = "https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4";
    private static final String ENTRY_ID = "1_89fm8xyq"; //ENTRY_ID
    private static final String MEDIA_SOURCE_ID = "1821821"; // PARTNER_ID
    
    private static final String PLAYER_TYPE = "OFFLINE"; // CAN BE OFFLINE ALSO

    // PROPS  NEEDED FOR OFFLINE PLAYER
    private static final String ASSET_URL = "https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4";
    private static final String ASSET_ID = "1_89fm8xyq";
    private static final String ASSET_LICENSE_URL = null;

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private ViewGroup playerView;

    private ContentManager contentManager;
    private LocalAssetsManager localAssetsManager;
    private PKMediaEntry originMediaEntry = mediaEntry(ASSET_ID, ASSET_URL, ASSET_LICENSE_URL);
    private PKMediaSource originMediaSource = originMediaEntry.getSources().get(0);
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // CONSTRUCTOR
    public  supermoduleManager(){
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public String getName() {
        // Tell React the name of the module
        return REACT_CLASS;
    }

    @Override
    public ViewGroup createViewInstance(ThemedReactContext context){

        mContext = context;

        if (PLAYER_TYPE == "SIMPLE") {
            playerView = createSimplePlayerView(context);
        }

        if (PLAYER_TYPE == "OFFLINE") {
            playerView = createOfflinePlayerView(context);
        }

        return playerView;
    }

    // ==========================================================
    //         ------------ OFFLINE PLAYER ---------------
    // ==========================================================
/*

        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Create instance of the player.
        player = PlayKitManager.loadPlayer(mContext, null);                   
        //Add player view to the layout.
        ViewGroup flowContainer   = (ViewGroup) ViewGroup.inflate(mContext, R.layout.activity_main_simple, null);
        // Get player View
        View playerView = player.getView();
        //add player view to ViewGroup
        flowContainer.addView(playerView);
        // Add Simple Play/Pause Button to Layout
        playPauseButton = (Button) flowContainer.findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //If player is playing, change text of the button and pause.
                    playPauseButton.setText(R.string.play_text);
                    player.pause();
                } else {
                    //If player is not playing, change text of the button and play.
                    playPauseButton.setText(R.string.pause_text);
                    player.play();
                }
            }
        });  
        //Prepare player with media configuration.
        player.prepare(mediaConfig);

*/
    private ViewGroup createOfflinePlayerView(ThemedReactContext context){

        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        ViewGroup flowContainer   = (ViewGroup) ViewGroup.inflate(mContext, R.layout.activity_main_offline, null);
        // Get player View
        //Create instance of the player.

        if (player == null) {
            player = PlayKitManager.loadPlayer(mContext, null);

            View playerView = player.getView();
            //add player view to ViewGroup
            flowContainer.addView(playerView);
        }

        //toolbar = (Toolbar) flowContainer.findViewById(R.id.toolbar);
  
       // mAppCompatActivity.setSupportActionBar(toolbar);
        fab = (FloatingActionButton) flowContainer.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

                // Add Simple Play/Pause Button to Layout
        playPauseButton = (Button) flowContainer.findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //If player is playing, change text of the button and pause.
                    playPauseButton.setText(R.string.play_text);
                    player.pause();
                } else {
                    //If player is not playing, change text of the button and play.
                    playPauseButton.setText(R.string.pause_text);
                    player.play();
                }
            }
        });  
        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        startContentManager(context);
        startLocalAssetsManager(context);
        
        return flowContainer;
    }

    private PKMediaEntry mediaEntry(String id, String url, String licenseUrl) {

        PKMediaSource source = new PKMediaSource()
                .setId(id)
                .setMediaFormat(PKMediaFormat.valueOfUrl(url))
                .setUrl(url);

        if (licenseUrl != null) {
            source.setDrmData(Collections.singletonList(
                    new PKDrmParams(licenseUrl, PKDrmParams.Scheme.WidevineCENC)));
        }

        return new PKMediaEntry()
                .setId(id)
                .setSources(Collections.singletonList(source));
    }

    private void startLocalAssetsManager(ThemedReactContext context) {
        if (localAssetsManager == null) {
            localAssetsManager = new LocalAssetsManager(context);
        }
    }

    private void startContentManager(ThemedReactContext context) {
        if (contentManager != null) {
            return;
        }
        contentManager = ContentManager.getInstance(context);
        contentManager.getSettings().maxConcurrentDownloads = 4;

        contentManager.addDownloadStateListener(new DownloadStateListener() {
            @Override
            public void onDownloadComplete(DownloadItem item) {
                Log.d(TAG, "complete: " + item);
            }

            @Override
            public void onProgressChange(DownloadItem item, long downloadedBytes) {
                Log.d(TAG, "progress: " + downloadedBytes);
            }

            @Override
            public void onDownloadStart(DownloadItem item) {
                Log.d(TAG, "start: " + item);
            }

            @Override
            public void onDownloadPause(DownloadItem item) {
                Log.d(TAG, "pause: " + item);
            }

            @Override
            public void onDownloadFailure(DownloadItem item, Exception error) {
                Log.d(TAG, "failure: " + item);

            }

            @Override
            public void onDownloadMetadata(DownloadItem item, Exception error){
                Log.d(TAG, "meta: " + item);
                if (error == null) {
                    item.startDownload();
                } else {

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext, "Error: Load Metdata Failed - check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, "onDownloadMetadata failure: " + error.getMessage());
                    contentManager.removeItem(ASSET_ID);
                }
            }

            @Override
            public void onTracksAvailable(DownloadItem item, DownloadItem.TrackSelector trackSelector) {
                Log.d(TAG, "tracks: " + item);

                // Select video track
                List<DownloadItem.Track> videoTracks = trackSelector.getAvailableTracks(DownloadItem.TrackType.VIDEO);
                DownloadItem.Track selectedVideoTrack = null;
                
                // A few recipes, pick one or cook something else
                // SD-ish track
                selectedVideoTrack = getMinimumRequiredTrack(videoTracks, 600000, true);

                // HD-ish 
                selectedVideoTrack = getMinimumRequiredTrack(videoTracks, 1300000, true);
                
                // Select the highest-bitrate video
                selectedVideoTrack = Collections.max(videoTracks, DownloadItem.Track.bitrateComparator);
                
                // Or settle for the lowest
                selectedVideoTrack = Collections.min(videoTracks, DownloadItem.Track.bitrateComparator);

                trackSelector.setSelectedTracks(DownloadItem.TrackType.VIDEO, Collections.singletonList(selectedVideoTrack));


                // Select ALL audio tracks
                trackSelector.setSelectedTracks(DownloadItem.TrackType.AUDIO, trackSelector.getAvailableTracks(DownloadItem.TrackType.AUDIO));

                // Select ALL text tracks
                trackSelector.setSelectedTracks(DownloadItem.TrackType.TEXT, trackSelector.getAvailableTracks(DownloadItem.TrackType.TEXT));
            }
        });
        contentManager.start(new ContentManager.OnStartedListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Download Service started");
            }
        });
    }

    // Find the minimal "good enough" track. In other words, the track that has bitrate greater than or equal
    // to the requested minimum.
    private DownloadItem.Track getMinimumRequiredTrack(List<DownloadItem.Track> videoTracks, int minRequired, boolean settleForLess) {
        
        if (videoTracks == null || videoTracks.isEmpty()) {
            return null;
        }
        
        Collections.sort(videoTracks, DownloadItem.Track.bitrateComparator);
        for (DownloadItem.Track videoTrack : videoTracks) {
            if (videoTrack.getBitrate() >= minRequired) {
                return videoTrack;
            }
        }
        
        if (settleForLess) {
            return videoTracks.get(videoTracks.size()-1);
        }
        return null;
    }


    void showMenu() {
        new AlertDialog.Builder(mContext)
                .setItems(new String[]{"Download", "Register", "Play Local", "Unregister", "Remove", "Refresh"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // download
                                download();
                                break;
                            case 1: // register
                                registerDownloadedAsset();
                                break;
                            case 2: // play
                                playLocalAsset();
                                break;
                            case 3: // unregister
                                unregisterDownloadedAsset();
                                break;
                            case 4: // remove
                                removeDownload();
                                break;
                            case 5: // refresh
                                refreshLicense();
                                break;
                        }
                        Toast.makeText(mContext, "Selected " + which, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void unregisterDownloadedAsset() {
        String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        localAssetsManager.unregisterAsset(path, ASSET_ID, new LocalAssetsManager.AssetRemovalListener() {
            @Override
            public void onRemoved(String localAssetPath) {
                Toast.makeText(mContext, "unregistered", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void playLocalAsset() {
        final String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        if (path == null) {
            Toast.makeText(mContext, "Error path is null", Toast.LENGTH_LONG).show();
            return;
        }

        localAssetsManager.checkAssetStatus(path, ASSET_ID, new LocalAssetsManager.AssetStatusListener() {
            @Override
            public void onStatus(String localAssetPath, long expiryTimeSeconds, long availableTimeSeconds, boolean isRegistered) {
                //  check if DRM content valid                           or clear content
                if ((expiryTimeSeconds >= MIN_EXP_SEC && isRegistered) || (expiryTimeSeconds == Long.MAX_VALUE && availableTimeSeconds == Long.MAX_VALUE)) {
                    playOfflineVideo(path);
                } else {
                    Toast.makeText(mContext, "Error License Expired or not registerd please refresh it while in online mode", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void playOfflineVideo(String path) {
        PKMediaSource mediaSource = localAssetsManager.getLocalMediaSource(ASSET_ID, path);

        player.prepare(new PKMediaConfig().setMediaEntry(new PKMediaEntry().setSources(Collections.singletonList(mediaSource))));
        player.play();
    }

    private void registerDownloadedAsset() {
        File localFile = contentManager.getLocalFile(ASSET_ID);
        if (localFile == null) {
            Toast.makeText(mContext, "failed localFile is null", Toast.LENGTH_LONG).show();
            return;
        }
        localAssetsManager.registerAsset(originMediaSource, localFile.getAbsolutePath(), ASSET_ID, new LocalAssetsManager.AssetRegistrationListener() {
            
            @Override
            public void onRegistered(String localAssetPath) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "registered", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(String localAssetPath, Exception error) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void removeDownload() {
        contentManager.removeItem(ASSET_ID);
    }

    private void refreshLicense() {
        final String path = contentManager.getLocalFile(ASSET_ID).getAbsolutePath();
        if (path == null) {
            Toast.makeText(mContext, "Error path is null", Toast.LENGTH_LONG).show();
            return;
        }
        localAssetsManager.refreshAsset(originMediaSource, path, ASSET_ID, new LocalAssetsManager.AssetRegistrationListener() {
            @Override
            public void onRegistered(String localAssetPath) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "refreshed", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(String localAssetPath, Exception error) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void download() {
        
        DownloadItem item = contentManager.findItem(ASSET_ID);
        if (item == null) {
            item = contentManager.createItem(ASSET_ID, ASSET_URL);
            item.loadMetadata();
        } else {
            item.startDownload();
        }
    }

    // ==========================================================
    //         ------------ SIMPLE PLAYER ----------------
    // ==========================================================

    // SIMPLE PLAYER CREATOR 
    private  ViewGroup createSimplePlayerView(ThemedReactContext context){
        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Create instance of the player.
        player = PlayKitManager.loadPlayer(mContext, null);                   
        //Add player view to the layout.
        ViewGroup flowContainer   = (ViewGroup) ViewGroup.inflate(mContext, R.layout.activity_main_simple, null);
        // Get player View
        View playerView = player.getView();
        //add player view to ViewGroup
        flowContainer.addView(playerView);
        // Add Simple Play/Pause Button to Layout
        playPauseButton = (Button) flowContainer.findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //If player is playing, change text of the button and pause.
                    playPauseButton.setText(R.string.play_text);
                    player.pause();
                } else {
                    //If player is not playing, change text of the button and play.
                    playPauseButton.setText(R.string.pause_text);
                    player.play();
                }
            }
        });  
        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        return flowContainer;
    }

    private PKMediaEntry createMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

        private List<PKMediaSource> createMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
       // mediaSource.setId(MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(SOURCE_URL);

        //Set the format of the source. In our case it will be hls.
        mediaSource.setMediaFormat(PKMediaFormat.mp4);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }


    /**
     * Just add a simple button which will start/pause playback.
     */


    @ReactProp(name = "exampleProp")
    public void setExampleProp(View view, String prop) {
        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation
    }
}
