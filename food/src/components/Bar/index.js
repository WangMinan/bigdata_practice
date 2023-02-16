import React, { Component } from 'react'
import * as ECharts  from 'echarts'
import axios from 'axios';

export default class Bar extends Component {

    componentDidMount(){
        const {name,gname}=this.props
        let yDataArr=[],xDataArr=[]
        axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
        axios.get('/district/avgPrice',{

        }).then(value=>{
        let yData=Object.values(value.data.result)
        let xData=Object.keys(value.data.result)
        // console.log(xDataArr)
        for(let i=0;i<xData.length;i++){
            xDataArr.push(xData[i])
        }
        for(let i=0;i<yData.length;i++){
            yDataArr.push(yData[i])
        }
        var mcharts=ECharts.init(document.getElementById(name))
        var option={
            title:{
                x:'center',
                text:gname,
                textStyle:{
                    color:'red'
                }
            },
            yAxis:{//将x轴y轴配置互换即可实现横向柱状图
                type:'category',
                data:xDataArr,
            },
            xAxis:{
                type:'value'
            },
            series:[
                {
                    name:gname,
                    type:'bar',
                    markLine:{//平均值标记
                        data:[
                            {
                                type:'average',
                                name:'平均值'
                            }
                        ]
                    },
                    label:{
                        show:true
                    },
                    data:yDataArr,
                }
            ]
        }
        mcharts.setOption(option)
        })
    }
    
  render() {
    const {name}=this.props
    return (
        <div>
            <div id={name} style={{width:600,height:400}}></div>
        </div>
    )
  }
}
