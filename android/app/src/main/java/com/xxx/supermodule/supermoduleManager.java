//  Created by react-native-create-bridge

package com.xxx.supermodule;

import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;

import java.util.ArrayList;
import java.util.List;

public class supermoduleManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "supermodule";

    private ThemedReactContext mContext;
    private View view;

    private static final int START_POSITION = 60; // one minute.
/**
   NSString *partnerId = config[0];
   NSString *configId = config[1];
   NSString *entryId = config[2];
   NSString *url = config[3];
https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4
    const config = [
      '2358011',
      '41441941',
      '1_tyok377y',
      'http://cdnapi.kaltura.com', */

    //The url of the source to play
    private static final String SOURCE_URL = "https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4";

    private static final String ENTRY_ID = "1_89fm8xyq";
    private static final String MEDIA_SOURCE_ID =  "1_y1rbgvs6";

    private Player player;
    private PKMediaConfig mediaConfig;
  //  private MockMediaProvider mockProvider;

    @Override
    public String getName() {
        // Tell React the name of the module
        // https://facebook.github.io/react-native/docs/native-components-android.html#1-create-the-viewmanager-subclass
        return REACT_CLASS;
    }

    @Override
    public View createViewInstance(ThemedReactContext context){
        // Create a view here
        // https://facebook.github.io/react-native/docs/native-components-android.html#2-implement-method-createviewinstance
        mContext = context;

       //MediaEntryProvider mockProvider = new MediaEntryProvider("entries.playkit.json", this, "1_1h1vsv3z");               
        
        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);

        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        //Create player instance, using config object.
        //Create instance of the player.
        player = PlayKitManager.loadPlayer(mContext, null);                   
                   //Add player view to the layout.
                    view = new View(context);
                    View playerView = player.getView();
                    view = playerView;
                    //Prepare player with media configuration.
        player.prepare(mediaConfig);

        //Start the playback of the media.
        player.play();
                   
        return view;
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
        mediaSource.setId(MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(SOURCE_URL);

        //Set the format of the source. In our case it will be hls.
        mediaSource.setMediaFormat(PKMediaFormat.hls);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

    @ReactProp(name = "exampleProp")
    public void setExampleProp(View view, String prop) {
        // Set properties from React onto your native component via a setter method
        // https://facebook.github.io/react-native/docs/native-components-android.html#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation
    }
}
