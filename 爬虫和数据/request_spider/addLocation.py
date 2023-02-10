import json
import os

import MySQLdb


def initDB():
    # 存入数据库
    global db, cursor
    db = MySQLdb.connect("localhost", "root", "123456", "xian_meituan", charset="utf8")
    cursor = db.cursor()


def getName():
    message = str("SELECT poiId,business_district from xian_meituan where district is null")
    cursor.execute(message)
    result = cursor.fetchall()
    return result


def mkdic():
    dic = {}
    for qu in os.listdir(r'D:\assignment\3_1\实习\str\here\区划'):
        # print(qu)
        path = str(r'D:\assignment\3_1\实习\str\here\区划')
        dis = os.listdir(path + "\\" + qu)
        for disname in dis:
            dic[disname] = qu
    return dic


if __name__ == '__main__':
    # initDB()
    dic = mkdic()
    print(dic['钟楼'])
    # busis = getName()
    # for busi in busis:
    #     try:
    #         message = "update xian_meituan set district = %s where poiId=%s"
    #         data = dic[busi[1]], busi[0]
    #         cursor.execute(message, data)
    #         db.commit()
    #     finally:
    #         continue
