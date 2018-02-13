//  Created by react-native-create-bridge

package com.xxx.supermodule;

import com.xxx.R;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.net.NetworkInfo;
import android.os.StrictMode;

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
import java.util.concurrent.TimeUnit; 
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.TargetApi;
import android.os.Build;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import 	java.net.HttpURLConnection;

public class supermoduleManager extends ViewGroupManager<ViewGroup> {
    public static final String REACT_CLASS = "supermodule";
    private static final String TAG = "MyActivity";

    private ThemedReactContext mContext;
    private View view;
    private Activity mActivity;
    private AppCompatActivity mAppCompatActivity;

    private static final int START_POSITION = 0; // one minute.
    private static final long MIN_EXP_SEC = 10;

    // PROPS NEEDED FOR SIMPLE PLAYER 
    private static  String SOURCE_URL = "https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4"; 
    private static  String ENTRY_ID = "1_89fm8xyq"; // ENTRY_ID
    private static  String MEDIA_SOURCE_ID = "1821821"; // PARTNER_ID
    
    private static final String PLAYER_TYPE = "OFFLINE"; // CAN BE OFFLINE ALSO

    // PROPS  NEEDED FOR OFFLINE PLAYER
    private static String ASSET_URL = SOURCE_URL;
    private static String ASSET_ID = ENTRY_ID ; // ENTRY ID
    private static String ASSET_LICENSE_URL = null;
    private static boolean IS_DOWNLOADABLE;

    private static boolean IS_DOWNLOADED = false;

    //HANDLE PLAYER PROPS
    private double startTime = 0;
    private double finalTime = 0;
    private long progressDownload;
    private Handler myHandler = new Handler(Looper.getMainLooper());
    private Handler offlineHandler = new Handler(Looper.getMainLooper());
    private int forwardTime = 5000; 
    private int backwardTime = 5000;
    public static int oneTimeOnly = 0;

    private boolean isOnline;
 
    private Player player;
    private PKMediaConfig mediaConfig;
    private ImageButton playButton, pauseButton, rewindButton, forwardButton, offlineButton; 
    private ViewGroup playerView;
    private Button submitButton;
    private SeekBar seekbar;
    private TextView startTimeField, endTimeField;
    private ProgressBar downloadProgressBar;

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 

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

