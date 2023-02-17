import React, { Component } from 'react'
import { Navbar,Container } from 'react-bootstrap'
import { Button } from 'antd';

export default class Header extends Component {

  change=()=>{
    this.props.changePage()
  }

  render() {
    return (
      <div>
        <Navbar bg='light' variant='light' >
            <Container>
                <Navbar.Brand>
                    {/* src指定图片路径 width和height指定宽高 */}
                    <img src="./icons/鸡腿.png" alt="none" width={30} height={30} />{' '}
                    西安食品
                </Navbar.Brand>
                <Button type="primary" onClick={this.change} >转到{this.props.text}</Button>
            </Container>
        </Navbar>
      </div>
    )
  }
}
