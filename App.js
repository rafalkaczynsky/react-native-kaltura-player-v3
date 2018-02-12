import React, { Component } from 'react';
import {

  StyleSheet,
  View,

} from 'react-native';

import KalturaPlayer from './supermoduleNativeView';

export default class App extends Component<{}> {

  render() {

    return (
      <View style={styles.container}>
          <KalturaPlayer 

            configIOS={[
                  '2358011',     //partnerId
                  '41441941',    //configId
                  '1_tyok377y',  //entryId
                  'http://cdnapi.kaltura.com', //baseUrl / url
                  null
                ]}

            entryId={"1_tyok377y"}
            partnerId={"2358011"}
            sourceUrl={"https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4"}
            licence={null}
            playerType={"OFFLINE"}
            canDownload={true}
            width={380}
            height={250}
          />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    paddingBottom: 20,
    
  },
});


    /**
          <KalturaPlayer 
            configId={"41441941"}
            baseUrl={"https://cfvod.kaltura.com/"}
            entryId={"1_89fm8xyq"}
            partnerId={"1821821"}
            sourceUrl={"https://cfvod.kaltura.com/pd/p/1821821/sp/182182100/serveFlavor/entryId/1_89fm8xyq/v/1/flavorId/1_y1rbgvs6/name/a.mp4"}
            licence={null}
            playerType={"OFFLINE"}
            canDownload={true}
            width={380}
            height={250}
          />
     */