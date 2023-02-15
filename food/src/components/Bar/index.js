import React, { Component } from 'react'
import * as ECharts  from 'echarts'

export default class Bar extends Component {

    componentDidMount(){
        const {name,gname,xDataArr,yDataArr}=this.props
        // console.log(document.getElementById(name))
        // console.log(xDataArr)
        // console.log(this.props.gname)
        var mcharts=ECharts.init(document.getElementById(name))
        var option={
            title:{
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
