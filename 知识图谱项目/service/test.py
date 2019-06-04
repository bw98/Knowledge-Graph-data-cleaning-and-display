import re
import random


val = '成都理工大学,四川成都,610041;中国科学院水利部成都山地灾害与环境研究所,四川成都,610041;测试大学'
l = re.split('[,;]', val)
author_dict = {}
for idx in range(len(l)):
    if idx % 3 == 0:
        author_dict['org'+'\t' + str(hash(random.random()))] = l[idx]
    if idx % 3 == 1:
        author_dict['orgAddress' + '\t' + str(hash(random.random()))] = l[idx]
    if idx % 3 == 2:
        author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = l[idx]
#author字典补齐
if len(l) % 3 == 1:
    author_dict['orgAddress' + '\t' + str(hash(random.random()))] = 'null'
    author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = 'null'
if len(l) % 3 == 2:
    author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = 'null'
debug = 1


