import React, { Component } from 'react'
import * as ECharts  from 'echarts'
import axios from 'axios';

export default class Bar1 extends Component {

    componentDidMount(){
        const {name,gname}=this.props
        let yDataArr=[],xDataArr=[]
        axios.defaults.baseURL='https://meituan.wangminan.me'//设定baseURL
        axios.get('/type/number',{

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
            yAxis:{
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
                    markPoint:{//最大、最小值标记
                        data:[
                            {
                                type:'max',
                                name:'最大值'
                            },
                            {
                                type:'min',
                                name:'最小值'
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
        <div style={{width:'100%'}}>
            <div className='back' id={name} style={{width:850,height:600,position:'relative',left:'10px'}}></div>
        </div>
    )
  }
}
