import os
import random
import string
import time

import requests
from entities import xian_meituan_list, init_meituan_list, meituan_cate_list


def spider_crawl(page_num: string, cate_id: string, area_id: string):
    url = 'https://xa.meituan.com/meishi/api/poi/getPoiList'
    # (.*?):(.*)  ->  '$1': '$2'
    data = {
        'cityName': '西安',
        'cateId': cate_id,
        'areaId': area_id,
        'sort': '',
        'dinnerCountAttrId': '',
        'page': page_num,
        'userId': '876385598',
        'uuid': '5b2a99cda972439e896a.1673359739.1.0.0',
        'platform': '1',
        'partner': '126',
        'originUrl': 'https%3A%2F%2Fxa.meituan.com%2Fmeishi%2Fpn' + page_num + '%2F',
        'riskLevel': '1',
        'optimusCode': '10',
        '_token': 'eJxV0FuPojAYBuD/0lsbe4KWeqfArCdUWFTGyVwAKiIDymFV3Ox/30pmLzZp0qdv3+Rr+htUkz0YEIwlxhDcDhUYANLHfQ4gaGp1wwVjHOuMa4RAEP+f6UyDIKo2Fhh8EE1iSKT2+Uo8FXwQ1YAGV8k3Cf+EVFPr1ZmoCjg1zbUeIPQI+/khbX6FRT++5Ei5PqUophpS7wCqnvuvOiUUMmKojGKpRDoZkGGtkw6p5J3UFEN2YkqsE4ZUCCUipRLrZEDKZSehJF6jstcotYffe/Pv7Ki/UdU6TQqlw/S+Oa/JcmTa7ulgtCm5mgvkHqfp06ns1lnepeuev6xoNrROe/t8GJksqMd+Pk6c+Ljt3ebt0X5E5pIa49g5r901S4aVgd5NX/RQIQ20Hq1Kz9Z3j0j7uuU4W7rWnT9IsKI7Jx7FSR5kIzGPSuR4gnpGQt6uznCRsaHe1oL8KD0/01xrLtj6GpjHJlyE2iL1Cracsp/8dGue/qxH92Gr15dNwJF8br0tekbW7jIPErFqM/woMlzqJb7EuJ45uy8SXGj53KVvE+fdL+T9bFd38OcvKYWg1A=='
    }
    headers = {
        'Accept': 'application/json',
        'Accept-Encoding': 'gzip, deflate, br',
        'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Cookie': '_lxsdk_cuid=185800e34c5c8-08b84285c3d6f5-26021151-144000-185800e34c6c8; WEBDFPID=w0xz8v050899572uyzy3504z3w9v6w5w814199v570x979588uy8z0v2-1988250693759-1672890693004SEEAWICfd79fef3d01d5e9aadc18ccd4d0c95071372; ci=42; rvct=42; mtcdn=K; _hc.v=f9f8afb3-0904-1d77-2134-4f2c91221449.1672890846; iuuid=00786FB10D3457523E34EF26842EB1372AA68F83C0060CF56BA30C2AA072E60E; _lxsdk=00786FB10D3457523E34EF26842EB1372AA68F83C0060CF56BA30C2AA072E60E; wm_order_channel=default; request_source=openh5; _lx_utm=utm_source%3DBaidu%26utm_medium%3Dorganic; qruuid=0508abd4-308e-484e-b0e6-d2f5eeb51c56; client-id=d0d3a5df-0d0d-44d7-b031-0ff51d2651f0; uuid=5b2a99cda972439e896a.1673359739.1.0.0; __mta=216497705.1672890710892.1673341030878.1673359740975.21; userTicket=ksjcYKraOEAEaaedKNfJXOGTtujShXcHyvfVUJNg; _yoda_verify_resp=%2BwwEWTi8f3IITGHJJVCoMLUYYmRAUiA2FPOl0fvpPcDVi3BrQ0mTCUYR%2FOQZIAL9ygVvk9aaiHw%2FRyJORsYH4T4K67exoWMqw63swRIqStIwKbVWgtR4amd%2Fy2qkxakuqWtM%2B2PEsqVNvwBzlzh%2FJvG%2B11DBYLYpa%2FiNTIr3jhjRQ0pk%2FBYHVKw1CE9AhQ2mnxf7wHkmrILBv0Xqm77OKd2%2BOPdHfVEAN2LoSOPzVEIoZooU3TCmqKA15p%2Fn0uyVjf1CHiRObIHm%2F%2F7EqQ%2BhxTRzJGbCsFDwBxh9QmVtWPiiHUszhFe%2BoqFzqrMs18nwiWQCmdi5dA4fNMwMHljfkrgRYxC3KVQN7yDF841hsoBmA2viK0SQkI%2F5lw%2FKjKLn; _yoda_verify_rid=1661ddaef8c17063; u=876385598; n=%E5%AF%82%E7%8E%84%E6%A5%9A; lt=AgFnI_k-2zkORQxU25_1SSS3YqkryATsB3sh3-hII0_DdPM3ZKyDhOuA07grVziKdpFhnP8Kfu4ysQAAAADnFQAAx0AoPMiPoE0X8NQW8ZrBJInI2gxEArjIqmvDHPEW9TR9RA0kgPnTPeADqplkVZ6k; mt_c_token=AgFnI_k-2zkORQxU25_1SSS3YqkryATsB3sh3-hII0_DdPM3ZKyDhOuA07grVziKdpFhnP8Kfu4ysQAAAADnFQAAx0AoPMiPoE0X8NQW8ZrBJInI2gxEArjIqmvDHPEW9TR9RA0kgPnTPeADqplkVZ6k; token=AgFnI_k-2zkORQxU25_1SSS3YqkryATsB3sh3-hII0_DdPM3ZKyDhOuA07grVziKdpFhnP8Kfu4ysQAAAADnFQAAx0AoPMiPoE0X8NQW8ZrBJInI2gxEArjIqmvDHPEW9TR9RA0kgPnTPeADqplkVZ6k; token2=AgFnI_k-2zkORQxU25_1SSS3YqkryATsB3sh3-hII0_DdPM3ZKyDhOuA07grVziKdpFhnP8Kfu4ysQAAAADnFQAAx0AoPMiPoE0X8NQW8ZrBJInI2gxEArjIqmvDHPEW9TR9RA0kgPnTPeADqplkVZ6k; _lxsdk_s=1859c034e1e-e57-504-83c%7C%7C92',
        'Host': 'xa.meituan.com',
        'Pragma': 'no-cache',
        'Referer': 'https://xa.meituan.com/meishi/c229/',
        'sec-ch-ua': '"Not?A_Brand";v="8", "Chromium";v="108", "Microsoft Edge";v="108"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36'
    }
    response = requests.get(url, params=data, headers=headers)
    # 如果404了
    if response.status_code == 404:
        return "404"
    return response.text


