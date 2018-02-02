//  Created by react-native-create-bridge

import React, { Component } from 'react'
import { requireNativeComponent } from 'react-native'
import PropTypes from 'prop-types';

const supermodule = requireNativeComponent('supermodule', SupermoduleView)

export default class SupermoduleView extends Component {
  render () {
    return <supermodule {...this.props} />
  }
}

SupermoduleView.propTypes = {
  exampleProp: PropTypes.string
}
