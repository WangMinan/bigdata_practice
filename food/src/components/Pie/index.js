import React, { Component } from 'react'

export default class Pie extends Component {

    componentDidMount()

  render() {
    const {name}=this.props
    return (
      <div>
        <div id={name} style={{width:600,height:400}}></div>
      </div>
    )
  }
}