# 将返回的字符串写入到文件
def write_to_file(content: string, prefix: string, filename: string):
    try:
        with open(prefix + filename + '.txt', 'a', encoding='utf-8') as f:
            f.write(content)
    finally:
        f.close()


# 按照区划爬取
def get_district_list():
    # # write_to_file(spider_crawl('3', '113'), '')
    # page: string = '3'
    # position: string = '113'
    # write_to_file(spider_crawl(page, position), 'meituan_' + position + '_' + page)
    init_meituan_list()
    for district in xian_meituan_list:
        if not os.path.exists('./txtFiles'):
            os.mkdir('./txtFiles')
        if not os.path.exists('./txtFiles/' + district.district_name):
            os.mkdir('./txtFiles/' + district.district_name)
        if not os.path.exists('./txtFiles/' + district.district_name + '/' + district.business_district_name):
            os.mkdir('./txtFiles/' + district.district_name + '/' + district.business_district_name)
        for page in range(1, 60):
            # 打印日志
            print('正在爬取' + district.district_name + ' ' +
                  district.business_district_name + ' 第' + str(page) + '页' '的数据')
            result = spider_crawl(str(page), '0', district.code)
            if result == "404":
                break
            # 如果result包含"poiInfos":[]，说明没有数据了 也直接break
            if result.find('"poiInfos":[]') != -1:
                print(district.business_district_name + '的数据爬取完毕')
                break
            write_to_file(result,
                          './txtFiles/' + district.district_name + '/' + district.business_district_name + '/',
                          'meituan_' + district.business_district_name + '_' + str(page))
            # 随机休眠3-5秒
            time.sleep(random.randint(1, 3))


def get_cate_list():
    for cateItem in meituan_cate_list:
        if not os.path.exists('./txtFiles/cate'):
            os.mkdir('./txtFiles/cate')
        if not os.path.exists('./txtFiles/cate/' + cateItem.cate_name):
            os.mkdir('./txtFiles/cate/' + cateItem.cate_name)
        for page in range(1, 60):
            # 打印日志
            print('正在爬取' + cateItem.cate_name + ' ' + ' 第' + str(page) + '页' '的数据')
            result = spider_crawl(str(page), cateItem.cate_id, '0')
            if result == "404":
                break
            # 如果result包含"poiInfos":[]，说明没有数据了 也直接break
            if result.find('"poiInfos":[]') != -1:
                print(cateItem.cate_name + '的数据爬取完毕')
                break
            write_to_file(result,
                          './txtFiles/cate/' + cateItem.cate_name + '/',
                          'meituan_' + cateItem.cate_name + '_' + str(page))
            # 随机休眠1-3秒
            time.sleep(random.randint(1, 3))


if __name__ == '__main__':
    # get_district_list()
    get_cate_list()
