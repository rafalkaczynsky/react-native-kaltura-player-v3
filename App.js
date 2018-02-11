/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Dimensions,
  TouchableOpacity,
  Image
} from 'react-native';

import SupermoduleView from './supermoduleNativeView';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu'
});

var { height, width } = Dimensions.get('window');

export default class App extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      fullScreen: false,
      width: 380,
      height: 250
    };
  }

  handleFullScreen(){

    if (!this.state.fullScreen){
      this.setState({width: width, height: height, fullScreen: true})
    }
    else{
      this.setState({width: 380, height: 250, fullScreen: false})
    }
  }

  render() {
    /**
     *  
     * 
   NSString *partnerId = config[0];
   NSString *configId = config[1];
   NSString *entryId = config[2];
   NSString *url = config[3];
     * 
     */
    const config = [
      '2358011',
      '41441941',
      '1_tyok377y',
      'http://cdnapi.kaltura.com',
      null
    ];
    return (
      <View style={styles.container}>
        <View>
          <View style={{ position: 'absolute', zIndex: 1000, top:20, right: 20, alignItems: 'flex-end' , paddingTop: this.state.fullScreen ? 80 : 10, paddingRight: this.state.fullScreen ? 20 : 10}}>
            <TouchableOpacity onPress={() => this.handleFullScreen()}>
            <Image
          style={{width: 22, height: 20}}
          source={require('./src/images/fullscreen.png')}
        />
            </TouchableOpacity>
          </View>

          {/* ------ OUR REACT - NATIVE CALTURA PLAYER------ */}
          <SupermoduleView
            configEntries={config}
            style={{width: this.state.width, height: this.state.height }}
          />
        {/* ----------------------------------------------- */}
        </View>
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
