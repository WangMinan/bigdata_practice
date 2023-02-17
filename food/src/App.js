// import logo from './logo.svg';
import Header from './components/Header';
import Map from './components/Map';
import Bar from './components/Bar';
import Pie from './components/Pie';
import Bar1 from './components/Bar1';
import Bar2 from './components/Bar2';
import Bar3 from './components/Bar3';
import Seach from './components/Seach';
import Locasuggest from './components/Locasuggest';
import 'bootstrap/dist/css/bootstrap.min.css'
// import axios from 'axios';
import { Component } from 'react';
import { Card } from 'antd';
const gridStyle = {
  width: '50%',
  textAlign: 'center',
};
const gridStyle1 = {
  width: '100%',
  textAlign: 'center',
};
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
  state={showPie:"灞桥区",issuggset:false,text1:'智能推荐',text2:'网页搜索'}

  changePie=(name)=>{
    this.setState({showPie:name})
    // console.log(this.state.showPie)
  }

  changePage=()=>{
    this.setState({issuggset:!this.state.issuggset})
  }

  render(){
    if(this.state.issuggset){
      return(
        <div className='App'>
          <Header changePage={this.changePage} text={this.state.text2} />
          <Locasuggest />
        </div>
      )
    }else{
      return (
        <div className="App">
          <Header changePage={this.changePage} text={this.state.text1} />
          <Seach/>
          <Card title="Card Title">
            <Card.Grid hoverable={false} style={gridStyle}>
              <Map changePie={this.changePie} />
            </Card.Grid>
            <Card.Grid hoverable={false} style={gridStyle}>
              {/* 餐饮种类占比 */}
              <Pie name="merchart" showPie={this.state.showPie} />
            </Card.Grid>
            <Card.Grid style={gridStyle}>
              {/* 人均价格分布表 */}
              <Bar name="capita" gname="餐饮消费人均价格分布"/>
            </Card.Grid>
            <Card.Grid style={gridStyle}>
              {/* 各类别餐饮店铺总数 */}
              <Bar1 name="total" gname="各类别餐饮店铺总数" />
            </Card.Grid>
            <Card.Grid style={gridStyle1}>
              {/* 各类别餐饮店铺人均价格比较 */}
              <Bar2 name="avgPrice" gname="各类别餐饮店铺人均价格比较" />
            </Card.Grid>
            <Card.Grid style={gridStyle1}>
              {/* 各类别餐饮店铺历史评价数量比较 */}
              <Bar3 name="flow" gname="各类别餐饮店铺历史评价数量比较" />
            </Card.Grid>
          </Card>
        </div>
      );
    }
    
  }
  
}

export default App;
