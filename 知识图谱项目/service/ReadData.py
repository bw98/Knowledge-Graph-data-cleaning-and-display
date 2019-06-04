import re
import random
import re

def readLineByFile(file_name):
    lines = []
    with open(file_name, 'r', encoding='utf-8') as f:
        while True:
            line = f.readline()
            if not line:
                break
            lines.append(line)
    return lines


def hbaseDataProcessing(org_profile_lines: list, person_lines: list, paper_lines: list):
    # 利用三个字典列表存储三个文件中的数据
    org_list = []
    person_list = []
    paper_list = []

    # 处理 org_profile 文件中的每一行数据
    for line in org_profile_lines:
        org_dict = {}
        sub_dict = {}
        parts = line.split('\t')
        row_key_name = ''
        if parts[0] == 'rowkey':
            row_key_name = parts[1]
        key_name = ''  # 记录sub_dict字典的键名
        for i, val in enumerate(parts[2:]):
            if val == '\n':
                break
            if (i % 2) == 0:  # 取得cell的"列族名+限定词"的拼接字符串
                # 如果限定词没用，就用列族名存储
                family_qualifier_tuple = val.split(":")
                if family_qualifier_tuple[1].isdigit():  # 纯数字的话，限定词就没有用了，需要存列族名
                    key_name = family_qualifier_tuple[0] + '\t' + str(hash(random.random()))  # 防止数据被字典键去重，可以在存入数据时去掉随机数
                else:
                    key_name = family_qualifier_tuple[1]

            if (i % 2) == 1:  # 取得hbase中cell的value
                if ('null' in val) or ('NULL' in val) or (len(val)==0):
                    sub_dict[key_name] = 'null'
                sub_dict[key_name] = val

        org_dict[row_key_name] = sub_dict
        org_list.append(org_dict)

    # 处理 person_file 文件中的每一行数据
    for line in person_lines:
        person_dict = {}
        sub_dict = {}
        parts = line.split('\t')
        row_key_name = ''
        if parts[0] == 'rowkey':
            row_key_name = parts[1]
            person_org_tuple = parts[1].split('->')
            sub_dict['nameCN'] = person_org_tuple[0]
            sub_dict['organization'] = re.split('[;]', person_org_tuple[1])
        key_name = ""  # 记录sub_dict字典的键名
        for i, val in enumerate(parts[2:]):
            if val == '\n':
                break
            if (i % 2) == 0:
                # 如果限定词没用，就用列族名存储
                family_qualifier_tuple = val.split(':')
                if family_qualifier_tuple[1].isdigit():  # 纯数字的话，限定词就没有用了，需要存列族名
                    key_name = family_qualifier_tuple[0] + '\t' + str(hash(random.random()))  # 防止数据被字典键去重，可以在存入数据时去掉随机数
                else:
                    key_name = family_qualifier_tuple[1]

            if (i % 2) == 1:  # 取得hbase中cell的value
                if key_name == 'orgMessage':  # orgMessage 比较特殊，需要再提取
                    # 作者组织为：中国地质大学;江苏省地质矿产局
                    # orgMessage 对应的value为：null;贵州遵义,563003
                    orgAddress_list = []
                    orgPostcode_list = []

                    val_parts = re.split('[,;]', val)
                    val_segments = val.split(';')
                    if 2*len(sub_dict['organization']) != len(val_parts):
                        for address_postcode_tuple in val_segments:
                            if address_postcode_tuple == 'null':
                                orgAddress_list.append('null')
                                orgPostcode_list.append('null')
                            else:
                                address_postcode_tuple = address_postcode_tuple.split(',')
                                orgAddress_list.append(address_postcode_tuple[0])
                                orgPostcode_list.append(address_postcode_tuple[1])
                    else:
                        for idx, value in enumerate(val_parts):
                                if 'null' in value:
                                    if (idx % 2) == 0:
                                        orgAddress_list.append('null')
                                    if (idx %2) == 1:
                                        orgPostcode_list.append('null')
                                else:
                                    if (idx % 2) == 0:
                                        orgAddress_list.append(value)
                                    if (idx %2) == 1:
                                        orgPostcode_list.append(value)

                    sub_dict['orgAddress'] = orgAddress_list
                    sub_dict['orgPostcode'] = orgPostcode_list

                else:
                    sub_dict[key_name] = val

        person_dict[row_key_name] = sub_dict
        person_list.append(person_dict)

    # 处理 test2_lines 文件中的每一行数据
    for line in paper_lines:
        paper_dict = {}
        sub_dict = {}
        parts = line.split('\t')
        row_key_name = ''
        if parts[0] == 'rowkey':
            row_key_name = parts[1]
        key_name = ''  # 记录sub_dict字典的键名
        author_dict = {}  # 存入一个person，包含中文人名，英文人名，作者顺序，归属组织, 组织所在的地址，组织所在地址的邮编
        for i, val in enumerate(parts[2:]):
            if val == '\n':
                break

            if (i % 2) == 0:  # 取得cell的"列族名+限定词"的拼接字符串
                # 如果限定词没用，就用列族名存储
                family_qualifier_tuple = val.split(':')
                if family_qualifier_tuple[1].isdigit():  # 纯数字的话，限定词就没有用了，需要存列族名
                    key_name = family_qualifier_tuple[0] + '\t' + str(hash(random.random()))  # 防止数据被字典键去重，可以在存入数据时去掉随机数
                elif family_qualifier_tuple[0] == 'authors':
                    part_authors_qualifier = family_qualifier_tuple[1].split('_', 1)
                    author_dict['nameCN'] = part_authors_qualifier[0]
                    part_authors_qualifier = part_authors_qualifier[1].split(',')
                    author_dict['nameEN'] = part_authors_qualifier[0]
                    author_dict['authorRank'] = part_authors_qualifier[1]
                    key_name = 'authors\t' + str(author_dict['authorRank'])  # 提取authors列族的信息时需要区分作者顺序

                else:
                    key_name = family_qualifier_tuple[1]

            if (i % 2) == 1:  # 取得hbase中cell的value
                if len(val) == 0:
                    sub_dict[key_name] = 'null'
                elif 'authors' in key_name:
                    # 键：authors
                    # 值：贵州大学,null;苏州大学,苏州,null;遵义医学院,贵州遵义,563003
                    orgname_list = []
                    orgAddress_list = []
                    orgPostcode_list = []
                    val_parts = re.split('[,;]', val)
                    val_segments = val.split(';')
                    if 3 * len(val_segments) != len(val_parts): # 值中含有null的情况
                        for orgname_address_postcode_triple in val_segments:
                            triple = orgname_address_postcode_triple.split(',')
                            if len(triple) == 0:
                                orgname_list.append('null')
                                orgAddress_list.append('null')
                                orgPostcode_list.append('null')
                            if len(triple) == 1:
                                orgname_list.append(triple[0])
                                orgAddress_list.append('null')
                                orgPostcode_list.append('null')
                            if len(triple) == 2:
                                orgname_list.append(triple[0])
                                orgAddress_list.append(triple[1])
                                orgPostcode_list.append('null')
                            if len(triple) == 3:
                                orgname_list.append(triple[0])
                                orgAddress_list.append(triple[1])
                                orgPostcode_list.append(triple[2])
                    else:
                        for idx, value in enumerate(val_parts):
                            if 'null' in value:
                                if (idx % 3) == 0:
                                    orgname_list.append('null')
                                if (idx % 3) == 1:
                                    orgAddress_list.append('null')
                                if (idx % 3) == 2:
                                    orgPostcode_list.append('null')
                            else:
                                if (idx % 3) == 0:
                                    orgname_list.append(value)
                                if (idx % 3) == 1:
                                    orgAddress_list.append(value)
                                if (idx % 3) == 2:
                                    orgPostcode_list.append(value)

                    # sub_dict['orgAddress'] = orgAddress_list
                    # sub_dict['orgPostcode'] = orgPostcode_list

                    # part_val = re.split('[,;]', val)
                    # if len(part_val) == 2:
                    #     author_dict['organization' + '\t' + str(hash(random.random()))] = part_val[0]
                    #     author_dict['orgAdress' + '\t' + str(hash(random.random()))] = part_val[1]
                    #     author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = 'null'
                    # elif len(part_val) >= 3:
                    #     for idx in range(len(part_val)):
                    #         if idx % 3 == 0:
                    #             author_dict['organization' + '\t' + str(hash(random.random()))] = part_val[idx]
                    #         if idx % 3 == 1:
                    #             author_dict['orgAddress' + '\t' + str(hash(random.random()))] = part_val[idx]
                    #         if idx % 3 == 2:
                    #             author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = part_val[idx]
                    #     # author字典补齐
                    #     if len(part_val) % 3 == 1:
                    #         author_dict['orgAddress' + '\t' + str(hash(random.random()))] = 'null'
                    #         author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = 'null'
                    #     if len(part_val) % 3 == 2:
                    #         author_dict['orgPostcode' + '\t' + str(hash(random.random()))] = 'null'
                    #
                    # else:  # 处理author列族的限定词中没有信息或其他情况
                    #     author_dict['organization'] = 'null'
                    #     author_dict['orgAdress'] = 'null'
                    #     author_dict['orgPostcode'] = 'null'
                    author_dict['organization'] = orgname_list
                    author_dict['orgAddress'] = orgAddress_list
                    author_dict['orgPostcode'] = orgPostcode_list
                    sub_dict[key_name] = author_dict
                    author_dict = {}

                elif 'indexs' in key_name:
                    indexs_list = val.split(',')
                    for idx_name in indexs_list:
                        sub_dict['indexs\t' + str(hash(random.random()))] = idx_name

                else:
                    sub_dict[key_name] = val

        paper_dict[row_key_name] = sub_dict
        paper_list.append(paper_dict)

    return org_list, person_list, paper_list
