import re
import string


class MeituanPages:
    district_name: string
    business_district_name: string
    code: string

    # 构造函数
    def __init__(self, district_name, business_district_name, code):
        self.district_name = district_name
        self.business_district_name = business_district_name
        self.code = code


xian_meituan_list: list[MeituanPages] = []


def init_meituan_list():
    # ---------------------------------碑林区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" 
    href="http://xa.meituan.com/meishi/b113/">全部</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b6835/">东大街</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b7137/">南大街</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b6838/">互助立交</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b897/">钟楼/鼓楼</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b898/">西北大/西工大</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b899/">和平门/建国门</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b908/">交大东校区/理工大</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b7402/">小雁塔/南稍门</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b7404/">东关正街</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b8975/">太乙路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b9012/">长安立交</a></li><li><a class="on" 
    href="http://xa.meituan.com/meishi/b15634/">怡丰城/西荷花园</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15639/">互助路立交</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15642/">李家村</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15643/">朱雀大街北段</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15664/">文艺路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15667/">何家村/黄雁村</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15785/">广济街</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b37380/">建工路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b41567/">省体育场</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b41838/">长乐坊</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b42104/">朱雀门</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b43041/">东门外</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b45172/">南院门</a></li></ul>"""
    insert_meituan_list("碑林区", html_str)
    # 接下来几个区也执行同样操作
    # ---------------------------------雁塔区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" 
    href="http://xa.meituan.com/meishi/b116/">全部</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b907/">小寨</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b4763/">曲江新区</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b6836/">大雁塔</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b7403/">朱雀大街南段</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b8977/">南二环西段</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b8978/">南二环东段</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b8979/">明德门</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b8984/">雁翔路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15629/">大寨路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15630/">西影路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15632/">电视台/会展中心</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15635/">含光路南段</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15638/">徐家庄</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15640/">杨家村</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15641/">吉祥村</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b15644/">三森</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b43777/">翠华路</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b44198/">太白立交</a></li><li><a class="" 
    href="http://xa.meituan.com/meishi/b45974/">长安路</a></li></ul>"""
    insert_meituan_list("雁塔区", html_str)
    # ---------------------------------高新区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b119/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b910/">高新路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b1099/">电子城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8980/">光华路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8983/">科技路西口</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8991/">唐延路南段</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15633/">绿地世纪城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15636/">西万路口</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15637/">枫林绿洲</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15646/">高新软件园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15647/">玫瑰大楼</a></li><li><a class="" href="http://xa.meituan.com/meishi/b40282/">丈八</a></li><li><a class="" href="http://xa.meituan.com/meishi/b41015/">科技路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42238/">太白南路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b43326/">唐延路沿线</a></li></ul>"""
    insert_meituan_list("高新区", html_str)
    # ---------------------------------莲湖区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b115/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b903/">劳动公园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7479/">丰庆公园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b904/">汉城路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b905/">莲湖公园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b906/">西大街</a></li><li><a class="" href="http://xa.meituan.com/meishi/b6839/">北大街</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7408/">西稍门</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7480/">劳动南路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8982/">土门</a></li><li><a class="" href="http://xa.meituan.com/meishi/b14024/">大兴新区</a></li><li><a class="" href="http://xa.meituan.com/meishi/b14025/">红庙坡</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15631/">唐延路北段</a></li><li><a class="" href="http://xa.meituan.com/meishi/b41287/">北关</a></li><li><a class="" href="http://xa.meituan.com/meishi/b41982/">大皮院/西羊市</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42102/">安定门</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42109/">甜水井</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42751/">桃园路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b43211/">桥梓口</a></li><li><a class="" href="http://xa.meituan.com/meishi/b43888/">玉祥门</a></li></ul>"""
    insert_meituan_list("莲湖区", html_str)
    # ---------------------------------未央区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b117/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7407/">朱宏路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7478/">明光路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7477/">文景路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7140/">大明宫</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8951/">赛高街区</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8952/">城市运动公园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7141/">辛家庙</a></li><li><a class="" href="http://xa.meituan.com/meishi/b902/">金花路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b909/">未央路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7138/">北大学城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7406/">太华路沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8950/">龙首</a></li><li><a class="" href="http://xa.meituan.com/meishi/b9309/">大明宫万达</a></li><li><a class="" href="http://xa.meituan.com/meishi/b9477/">红旗厂</a></li><li><a class="" href="http://xa.meituan.com/meishi/b13026/">三桥</a></li><li><a class="" href="http://xa.meituan.com/meishi/b18674/">矿山路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25592/">和平村</a></li><li><a class="" href="http://xa.meituan.com/meishi/b37367/">西安北站</a></li><li><a class="" href="http://xa.meituan.com/meishi/b40555/">张家堡</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42798/">雅荷花园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b45716/">二府庄</a></li><li><a class="" href="http://xa.meituan.com/meishi/b45746/">六号大院</a></li></ul>"""
    insert_meituan_list("未央区", html_str)
    # ---------------------------------新城区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b114/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7476/">新城广场</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7142/">民乐园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b901/">解放路/火车站</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7143/">胡家庙</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8974/">立丰国际</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8985/">韩森寨</a></li><li><a class="" href="http://xa.meituan.com/meishi/b39163/">万寿路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b39296/">康复路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b41607/">幸福路沿线</a></li></ul>"""
    insert_meituan_list("新城区", html_str)
    # ---------------------------------灞桥区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4251/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7398/">纺织城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7399/">十里铺</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7400/">白鹿原</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7401/">浐灞半岛/世园会</a></li><li><a class="" href="http://xa.meituan.com/meishi/b14199/">田洪正街</a></li><li><a class="" href="http://xa.meituan.com/meishi/b16010/">城东客运站</a></li><li><a class="" href="http://xa.meituan.com/meishi/b16013/">灞桥火车站</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25659/">长乐坡</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42254/">浐灞生态区</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42956/">御锦城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b44782/">田王洪庆</a></li><li><a class="" href="http://xa.meituan.com/meishi/b45226/">灞业大境</a></li></ul>"""
    insert_meituan_list("灞桥区", html_str)
    # ---------------------------------临潼区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4253/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8986/">兵马俑</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8987/">人民路/文化路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8989/">华清宫</a></li><li><a class="" href="http://xa.meituan.com/meishi/b8990/">东三岔</a></li><li><a class="" href="http://xa.meituan.com/meishi/b17228/">芷阳湖</a></li><li><a class="" href="http://xa.meituan.com/meishi/b44625/">华清池</a></li></ul>"""
    insert_meituan_list("临潼区", html_str)
    # ---------------------------------长安区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b235/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7145/">秦岭沿线</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7146/">长安广场</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7147/">郭杜</a></li><li><a class="" href="http://xa.meituan.com/meishi/b7148/">南大学城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15645/">紫薇田园都市</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15665/">韦曲西街</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15666/">韦曲南站</a></li><li><a class="" href="http://xa.meituan.com/meishi/b15668/">航天城</a></li></ul>"""
    insert_meituan_list("长安区", html_str)
    # ---------------------------------高陵区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4257/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b16938/">高陵县城</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25170/">车城花园</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25171/">马家湾</a></li></ul>"""
    insert_meituan_list("高陵区", html_str)
    # ---------------------------------阎良区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b7149/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25713/">前进路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25715/">千禧广场</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25717/">润天大道</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25719/">凤凰广场</a></li><li><a class="" href="http://xa.meituan.com/meishi/b25721/">蓝天广场</a></li></ul>"""
    insert_meituan_list("阎良区", html_str)
    # ---------------------------------蓝田县---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4254/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b23761/">蓝田县中心城区</a></li><li><a class="" href="http://xa.meituan.com/meishi/b23763/">汤峪镇</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42065/">向阳路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b46271/">北环路</a></li></ul>"""
    insert_meituan_list("蓝田县", html_str)
    # ---------------------------------周至县---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4255/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b22416/">周至县中心城区</a></li><li><a class="" href="http://xa.meituan.com/meishi/b42865/">沙河村</a></li><li><a class="" href="http://xa.meituan.com/meishi/b45403/">武商购物广场</a></li><li><a class="" href="http://xa.meituan.com/meishi/b45783/">周至汽车站</a></li></ul>"""
    insert_meituan_list("周至县", html_str)
    # ---------------------------------鄠邑区---------------------------------
    html_str = """<ul class="clear" data-reactid="157"><li><a class="" href="http://xa.meituan.com/meishi/b4256/">全部</a></li><li><a class="" href="http://xa.meituan.com/meishi/b22405/">娄敬路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b22408/">草堂路</a></li><li><a class="" href="http://xa.meituan.com/meishi/b26289/">余下镇</a></li><li><a class="" href="http://xa.meituan.com/meishi/b26295/">人民路</a></li></ul>"""
    insert_meituan_list("鄠邑区", html_str)


