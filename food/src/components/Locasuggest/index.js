import React, { Component } from 'react'
import axios from 'axios';
import { Input } from 'antd';

const { Search } = Input;
const { TextArea } = Input;

export default class Locasuggest extends Component {
    state={longitude:null,latitude:null,urls:[],textvalue:[],returned:false,pageHeight:163.6}

    componentDidMount(){
        if(navigator.geolocation){
            navigator.geolocation.getCurrentPosition((position)=>{
                var longitude=position.coords.longitude//经度
                var latitude=position.coords.latitude//纬度
                // console.log(longitude)
                // console.log(latitude)
                this.setState({longitude})
                this.setState({latitude})
            })
        }else{
            console.log('unsupported')
        }
    }

    onSearch=(value)=>{
        axios.defaults.baseURL='https://meituan.wangminan.me'
        axios({
            //请求方法
            method:'POST',
            url:'/smartSuggestion',
            //请求体参数
            data:{
                keyword:value,
                latitude:this.state.latitude,
                longitude:this.state.longitude,
            }
        }).then(response=>{
            console.log(response.data.result)
            const {result}=response.data
            // console.log(response)
            let returnArr=[],urls=[]
            for(let i=0;i<10;i++){
                let str=""
                let obj=result[i]
                urls.push(obj.frontImg)
                str+=obj.title+'\n'
                str+="区位:"+obj.district+'\n'
                str+="商圈:"+obj.businessDistrict+'\n'
                str+="大众评分:"+obj.avgScore+'\n'
                str+="评论数:"+obj.allCommentNum+'\n'
                str+="人均消费:"+obj.avgPrice+'\n'
                returnArr.push(str)
            }
            this.setState({urls})
            this.setState({textvalue:returnArr})
            this.setState({returned:true})
        })
    }

  render() {
    var items=[]
    for(let i=0;i<10;i++){
        items.push(
            <div>
                <TextArea placeholder="" autoSize value={this.state.textvalue[i]} style={{ width: '80%' }} />
                <img src={this.state.urls[i]} alt="pic" height={this.state.pageHeight} />
            </div>
        )
    }
    if(this.state.returned){
        return (
            <div>
                <p>本页面将根据您输入的关键词和您的地理位置为您做出综合推荐，请允许我们获取定位以提供最佳服务</p>
                <Search
                placeholder="请输入关键词"
                onSearch={this.onSearch}
                style={{ width: '80%' }}
                />
                {items}
            </div>
        )
    }else{
        return(
            <div>
                <p>本页面将根据您输入的关键词和您的地理位置为您做出综合推荐，请允许我们获取定位以提供最佳服务</p>
                <Search
                placeholder="请输入关键词"
                onSearch={this.onSearch}
                style={{ width: '80%' }}
                />
            </div>
        )
    }
    
  }
}
