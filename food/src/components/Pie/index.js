import React, { Component } from 'react'
import axios from 'axios'
import * as ECharts  from 'echarts'

export default class Pie extends Component {

    componentDidMount(){
      const {name}=this.props
      // console.log(showPie)
      axios.defaults.baseURL='https://meituan.wangminan.me'
      axios.get('/district/merchantType',{

      }).then(value=>{
        const {result}=value.data
        let names=[]
        for(let i=0;i<result.length;i++){
          let tmpname=Object.keys(result[i])
          names.push(tmpname[0])
        }
        this.setState({names})
        let allPie=[]
        for(let i=0;i<result.length;i++){
          let data=Object.values(result[i])
          let xData=Object.keys(data[0])
          let yData=Object.values(data[0])
          let pieArr=[]
          for(let j=0;j<xData.length;j++){
            let tmp={name:null,value:null}
            tmp.name=xData[j]
            tmp.value=yData[j]
            pieArr.push(tmp)
          }
          allPie.push(pieArr)
        }
        this.setState({allPie})
        var mcharts=ECharts.init(document.getElementById(name))
        var option={
          series:{
              type:'pie',
              data:allPie[0],
              label:{//配置饼图文字显示
                  show:true,
                  formatter:function(arg){//决定显示文字的内容
                      return arg.name+'类'+arg.value+'家\n'+arg.percent+'%'
                  }
              }
          }
        }
        mcharts.setOption(option)
      })
    }

    componentDidUpdate(){
      const {name,showPie}=this.props
      const {allPie,names}=this.state
      let showNo
      for(let i=0;i<names.length;i++){
        if(names[i]===showPie){
          showNo=i
        }
      }
      var mcharts=ECharts.init(document.getElementById(name))
        var option={
          series:{
              type:'pie',
              data:allPie[showNo],
              label:{//配置饼图文字显示
                  show:true,
                  formatter:function(arg){//决定显示文字的内容
                      return arg.name+'类'+arg.value+'家\n'+arg.percent+'%'
                  }
              },
              selectedMode:'multiple',//选中效果 single：选中时偏离 multiple：可同时存在多个偏离元素
              selectedOffset:50,//设置选中偏离距离
          }
        }
        mcharts.setOption(option)
    }

  render() {
    const {name,showPie}=this.props
    return (
      <div>
        <p>{showPie}各商户类别占比</p>
        <div id={name} style={{width:600,height:500,position:'relative',top:'0px'}}></div>
      </div>
    )
  }
}
