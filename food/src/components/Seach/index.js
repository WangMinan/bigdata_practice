import React, { Component } from 'react'
import axios from 'axios';
import { Input } from 'antd';
import { AutoComplete,Pagination,Button } from 'antd'

// const { Search } = Input;
const { TextArea } = Input;

export default class Seach extends Component {
    state={textvalue:[],dataSource:[],pagesize:5,urls:[],pages:0,nowSeach:null,pagemod:0,needmod:false}
    handleChange=(e)=>{
        // console.log(e)
        let datas=["加载中..."]
        this.setState({dataSource:datas})
        axios.defaults.baseURL='https://meituan.wangminan.me'
        if(e===null)return
        axios({
            //请求方法
            method:'POST',
            url:'/suggestion',
            //请求体参数
            data:{
                keyword:e
            }
        }).then(response=>{
            // console.log(response.data.result)
            const {result}=response.data
            this.setState({dataSource:result})
        })
    }

    onSelect=(value)=>{//此处的value为用户最后从联想栏中选择的内容
        // console.log('onSelect', value);
        this.setState({nowSeach:value})
    }

    onSeach=()=>{
        axios({
            //请求方法
            method:'POST',
            url:'/query',
            //请求体参数
            data:{
                query:this.state.nowSeach,
                pageNum:1,
                pageSize:this.state.pagesize,
            }
        }).then(response=>{
            const {pagesize}=this.state
            const {result}=response.data
            const {merchants}=response.data.result
            // console.log(result.total)
            // console.log(pagesize)
            if(result.total%pagesize===0){//计算页码
                // console.log(result.total/pageSize)
                this.setState({pages:result.total/pagesize})
            }else{
                this.setState({pagemod:result.total%pagesize})
                this.setState({pages:(result.total-result.total%pagesize)/pagesize+1})
            }
            // console.log(this.state.pages)
            let returnArr=[],urls=[]
            for(let i=0;i<this.state.pagesize;i++){
                let str=""
                let obj=merchants[i]
                urls.push(obj.frontImg)
                str+=obj.title+'\n'
                str+="区位"+obj.district+'\n'
                str+="大众评分"+obj.avgScore+'\n'
                str+="人均消费"+obj.avgPrice+'\n'
                returnArr.push(str)
            }
            this.setState({urls})
            this.setState({textvalue:returnArr})
        })
    }

    handlePageChange=(Num)=>{
        axios({
            //请求方法
            method:'POST',
            url:'/query',
            //请求体参数
            data:{
                query:this.state.nowSeach,
                pageNum:Num,
                pageSize:this.state.pagesize,
            }
        }).then(response=>{
            const {merchants}=response.data.result
            // console.log(this.state.pages)
            let returnArr=[],urls=[]
            if(this.state.pagemod!==0&&Num===this.state.pages){
                this.setState({needmod:true})
                for(let i=0;i<this.state.pagemod;i++){
                    let str=""
                    let obj=merchants[i]
                    urls.push(obj.frontImg)
                    str+=obj.title+'\n'
                    str+="区位"+obj.district+'\n'
                    str+="大众评分"+obj.avgScore+'\n'
                    str+="人均消费"+obj.avgPrice+'\n'
                    returnArr.push(str)
                }
            }else{
                this.setState({needmod:false})
                for(let i=0;i<this.state.pagesize;i++){
                    let str=""
                    let obj=merchants[i]
                    urls.push(obj.frontImg)
                    str+=obj.title+'\n'
                    str+="区位"+obj.district+'\n'
                    str+="大众评分"+obj.avgScore+'\n'
                    str+="人均消费"+obj.avgPrice+'\n'
                    returnArr.push(str)
                }
            }
            this.setState({urls})
            this.setState({textvalue:returnArr})
        })
    }

  render() {
    const {dataSource}=this.state
    var items=[]
    if(this.state.needmod){
        for(let i=0;i<this.state.pagemod;i++){
            items.push(
                <div>
                    <TextArea placeholder="" autoSize value={this.state.textvalue[i]} style={{ width: '80%' }} />
                    <img src={this.state.urls[i]} alt="pic" height={119.6} />
                </div>
            )
        }
    }else{
        for(let i=0;i<this.state.pagesize;i++){
            items.push(
                <div>
                    <TextArea placeholder="" autoSize value={this.state.textvalue[i]} style={{ width: '80%' }} />
                    <img src={this.state.urls[i]} alt="pic" height={119.6} />
                </div>
            )
        }
    }
    
    return (
      <div>
        <AutoComplete
          dataSource={dataSource}
          style={{ width: '80%' }}
          onSelect={this.onSelect}
          onSearch={this.onSearch}
          onChange={this.handleChange}
          placeholder="input here"
        />
        <Button type="primary" onClick={this.onSeach} >搜索</Button>
        {/* <Search
        placeholder="input search text"
        enterButton="Search"
        size="large"
        onSearch={value => console.log(value)}
        onChange={this.handleChange}
        /> */}
        {/* <TextArea placeholder="" autoSize value={this.state.textvalue[0]} style={{ width: '80%' }} />
        <img src={this.state.urls[0]} alt="picture1" height={119.6} />
        <TextArea placeholder="" autoSize value={this.state.textvalue[1]} style={{ width: '80%' }} />
        <img src={this.state.urls[1]} alt="picture2" height={119.6} />
        <TextArea placeholder="" autoSize value={this.state.textvalue[2]} style={{ width: '80%' }} />
        <img src={this.state.urls[2]} alt="picture3" height={119.6} />
        <TextArea placeholder="" autoSize value={this.state.textvalue[3]} style={{ width: '80%' }} />
        <img src={this.state.urls[3]} alt="picture4" height={119.6} />
        <TextArea placeholder="" autoSize value={this.state.textvalue[4]} style={{ width: '80%' }} />
        <img src={this.state.urls[4]} alt="picture5" height={119.6} /> */}
        {items}
        <Pagination defaultCurrent={1} total={this.state.pages*10} onChange={this.handlePageChange} showSizeChanger={false} />
      </div>
    )
  }
}
