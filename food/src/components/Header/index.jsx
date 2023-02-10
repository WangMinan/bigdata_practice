import React, { Component } from 'react'
import { Navbar,Container } from 'react-bootstrap'

export default class Header extends Component {
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
            </Container>
        </Navbar>
      </div>
    )
  }
}