def insert_meituan_list(district_name, html_str):
    # 用正则表达式获取其中每个href中'/b'后的数字
    pattern = re.compile(r'href="http://xa.meituan.com/meishi/b(\d+)/">')
    # 用正则表达式匹配html字符串
    result_code = pattern.findall(html_str)
    # 再获取每个<a>标签中的文本
    pattern = re.compile(r'href="http://xa.meituan.com/meishi/b\d+/">(.+?)</a>')
    result_str = pattern.findall(html_str)
    for i in range(1, len(result_code)):
        # 如果result_str[i]中含'/'的要转成'_'
        if '/' in result_str[i]:
            result_str[i] = result_str[i].replace('/', '_')
        xian_meituan_list.append(MeituanPages(district_name, result_str[i], result_code[i]))


class CateItem:
    cate_name: string
    cate_id: string

    def __init__(self, cate_name, cate_id):
        self.cate_name = cate_name
        self.cate_id = cate_id


meituan_cate_list: list[CateItem] = [CateItem("代金券", "393"), CateItem("蛋糕甜点", "11"), CateItem("火锅", "17"),
                                     CateItem("自助餐", "40"), CateItem("小吃快餐", "36"), CateItem("日韩料理", "28"),
                                     CateItem("西餐", "35"), CateItem("聚餐宴请", "395"), CateItem("烧烤烤肉", "54"),
                                     CateItem("东北菜", "20003"), CateItem("川湘菜", "55"), CateItem("江浙菜", "56"),
                                     CateItem("香锅烤鱼", "20004"), CateItem("粤菜", "57"),
                                     CateItem("中式烧烤_烤串", "400"), CateItem("西北菜", "58"),
                                     CateItem("咖啡酒吧", "41"), CateItem("京菜卤菜", "59"), CateItem("云贵菜", "60"),
                                     CateItem("东南亚菜", "62"), CateItem("海鲜", "63"), CateItem("素食", "217"),
                                     CateItem("台湾_客家菜", "227"), CateItem("创意菜", "228"),
                                     CateItem("汤_粥_炖菜", "229"), CateItem("蒙餐", "232"), CateItem("新疆菜", "233"),
                                     CateItem("其他美食", "24")]

if __name__ == '__main__':
    init_meituan_list()
    for i in xian_meituan_list:
        print(i.district_name, i.business_district_name, i.code)
