// import logo from './logo.svg';
import Header from './components/Header';
import Map from './components/Map';
import Bar from './components/Bar';
import 'bootstrap/dist/css/bootstrap.min.css'
import axios from 'axios';
import { Component } from 'react';

class App extends Component {
  state={capitaxData:[],capitayData:[]}
  // componentDidMount(){
  //   axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
  //   axios.get('/district/avgPrice',{

  //   }).then(value=>{
  //     var capitay=Object.values(value.data.result)
  //     var capitax=Object.keys(value.data.result)
  //     this.setState({capitaxData:capitax})
  //     this.setState({capitayData:capitay})
  //   })
  // }
  constructor(props){
    super(props)
    console.log(1)
    axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
    axios.get('/district/avgPrice',{

    }).then(value=>{
      console.log(2)
      var capitay=Object.values(value.data.result)
      var capitax=Object.keys(value.data.result)
      this.state.capitaxData=capitax
      this.state.capitayData=capitay
      // this.setState({capitaxData:capitax})
      // this.setState({capitayData:capitay})
    })
  }
  render(){
    console.log(3)
    const {capitaxData,capitayData}=this.state
    console.log(capitaxData)
    if(capitaxData.length===0){
      console.log(4)
      return (<div></div>)
    }else{
      console.log(5)
      return (
        <div className="App">
          <Header/>
          <Map/>
          {/* 人均价格分布表 */}
          <Bar name="capita" gname="餐饮消费人均价格分布" xDataArr={capitaxData} yDataArr={capitayData} />
        </div>
      );
    }
    
  }
  
}

export default App;
