import React, { Component } from 'react'
import * as ECharts  from 'echarts'
// import EChartsReact from 'echarts-for-react'
import ret from '../../json/map/西安市.json'

export default class Map extends Component {

    componentDidMount(){
      let shopData
      axios.defaults.baseURL='http://127.0.0.1:8080'//设定baseURL
      axios.get('/district/merchantNumber',{

      }).then(value=>{
        shopData=value.result
      })
        let mcharts=ECharts.init(document.getElementById('xian'))
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
    }

    render() {
    return (
      <div id='xian' style={{width:600,height:400}}>
        
      </div>
    )
  }
}
