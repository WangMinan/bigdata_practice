// import logo from './logo.svg';
import Header from './components/Header';
import Map from './components/Map';
import Bar from './components/Bar';
import Pie from './components/Pie';
import 'bootstrap/dist/css/bootstrap.min.css'
// import axios from 'axios';
import { Component } from 'react';

class App extends Component {
  // constructor(props){
  //   super(props)
  //   console.log(1)
  //   axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
  //   axios.get('/district/avgPrice',{

  //   }).then(value=>{
  //     console.log(2)
  //     var capitay=Object.values(value.data.result)
  //     var capitax=Object.keys(value.data.result)
  //   })
  // }
  state={showPie:"灞桥区"}

  changePie=(name)=>{
    this.setState({showPie:name})
    // console.log(this.state.showPie)
  }

  render(){
    return (
      <div className="App">
        <Header/>
        <Map changePie={this.changePie} />
        {/* 餐饮种类占比 */}
        <Pie name="merchart" showPie={this.state.showPie} />
        {/* 人均价格分布表 */}
        <Bar name="capita" gname="餐饮消费人均价格分布"/>
      </div>
    );
  }
  
}

export default App;
