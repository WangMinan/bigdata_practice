import React, { Component } from 'react'
import axios from 'axios';
import { Input } from 'antd';
import { AutoComplete,Pagination,Button,Radio } from 'antd'

// const { Search } = Input;
const { TextArea } = Input;

const clas=["全部（不启用筛选）","代金券","蛋糕甜点","火锅","自助餐","小吃快餐","日韩料理","西餐","聚餐宴请","烧烤烤肉","东北菜","川湘菜","江浙菜","香锅烤鱼","粤菜","中式烧烤_烤串","西北菜","咖啡酒吧","京菜鲁菜","云贵菜","东南亚菜","海鲜","素食","台湾_客家菜","创意菜","汤_粥_炖菜","蒙餐","新疆菜","其他美食"]
export default class Seach extends Component {
    state={textvalue:[],dataSource:[],pagesize:5,urls:[],pages:0,nowSeach:null,pagemod:0,needmod:false,classchack:null,reutrned:false,pageHeight:163.6,subtitles:[[<option>--请先选择区划--</option>]],leftsub:[],rightuse:0,busD:null,bds:[]}
    componentDidMount(){
        axios.defaults.baseURL='https://meituan.wangminan.me'
        axios({
            //请求方法
            method:'GET',
            url:'/district/list',
        }).then(response=>{
            const {result}=response.data
            // console.log(response.data.result)
            let subtitles=[]
            let tmp=Object.keys(result)
            //双下拉菜单 左
            let leftsub=[]
            leftsub.push(<option>--请选择区划--</option>)
            for(let i=0;i<tmp.length;i++){
                leftsub.push(
                    <option>{tmp[i]}</option>
                )
            }
            this.setState({leftsub})

            //双下拉菜单 右
            let tmpp=Object.values(result)
            this.setState({bds:tmpp})
            subtitles.push([<option>--请先选择区划--</option>])
            for(let i=0;i<tmp.length;i++){
                let tpmenu=tmpp[i]
                let rightitem=[]
                rightitem.push(<option>--请选择商圈--</option>)
                for(let j=0;j<tpmenu.length;j++){
                    rightitem.push(<option>{tpmenu[j]}</option>)
                }
                subtitles.push(rightitem)
            }
            this.setState({subtitles})
        })
    }

    handleChange=(e)=>{//输入联想
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

    onSeach=()=>{//初次搜索
        axios({
            //请求方法
            method:'POST',
            url:'/query',
            //请求体参数
            data:{
                query:this.state.nowSeach,
                pageNum:1,
                pageSize:this.state.pagesize,
                type:this.state.classchack,
                businessDistrict:this.state.busD,
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
                str+="区位:"+obj.district+'\n'
                str+="商圈:"+obj.businessDistrict+'\n'
                str+="大众评分:"+obj.avgScore+'\n'
                str+="评论数:"+obj.allCommentNum+'\n'
                str+="人均消费:"+obj.avgPrice+'\n'
                returnArr.push(str)
            }
            this.setState({urls})
            this.setState({textvalue:returnArr})
            this.setState({reutrned:true})
        })
    }

    handlePageChange=(Num)=>{//翻页
        axios({
            //请求方法
            method:'POST',
            url:'/query',
            //请求体参数
            data:{
                query:this.state.nowSeach,
                pageNum:Num,
                pageSize:this.state.pagesize,
                type:this.state.classchack,
                businessDistrict:this.state.busD,
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
                    str+="区位:"+obj.district+'\n'
                    str+="商圈:"+obj.businessDistrict+'\n'
                    str+="大众评分:"+obj.avgScore+'\n'
                    str+="评论数:"+obj.allCommentNum+'\n'
                    str+="人均消费:"+obj.avgPrice+'\n'
                    returnArr.push(str)
                }
            }else{
                this.setState({needmod:false})
                for(let i=0;i<this.state.pagesize;i++){
                    let str=""
                    let obj=merchants[i]
                    urls.push(obj.frontImg)
                    str+=obj.title+'\n'
                    str+="区位:"+obj.district+'\n'
                    str+="商圈:"+obj.businessDistrict+'\n'
                    str+="大众评分:"+obj.avgScore+'\n'
                    str+="评论数:"+obj.allCommentNum+'\n'
                    str+="人均消费:"+obj.avgPrice+'\n'
                    returnArr.push(str)
                }
            }
            this.setState({urls})
            this.setState({textvalue:returnArr})
        })
    }

    onClassChange=(e)=>{
        // console.log(e.target.value)
        if(e.target.value===0){
            this.setState({classchack:null})
        }else{
            this.setState({classchack:clas[e.target.value]})
        }
    }

    onMenuChange=(e)=>{
        // console.log(e.target.selectedIndex)
        this.setState({rightuse:e.target.selectedIndex})
    }

    onMenuChange1=(e)=>{
        const {selectedIndex}=e.target
        if(selectedIndex===0){
            this.setState({busD:null})
        }else{
            const arr=this.state.bds[this.state.rightuse-1]
            let bu=arr[selectedIndex-1]
            this.setState({busD:bu})
        }
        console.log(this.state.busD)
    }

  render() {
    const {dataSource}=this.state
    var items=[]
    if(this.state.needmod){
        for(let i=0;i<this.state.pagemod;i++){
            items.push(
                <div>
                    <TextArea placeholder="" autoSize value={this.state.textvalue[i]} style={{ width: '80%' }} />
                    <img src={this.state.urls[i]} alt="pic" height={this.state.pageHeight} />
                </div>
            )
        }
    }else{
        for(let i=0;i<this.state.pagesize;i++){
            items.push(
                <div>
                    <TextArea placeholder="" autoSize value={this.state.textvalue[i]} style={{ width: '80%' }} />
                    <img src={this.state.urls[i]} alt="pic" height={this.state.pageHeight} />
                </div>
            )
        }
    }

    //类别筛选
    var radioitem=[]
    for(let i=0;i<clas.length;i++){
        radioitem.push(<Radio.Button value={i}>{clas[i]}</Radio.Button>)
    }
    if(this.state.reutrned){
        return (
        <div>
            <select id='left' onChange={this.onMenuChange} >
                {this.state.leftsub}
            </select>
            <select id='right' onChange={this.onMenuChange1} >
                {this.state.subtitles[this.state.rightuse]}
            </select>
            <Radio.Group defaultValue="0" buttonStyle="solid" onChange={this.onClassChange} >
                {radioitem}
            </Radio.Group>
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
            {items}
            <Pagination defaultCurrent={1} total={this.state.pages*10} onChange={this.handlePageChange} showSizeChanger={false} />
        </div>
        )
    }else{
        return(
        <div>
            <select id='left' onChange={this.onMenuChange} >
                {this.state.leftsub}
            </select>
            <select id='right' onChange={this.onMenuChange1} >
                {this.state.subtitles[this.state.rightuse]}
            </select>
            <Radio.Group defaultValue="" buttonStyle="solid" onChange={this.onClassChange} >
                {radioitem}
            </Radio.Group>
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
            <Pagination defaultCurrent={1} total={this.state.pages*10} onChange={this.handlePageChange} showSizeChanger={false} />
        </div>
        )
        
    }
    
  }
}
