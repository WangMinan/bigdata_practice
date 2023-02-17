import React, { Component } from 'react'
import axios from 'axios';
import { Input } from 'antd';
import { AutoComplete,Pagination,Button,Radio } from 'antd'
import { Menu, Dropdown } from 'antd'
import { Icon } from '@ant-design/compatible'

// const { Search } = Input;
const { TextArea } = Input;
const { SubMenu } = Menu;

const clas=["全部（不启用筛选）","代金券","蛋糕甜点","火锅","自助餐","小吃快餐","日韩料理","西餐","聚餐宴请","烧烤烤肉","东北菜","川湘菜","江浙菜","香锅烤鱼","粤菜","中式烧烤_烤串","西北菜","咖啡酒吧","京菜鲁菜","云贵菜","东南亚菜","海鲜","素食","台湾_客家菜","创意菜","汤_粥_炖菜","蒙餐","新疆菜","其他美食"]
export default class Seach extends Component {
    state={textvalue:[],dataSource:[],pagesize:5,urls:[],pages:0,nowSeach:null,pagemod:0,needmod:false,classchack:null,reutrned:false,pageHeight:163.6,subtitles:null}
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
            let tmpp=Object.values(result)
            for(let i=0;i<tmp.length;i++){
                let itemss=[]
                let tpmenu=tmpp[i]
                // console.log(tmpp[i])
                for(let j=0;j<tpmenu.length;j++){
                    itemss.push(<Menu.Item>{tpmenu[j]}</Menu.Item>)
                }
                subtitles.push(
                    <SubMenu title={tmp[i]}>
                        {itemss}
                    </SubMenu>
                )
            }
            this.setState({subtitles:(<div>{subtitles}</div>)})
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
            <Dropdown menu={this.state.subtitles} fallback={<h2>loading</h2>} >
                <div>
                    <a className="ant-dropdown-link" onClick={e => e.preventDefault()}>
                    Cascading menu <Icon type="down" />
                    </a>
                </div>
            </Dropdown>
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
            <Dropdown menu={this.state.subtitles} fallback={<h2>loading</h2>} >
                <div>
                    <a className="ant-dropdown-link" onClick={e => e.preventDefault()}>
                    Cascading menu <Icon type="down" />
                    </a>
                </div>
            </Dropdown>
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