    private ViewGroup createOfflinePlayerView(ThemedReactContext context){

        isOnline = netIsAvailable();

        if(isOnline == true){
                    Toast.makeText(mContext, "You are Online", 
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "You are Offline", 
            Toast.LENGTH_SHORT).show();
        }

        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

       // ViewGroup flowContainer   = (ViewGroup) ViewGroup.inflate(mContext, R.layout.activity_main_offline, null);
        // Get player View

        // ------ PLACEHOLDERS -------

        // MAIN CONTAINER
        ViewGroup mainContent   = (ViewGroup) ViewGroup.inflate(mContext, R.layout.content_main, null);
        // PLAYER VIEW
        ViewGroup placeholder =  mainContent.findViewById(R.id.player_root);
        // START/END TIME
        startTimeField = (TextView) mainContent.findViewById(R.id.exo_position);
        endTimeField = (TextView) mainContent.findViewById(R.id.exo_duration);

        //SEEK BAR
        seekbar=(SeekBar) mainContent.findViewById(R.id.exo_progress);
        //PLAY/PAUSE
        playButton = (ImageButton) mainContent.findViewById(R.id.play_pause_button);
        pauseButton = (ImageButton) mainContent.findViewById(R.id.exo_pause);
        //REWIND/FORWARD
        rewindButton = (ImageButton) mainContent.findViewById(R.id.exo_rew);
        forwardButton = (ImageButton) mainContent.findViewById(R.id.exo_ffwd);
        //DOWNLOAD PROGRESSBAR
        downloadProgressBar = (ProgressBar) mainContent.findViewById(R.id.downloadProgressBar);

        //INITIAL ATTRIBUTES FOR VIEWS
        seekbar.setClickable(false);
        pauseButton.setEnabled(false);
        downloadProgressBar.setVisibility(4);

        endTimeField.setText(String.format("%02d:%02d", 
            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%02d:%02d", 
            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) startTime)))
        );

        // ADD PLAYERVIEW TO MAIN CONTAINER
        if (player == null) {
            player = PlayKitManager.loadPlayer(mContext, null);
            View playerView = player.getView();
            //add player view to ViewGroup
            mainContent.addView(playerView);
        }

        // perform seek bar change listener event used for getting the progress value
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                player.seekTo((int) progressChangedValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(mContext, "Video is at " + String.format("%d min %d sec", 
                    TimeUnit.MILLISECONDS.toMinutes((long) progressChangedValue),
                    TimeUnit.MILLISECONDS.toSeconds((long) progressChangedValue) - 
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                toMinutes((long) progressChangedValue))),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // CONTROL BUTTON LISTENERS
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause(v);
            }
        });  

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (IS_DOWNLOADED == true) {
                     playOffline(v);
                  }else{ 
                     play(v);  
                  }
            }
        }); 

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewind(v);
            }
        });  

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward(v);
            }
        }); 

        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        startContentManager(context);
        startLocalAssetsManager(context);

        // OFFLINE LOGIC MUST BE AFTER MANAGERS HAS STARTED

       offlineButton =  (ImageButton) mainContent.findViewById(R.id.offline_button);

       if (IS_DOWNLOADABLE == true){
            offlineButton.setImageResource(R.drawable.offline_icon_unable);
        }else {
            offlineButton.setEnabled(false);
            offlineButton.setImageResource(R.drawable.offline_icon_unable);
        }

       // OFFLINE BUTTON ON CLICK LISTENER
        offlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_DOWNLOADED == true) {
                    //OFFLINE BUTTON - GREEN
                    offlineButton.setImageResource(R.drawable.offline_icon_off);
                    unregisterDownloadedAsset();
     
                } else {
                     
                        try {
                            URL url = new URL("http://www.google.com");

                            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

                            if (urlc.getResponseCode() == 200) {
                               // Main.Log("getResponseCode == 200");
                               isOnline = true;
                               offlineButton.setImageResource(R.drawable.offline_icon_on);
                               downloadProgressBar.setVisibility(0);
                               download();
                                urlc.disconnect();
                            }else{
                                isOnline = false;
                                Toast.makeText(mContext, "Sorry, You are offline!", Toast.LENGTH_LONG).show();
                                urlc.disconnect();
                            }
                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                            Toast.makeText(mContext, "Sorry, You are offline!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                           e.printStackTrace();
                           Toast.makeText(mContext, "Sorry, You are offline!", Toast.LENGTH_LONG).show();
                        } 
                           
                  
                       

                }
            }
        });  
     
        return mainContent;
    }


    // =====================  PLAY VIDEO =========================
     @TargetApi(Build.VERSION_CODES.GINGERBREAD) public void play(View view){

       // ADD LOGIC IF PLAYER IS NULL TO RELOAD ALL NEEDED STEPS  
       // IF IS GOING FROM OFFLINE PLAYER WILL NOT PLAY VIDEO NOW
      Toast.makeText(mContext, "Play Online", 
      Toast.LENGTH_SHORT).show();
      player.play();
      finalTime = player.getDuration();
      startTime = player.getCurrentPosition();
      if(oneTimeOnly == 0){
         seekbar.setMax((int) finalTime);
         oneTimeOnly = 1;
      } 

      endTimeField.setText(String.format("%02d:%02d", 
         TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
         TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
         toMinutes((long) finalTime)))
      );
      startTimeField.setText(String.format("%02d:%02d", 
         TimeUnit.MILLISECONDS.toMinutes((long) startTime),
         TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
         toMinutes((long) startTime)))
      );
      seekbar.setProgress((int)startTime);
      myHandler.postDelayed(UpdateSongTime,100);
      pauseButton.setEnabled(true);
      playButton.setEnabled(false);
   }

 
     // =====================  PLAY OFFLINE VIDEO =========================
     @TargetApi(Build.VERSION_CODES.GINGERBREAD) public void playOffline(View view){

        Toast.makeText(mContext, "Play Offline", 
        Toast.LENGTH_SHORT).show();

        // ADD LOGIC IF PLAYER IS NULL TO RELOAD ALL NEEDED STEPS  IF NOT THEN NOT
       //  IF YOU CLICK PLAY NOW , ASSETS ARE ALWAYS REALOADED

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
                    //PLAY VIDEO LOGIC
                    PKMediaSource mediaSource = localAssetsManager.getLocalMediaSource(ASSET_ID, path);
                    player.prepare(new PKMediaConfig().setMediaEntry(new PKMediaEntry().setSources(Collections.singletonList(mediaSource))));
                    player.play();
                    finalTime = player.getDuration();
                    startTime = player.getCurrentPosition();
                    if(oneTimeOnly == 0){
                        seekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }   

                    startTimeField.setText(String.format("%02d:%02d", 
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                        toMinutes((long) startTime))));

                    seekbar.setProgress((int)startTime);
                    offlineHandler.postDelayed(UpdateSongTimeOffline,100);
                    pauseButton.setEnabled(true);
                    playButton.setEnabled(false);
                } else {
                    Toast.makeText(mContext, "Error License Expired or not registerd please refresh it while in online mode", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

   }

   // =========== UPDATE VIDEO TIME ============
   private Runnable UpdateSongTime = new Runnable() {
       
      @TargetApi(Build.VERSION_CODES.GINGERBREAD) public void run() {

         startTime = player.getCurrentPosition();
         startTimeField.setText(String.format("%02d:%02d", 
            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) startTime)))
         );
         seekbar.setProgress((int)startTime);
         myHandler.postDelayed(this, 100);
      }
   };

      // =========== UPDATE VIDEO TIME ============
   private Runnable UpdateSongTimeOffline = new Runnable() {
       
      @TargetApi(Build.VERSION_CODES.GINGERBREAD) public void run() {

         startTime = player.getCurrentPosition();
         startTimeField.setText(String.format("%02d:%02d", 
            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) startTime)))
         );
         seekbar.setProgress((int)startTime);
         offlineHandler.postDelayed(this, 100);
      }
   };

   // =========== PAUSE VIDEO ================
    public void pause(View view){
      Toast.makeText(mContext, "Pause", 
      Toast.LENGTH_SHORT).show();

      player.pause();
      pauseButton.setEnabled(false);
      playButton.setEnabled(true);
   }    

   // ========== SEEK FORWARD ================
   public void forward(View view){
      int temp = (int)startTime;
      if((temp+forwardTime)<=finalTime){
         startTime = startTime + forwardTime;
         player.seekTo((int) startTime);
      }
      else{
         Toast.makeText(mContext, 
         "Cannot jump forward 5 seconds", 
         Toast.LENGTH_SHORT).show();
      }

   }

    // ========= REWIND ==============
    public void rewind(View view){
      int temp = (int)startTime;
      if((temp-backwardTime)>0){
         startTime = startTime - backwardTime;
         player.seekTo((int) startTime);
      }
      else{
         Toast.makeText(mContext, 
         "Cannot jump backward 5 seconds",
         Toast.LENGTH_SHORT).show();
      }

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
               // downloadProgressBar.setVisibility(4);
                Toast.makeText(mContext, "Download completed ", Toast.LENGTH_LONG).show();
                registerDownloadedAsset();
                IS_DOWNLOADED = true;
            }

            @Override
            public void onProgressChange(DownloadItem item, long downloadedBytes) {
                //Log.d(TAG, "progress: " + downloadedBytes);
                //Toast.makeText(mContext, "progress: " + downloadedBytes, Toast.LENGTH_LONG).show();
                float progress  = downloadedBytes * 100 / item.getEstimatedSizeBytes();
                downloadProgressBar.setProgress((int)progress);           

            }

            @Override
            public void onDownloadStart(DownloadItem item) {
                Log.d(TAG, "start: " + item);
               // downloadProgressBar.setVisibility(0);
               // downloadProgressBar.setMax(100);
            }

            @Override
            public void onDownloadPause(DownloadItem item) {
                Log.d(TAG, "pause: " + item);
            }

            @Override
            public void onDownloadFailure(DownloadItem item, Exception error) {
                Log.d(TAG, "failure: " + item);
                Toast.makeText(mContext,  "failure: " + item, Toast.LENGTH_SHORT).show();
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
                    File localFile = contentManager.getLocalFile(ASSET_ID);
                    if (localFile == null) {
                        Toast.makeText(mContext, "OFFLINE MODE OFF", Toast.LENGTH_LONG).show();
                        IS_DOWNLOADED = false;
                        offlineButton.setImageResource(R.drawable.offline_icon_off);
                    }else {
                        Toast.makeText(mContext, "OFFLINE MODE ON", Toast.LENGTH_LONG).show();
                        IS_DOWNLOADED = true;
                        offlineButton.setImageResource(R.drawable.offline_icon_on);
                    }

                    if (IS_DOWNLOADABLE == false){
                        offlineButton.setImageResource(R.drawable.offline_icon_unable);
                    }
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
                Toast.makeText(mContext, "OFFLINE MODE OFF", Toast.LENGTH_LONG).show();
                removeDownload();
                IS_DOWNLOADED = false;
                offlineButton.setEnabled(true);
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
                        Toast.makeText(mContext, "OFFLINE MODE ON", Toast.LENGTH_LONG).show();
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
        playButton = (ImageButton) flowContainer.findViewById(R.id.play_pause_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //If player is playing, change text of the button and pause.
                   // playPauseButton.setText(R.string.play_text);
                    player.pause();
                } else {
                    //If player is not playing, change text of the button and play.
                   // playPauseButton.setText(R.string.pause_text);
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
        //Set the format of the source. In our case it will be mp4 (can be other for example hls).
        mediaSource.setMediaFormat(PKMediaFormat.mp4);
        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

     // ============== INTERNET CONNECTION =========

    private static boolean netIsAvailable() {
    try {
        final URL url = new URL("http://www.google.com");
        final URLConnection conn = url.openConnection();
        conn.connect();
        return true;
    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        return false;
    }
    }
    /**
     *    ==========   REACT PROPS ==============
     */

    @ReactProp(name = "sourceUrl")
    public void setSourceUrl(View view, String prop) {

        SOURCE_URL = prop;
        ASSET_URL = prop;

        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Create instance of the player.
        // ADD PLAYERVIEW TO MAIN CONTAINER
        View playerView = player.getView();
        //add player view to ViewGroup
        
        player.prepare(mediaConfig);


        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation
    }

    @ReactProp(name = "entryId")
    public void setEntryId(View view, String prop) {
        ENTRY_ID = prop;
        ASSET_ID = prop;

        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Create instance of the player.
        // ADD PLAYERVIEW TO MAIN CONTAINER
        View playerView = player.getView();
        //add player view to ViewGroup
        
        player.prepare(mediaConfig);
        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation

    }
    @ReactProp(name = "partnerId")
    public void setPartnerId(View view, String prop) {
        MEDIA_SOURCE_ID = prop;

        mediaConfig = new PKMediaConfig();
        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();
        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Create instance of the player.
        // ADD PLAYERVIEW TO MAIN CONTAINER
        View playerView = player.getView();
        //add player view to ViewGroup
        
        player.prepare(mediaConfig);
        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation

    }
    @ReactProp(name = "canDownload")
    public void setCanDownload(View view, boolean prop) {
        IS_DOWNLOADABLE = prop;
        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation
        if(prop == true) {
            Toast.makeText(mContext, "Can download", Toast.LENGTH_LONG).show();
              offlineButton.setEnabled(true);
              offlineButton.setImageResource(R.drawable.offline_icon_off);
        }else {
             Toast.makeText(mContext, "Can't download", Toast.LENGTH_LONG).show();
             offlineButton.setEnabled(false);
             offlineButton.setImageResource(R.drawable.offline_icon_unable);
        }
    } 
}

/*
    // PROPS NEEDED FOR SIMPLE PLAYER 
    private static final String SOURCE_URL = "https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4";
    private static final String ENTRY_ID = "1_89fm8xyq"; // ENTRY_ID
    private static final String MEDIA_SOURCE_ID = "1821821"; // PARTNER_ID
    
    private static final String PLAYER_TYPE = "OFFLINE"; // CAN BE OFFLINE ALSO

    // PROPS  NEEDED FOR OFFLINE PLAYER
    private static final String ASSET_URL = SOURCE_URL;
    private static final String ASSET_ID = ENTRY_ID; // ENTRY ID
    private static final String ASSET_LICENSE_URL = null;

    */