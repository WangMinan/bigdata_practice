import React, { Component } from 'react'
import * as ECharts  from 'echarts'
import ret from '../../json/map/西安市.json'
import axios from 'axios'
import './index.css'

export default class Map extends Component {

  state={shopData:null,commentData:null,use:1,mcharts:null,tex:"按商户数量"}
    componentDidMount(){
      const {changePie}=this.props
      let shopData=[],commentData=[]
      axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
      axios.get('/district/merchantNumber',{

      }).then(value=>{
        let values=Object.values(value.data.result)
        let keys=Object.keys(value.data.result)
        for(let i=0;i<values.length;i++){
          let tmp={name:keys[i],value:values[i]}
          shopData.push(tmp)
        }
        this.setState({shopData})
      })
      axios.get('/district/flow',{

      }).then(value=>{
        // commentData=value.data.result
        let values=Object.values(value.data.result)
        let keys=Object.keys(value.data.result)
        for(let i=0;i<values.length;i++){
          let tmp={name:keys[i],value:values[i]}
          commentData.push(tmp)
        }
        this.setState({commentData})
      })
      let mcharts=ECharts.init(document.getElementById('xian'))
      this.setState({mcharts})
      ECharts.registerMap('xianmap',ret)
      let option={
          geo:{
              type:'map',
              map:'xianmap',
              roam:true,
              zoom:1,
          },
          series:[
            {
              data:shopData,
              geoIndex:0,
              type:'map'
            }
          ],
          visualMap:{
            min:0,
            max:3000,
            inRange:{
                color:['white','red'],//指定渐变色区域
            },
            calculable:true,//允许数据筛选（利用滑块）
          }
      }
      mcharts.setOption(option)
      mcharts.on('click',function(params){
        changePie(params.data.name)
      })
    }

    componentDidUpdate(){
      const {use,mcharts,shopData,commentData}=this.state
      let option
      if(use===2){
        option={
          series:[
            {
              data:commentData,
              geoIndex:0,
              type:'map'
            }
          ],
          visualMap:{
            min:0,
            max:1500000,
            inRange:{
                color:['white','green'],//指定渐变色区域
            },
            calculable:true,//允许数据筛选（利用滑块）
          }
        }
      }else if(use===1){
        option={
          series:[
            {
              data:shopData,
              geoIndex:0,
              type:'map'
            }
          ],
          visualMap:{
            min:0,
            max:3000,
            inRange:{
                color:['white','red'],//指定渐变色区域
            },
            calculable:true,//允许数据筛选（利用滑块）
          }
        }
      }
      mcharts.setOption(option)
    }

    changeMap=()=>{
      //更改地图的着色数据 1:shop 2:comment
      const {use}=this.state
      if(use===1){
        this.setState({use:2})
        this.setState({tex:"按评论数量"})
      }else if(use===2){
        this.setState({use:1})
        this.setState({tex:"按商户数量"})
      }
    }

    render() {
    return (
      <div style={{width:'100%'}} >
        <div className='back' id='xian' style={{width:600,height:600,borderStyle:'solid'}} >
        
        </div>
        <div className='back' style={{width:100,height:30}} >
          <button onClick={this.changeMap}>{this.state.tex}</button>
        </div>
      </div>
      
    )
  }
}
