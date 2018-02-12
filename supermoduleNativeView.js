//  Created by react-native-create-bridge

import React, { Component } from 'react'
import { requireNativeComponent, ViewPropTypes, Platform } from 'react-native'
import PropTypes from 'prop-types';

const supermodule = Platform.OS === 'ios' ? requireNativeComponent('supermodule', SupermoduleView) : requireNativeComponent('supermodule', iface);

class SupermoduleView extends Component {
  render () {
    return <supermodule {...this.props} />
  }
}

var iface = {
  name: 'supermodule',
  propTypes: {
    entryId: PropTypes.string,
    partnerId: PropTypes.string,
    sourceUrl: PropTypes.string,
    licence: PropTypes.string,
    canDownload: PropTypes.bool,
    ...ViewPropTypes, 
  },
};

SupermoduleView.propTypes = {
  configEntries: PropTypes.array,

}

export default class KalturaPlayer extends Component {
  render () {
    return (
    <SupermoduleView
      configEntries={this.props.configIOS}
      entryId={this.props.entryId}
      partnerId={this.props.partnerId}
      sourceUrl={this.props.sourceUrl}
      licence={this.props.licence}
      canDownload={this.props.canDownload}
      style={{width: this.props.width, height: this.props.height }}
  />)
  }
}