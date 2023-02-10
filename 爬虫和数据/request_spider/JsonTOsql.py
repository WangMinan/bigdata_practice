import json
import os
import MySQLdb
import requests


def get_files():
    files = []
    for filepath, dirnames, filenames in os.walk(r'D:\assignment\3_1\实习\str\here'):
        for filename in filenames:
            files.append(os.path.join(filepath, filename))
    return files


def initDB():
    # 存入数据库
    global db
    db = MySQLdb.connect("localhost", "root", "123456", "xian_meituan", charset="utf8")
    # cursor = db.cursor()
    # # 数据库中如果已经有xian_meituan此表，则删除已存在的此表
    # cursor.execute("DROP TABLE IF EXISTS xian_meituan")
    # # 创建新表xian_meituan
    # sql = r"""CREATE TABLE xian_meituan (
    #                     poiId      VARCHAR(100) NOT NULL PRIMARY key,
    #                     frontImg   VARCHAR(100) NOT NULL,
    #                     title      VARCHAR(100) NOT NULL,
    #                     category   VARCHAR(100),
    #                     district   VARCHAR(100),
    #                     business_district VARCHAR(100),
    #                     avgScore   FLOAT NOT NULL,
    #                     allCommentNum INT NOT NULL,
    #                     address    VARCHAR(100) NOT NULL,
    #                     avgPrice   INT NOT NULL,
    #                     longitude  double not null,
    #                     latitude   double not null
    #                     )"""
    # cursor.execute(sql)


def updateloc():
    cursor = db.cursor()
    cnt = 15916
    message = str("SELECT * from xian_meituan limit 15916,17038")
    cursor.execute(message)
    shops = cursor.fetchall()
    for shop in shops:
        name = "西安市" + str(shop[4]) + str(shop[7]) + (shop[2])
        loc = str(getLoc(name))
        log = loc.split(',')[0]
        lat = loc.split(',')[1]
        cursor = db.cursor()
        sql = ("update xian_meituan set longitude = %s,latitude = %s where poiId = %s")
        data = log, lat, shop[0]
        cursor.execute(sql, data)
        db.commit()
        print(name)
        cnt = cnt + 1
        print(cnt)


def getLoc(name):
    # 输入API问号前固定不变的部分
    url = 'https://restapi.amap.com/v3/geocode/geo'

    # 将两个参数放入字典
    params = {'key': 'af426831c4955879edf1b8f8640726a1',
              'address': name}
    res = requests.get(url, params)
    jd = json.loads(res.text)
    # print(jd['geocodes'][0]['location'])
    return jd['geocodes'][0]['location']


def insertCateToDB(jsondata, cate):
    global db, cnt, all
    cursor = db.cursor()
    for i in jsondata:
        insert_message = ("INSERT INTO xian_meituan(poiId,frontImg,title,category,"
                          "district,business_district,avgScore,"
                          "allCommentNum,address,avgPrice) "
                          "value(%s,%s,%s,%s,null,null,%s,%s,%s,%s)"
                          )
        data = i['poiId'], i['frontImg'], i['title'], cate, round(i['avgScore'], 1), \
               int(i['allCommentNum']), i['address'], int(i['avgPrice'])
        # 数据插入数据库
        try:
            cursor.execute(insert_message, data)
            db.commit()
        except MySQLdb._exceptions.IntegrityError as e:
            print(e)
            print(data)
            all = all + 1
            try:
                message = str("SELECT category from xian_meituan where poiId = ") + str(i['poiId'])
                datatemp = i['poiId']
                cursor.execute(message)
                temp = cursor.fetchall()[0][0]
                if temp == cate:
                    cnt + 1
                else:
                    a = temp + "," + cate, i['poiId']
                    message = ("update xian_meituan set category=%s where poiId=%s")
                    cursor.execute(message, a)
                    db.commit()
            except Exception as e:
                print(e)
        finally:
            continue


def insertDisToDB(jsondata, dis, bus_dis):
    InsertData = []
    global db
    cursor = db.cursor()
    for i in jsondata:
        insert_message = ("INSERT INTO xian_meituan(poiId,frontImg,title,category,"
                          "district,business_district,avgScore,"
                          "allCommentNum,address,avgPrice) "
                          "value (%s,%s,%s,null,%s,%s,%s,%s,%s,%s) "
                          "ON DUPLICATE KEY UPDATE "
                          "district=%s,business_district=%s")
        data = i['poiId'], i['frontImg'], i['title'], dis, bus_dis, round(i['avgScore'], 1), \
               int(i['allCommentNum']), i['address'], int(i['avgPrice']), dis, bus_dis
        # print(data)
        InsertData.append(data)
        # 数据插入数据库
        cursor.execute(insert_message, data)
        db.commit()


if __name__ == '__main__':
    initDB()
    updateloc()

    # files = get_files()
    # initDB()
    # cnt = 0
    # all = 0
    # for file in files:
    #     with open(file, encoding="utf-8") as f:  # 打开文件
    #         # print(file)
    #         originJson = json.loads(f.read())
    #         JsonData = originJson['data']['poiInfos']
    #         if file.split('\\')[6] == "cate":  # cate包
    #             cate = file.split('\\')[7]  # 种类 如东北菜
    #             insertCateToDB(JsonData, cate)
    #         else:
    #             dis = file.split('\\')[7]  # 区划包 如长安区
    #             bus_dis = file.split('\\')[8]  # 街划包 如东大街
    #             insertDisToDB(JsonData, dis, bus_dis)
    # print(cnt)
