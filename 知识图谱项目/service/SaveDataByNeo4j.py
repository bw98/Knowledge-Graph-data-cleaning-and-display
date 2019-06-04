from Dao import Neo4jDao
import time


def saveOrg(dao: Neo4jDao.Neo4jDao, org_list: list):
    # 将org_list存入neo4j
    deal_org_list_start_time = time.time()
    org_count = 0
    for sub_dict in org_list:
        org_count += 1
        print('org_count: {}'.format(org_count))
        for key in sub_dict.keys():
            if not (dao.findOneNode(node_type='organization', properties={'name': key}) is None):
                print('组织({})相关信息已经存入数据库，不加重复存入，跳过'.format(key))
                break
            org_node = dao.createNode(label='organization', properties={'name': key})
            for k, v in sub_dict.get(key).items():
                if 'member\t' in k:  # 映射列族的每一个member到数据库neo4j中
                    person_node = dao.createNode(label='person', properties={'name': v})
                    dao.createRelationship(start_node=org_node, end_node=person_node,
                                           relation_type='organization')
                    dao.createRelationship(start_node=person_node, end_node=org_node, relation_type='organization')

                else:
                    target_node = dao.findOneNode(node_type=k, properties={'name': v})
                    # print('k:{} v:{} target_node:{}'.format(k, v, target_node))
                    if target_node is None:
                        target_node = dao.createNode(label=k, properties={'name': v})
                    dao.createRelationship(start_node=org_node, end_node=target_node,
                                           relation_type=k)
                    dao.createRelationship(start_node=target_node, end_node=org_node,
                                           relation_type=k)

    deal_org_list_end_time = time.time()
    print('处理org_list并存入数据库总计时：{}'.format(deal_org_list_end_time - deal_org_list_start_time))


def savePerson(dao: Neo4jDao.Neo4jDao, person_list: list):
    # 将person_list存入neo4j
    deal_person_list_start_time = time.time()
    person_count = 0
    for sub_dict in person_list:
        person_count += 1
        print('person_count={}'.format(person_count))
        person_node = None
        for key in sub_dict.keys():
            # 提前处理组织、组织地址、组织邮编，否则遍历字典然后再处理它们的效率会偏低
            organizations = sub_dict.get(key).get('organization')
            orgAddresses = sub_dict.get(key).get('orgAddress')
            orgPostcodes = sub_dict.get(key).get('orgPostcode')
            for this_idx, this_organization_name in enumerate(organizations):
                org_node = dao.findOneNode(node_type='organization', properties={'name': this_organization_name})
                if org_node is None:
                    org_node = dao.createNode(label='organization', properties={'name': this_organization_name})

                address_node = dao.findOneNode(node_type='address', properties={'name': orgAddresses[this_idx]})
                if address_node is None:
                    address_node = dao.createNode(label='address', properties={'name': orgAddresses[this_idx]})
                postcode_node = dao.findOneNode(node_type='postcode', properties={'name': orgPostcodes[this_idx]})
                if postcode_node is None:
                    postcode_node = dao.createNode(label='postcode', properties={'name': orgPostcodes[this_idx]})

                dao.createRelationship(start_node=org_node, end_node=address_node,
                                       relation_type='address')
                dao.createRelationship(start_node=address_node, end_node=org_node,
                                       relation_type='address')
                dao.createRelationship(start_node=org_node, end_node=postcode_node,
                                       relation_type='postcode')
                dao.createRelationship(start_node=postcode_node, end_node=org_node,
                                       relation_type='postcode')

            for k, v in sub_dict.get(key).items():
                if k == 'nameCN':
                    organization_name = sub_dict.get(key).get('organization')  # 获得一个person所在所有组织的列表

                    person_node = dao.findAllNode(node_type='person', properties={'name': v})
                    if person_node is None:
                        person_node = dao.createNode(label='person', properties={'name': v})
                    else:
                        # 判断人名和其所在的所有组织名是否都一样，如果都一样说明当前数据重复了，标志is_dup_flag为True
                        is_dup_flag = False
                        for one_person_node in person_node:
                            relationships = dao.findAllRelationship(nodes=[one_person_node], r_type='organization')
                            if len(relationships) != len(organization_name):  # 两个组织个数都不一样的人不可能是同一个人
                                continue  # 继续匹配下一个人是否是同一个人
                            else:
                                matched_num = 0
                                for relationship in relationships:
                                    for one_organization_name in organization_name:
                                        if one_organization_name in str(relationship):
                                            matched_num += 1
                                if matched_num == len(organization_name):  # 组织个数一样且组织也一样，即找到了同一个人，不用再找了
                                    is_dup_flag = True
                                    break

                        if is_dup_flag:
                            break  # 跳到下一个 person 记录
                        else:
                            person_node = dao.createNode(label='person', properties={'name': v})


                elif 'paper' in k:
                    paper_node = dao.findOneNode(node_type='paper', properties={'name': v})
                    if paper_node is None:
                        paper_node = dao.createNode(label='paper', properties={'name': v})
                    dao.createRelationship(start_node=person_node, end_node=paper_node, relation_type='author')
                    dao.createRelationship(start_node=paper_node, end_node=person_node, relation_type='author')

                elif k == 'organization':
                    # 把作者和其所在的所有组织建立关系
                    for organization_name in organizations:
                        target_node = dao.findOneNode(node_type=k, properties={'name': organization_name})
                        if target_node is None:
                            target_node = dao.createNode(label=k, properties={'name': organization_name})
                        dao.createRelationship(start_node=person_node, end_node=target_node,
                                               relation_type=k)
                        dao.createRelationship(start_node=target_node, end_node=person_node,
                                               relation_type=k)

                elif k == 'orgAddress':
                    continue  # 已提前与其组织建立关系
                    # address_node = dao.findOneNode(node_type='address', properties={'name': v})
                    # if address_node is None:
                    #     address_node = dao.createNode(label='address', properties={'name': v})
                    # dao.createRelationship(start_node=person_node, end_node=address_node, relation_type='address')
                    # dao.createRelationship(start_node=address_node, end_node=person_node, relation_type='address')

                elif k == 'orgPostcode':
                    continue  # 已提前与其组织建立关系
                    # postcode_node = dao.findOneNode(node_type='postcode', properties={'name': v})
                    # if postcode_node is None:
                    #     postcode_node = dao.createNode(label='postcode', properties={'name': v})
                    # dao.createRelationship(start_node=person_node, end_node=postcode_node, relation_type='postcode')
                    # dao.createRelationship(start_node=postcode_node, end_node=person_node, relation_type='postcode')

                else:
                    target_node = dao.findOneNode(node_type=k, properties={'name': v})
                    # print('k:{} v:{} target_node:{}'.format(k, v, target_node))
                    if target_node is None:
                        target_node = dao.createNode(label=k, properties={'name': v})
                    dao.createRelationship(start_node=person_node, end_node=target_node,
                                           relation_type=k)
                    dao.createRelationship(start_node=target_node, end_node=person_node,
                                           relation_type=k)

    deal_person_list_end_time = time.time()
    print('处理person_list并存入数据库总计时：{}'.format(deal_person_list_end_time - deal_person_list_start_time))

def savePaper(dao: Neo4jDao.Neo4jDao, paper_list: list):
    #将paper_list存入neo4j
    deal_paper_list_start_time = time.time()
    paper_cnt = 0
    for sub_dict in paper_list:
        paper_cnt += 1
        print('paper_cnt = {}'.format(paper_cnt))
        for key in sub_dict.keys():
            #print('(论文标题) key={}'.format(key))
            if not (dao.findOneNode(node_type='paper', properties={'name': key}) is None):  # 先查看这个论文之前有没有处理过
                print('论文({})相关信息已经存入数据库，不加重复存入，跳过'.format(key))
                break
            paper_node = dao.createNode(label='paper', properties={'name': key})

            for k, v in sub_dict.get(key).items():

                if 'authors' in k:
                    # 首选根据人名和组织名在图中查看该person是否重复存在，如果重复的话，说明不用再创建person结点，
                    # 然后再查看和该论文的关系是否存在，如果它也存在的话，就只要在author关系中添加authorRank的属性
                    author_dict = v
                    author_name = author_dict['nameCN']
                    organizations = author_dict['organization']

                    person_node = dao.findAllNode(node_type='person', properties={'name': author_name})
                    is_dup_flag = False

                    # 判断是否查询到的人是否是同一个人
                    if person_node is not None:
                        # 判断人名和其所在的所有组织名是否都一样，如果都一样说明当前数据重复了，标志is_dup_flag为True
                        for one_person_node in person_node:
                            relationships = dao.findAllRelationship(nodes=[one_person_node], r_type='organization')
                            if len(relationships) != len(organizations):  # 两个组织个数都不一样的人不可能是同一个人
                                continue  # 继续匹配下一个人是否是同一个人
                            else:
                                matched_num = 0
                                for relationship in relationships:
                                    for one_organization_name in organizations:
                                        if one_organization_name in str(relationship):
                                            matched_num += 1

                                if matched_num == len(organizations):  # 组织个数一样且组织也一样，即找到了同一个人，不用再找了
                                    is_dup_flag = True
                                    person_node = one_person_node
                                    break

                    # 处理完person结点是否重复的问题后，分两种情况：
                    # 1、对于不重复的person结点,需要添加一系列关系，比如组织、英文名等等，最后添加person与paper的authorRank关系
                    # 1、对于重复的person结点,只需要添加person与paper的authorRank关系
                    if not is_dup_flag:  # 对于不是重复的person结点，需要创建结点并需要重新添加一系列关系
                        person_node = dao.createNode(label='person', properties={'name': author_name})
                        for author_dict_key, author_dict_val in author_dict.items():
                            if 'nameCN' in author_dict_key:
                                continue
                            if 'authorRank' in author_dict_key:
                                dao.createRelationship(start_node=paper_node, end_node=person_node, relation_type='authorRank', relation_properties={'authorRank':author_dict_val})
                                dao.createRelationship(start_node=paper_node, end_node=person_node, relation_type='authorRank', relation_properties={'authorRank':author_dict_val})
                            elif 'organization' in author_dict_key:
                                # 将person和organization关联到一起
                                orgAddresses = author_dict['orgAddress']
                                orgPostcodes = author_dict['orgPostcode']
                                for this_idx, this_organization_name in enumerate(organizations):
                                    org_node = dao.findOneNode(node_type='organization',
                                                               properties={'name': this_organization_name})
                                    if org_node is None:
                                        org_node = dao.createNode(label='organization',
                                                                  properties={'name': this_organization_name})
                                    address_node = dao.findOneNode(node_type='address',
                                                                   properties={'name': orgAddresses[this_idx]})
                                    if address_node is None:
                                        address_node = dao.createNode(label='address',
                                                                      properties={'name': orgAddresses[this_idx]})
                                    postcode_node = dao.findOneNode(node_type='postcode',
                                                                    properties={'name': orgPostcodes[this_idx]})
                                    if postcode_node is None:
                                        postcode_node = dao.createNode(label='postcode',
                                                                       properties={'name': orgPostcodes[this_idx]})

                                    dao.createRelationship(start_node=org_node, end_node=address_node,
                                                           relation_type='address')
                                    dao.createRelationship(start_node=address_node, end_node=org_node,
                                                           relation_type='address')
                                    dao.createRelationship(start_node=org_node, end_node=postcode_node,
                                                           relation_type='postcode')
                                    dao.createRelationship(start_node=postcode_node, end_node=org_node,
                                                           relation_type='postcode')

                                    dao.createRelationship(start_node=person_node, end_node=org_node, relation_type='organization')
                                    dao.createRelationship(start_node=org_node, end_node=person_node, relation_type='organization')

                            elif 'orgAddress' in author_dict_key:
                                continue
                            elif 'orgPostcode' in author_dict_key:
                                continue
                            else:
                                target_node = dao.findOneNode(node_type=author_dict_key, properties={'name': author_dict_val})
                                if target_node is None:
                                    target_node = dao.createNode(label=author_dict_key, properties={'name': author_dict_val})
                                dao.createRelationship(start_node=person_node, end_node=target_node,
                                                       relation_type=author_dict_key)
                                dao.createRelationship(start_node=target_node, end_node=person_node,
                                                       relation_type=author_dict_key)

                    else: # 对于重复的person结点，只需要添加一条它和paper结点的authorRank关系
                        dao.createRelationship(start_node=paper_node, end_node=person_node, relation_type='authorRank',
                                               relation_properties={'authorRank': author_dict['authorRank']})
                        dao.createRelationship(start_node=paper_node, end_node=person_node, relation_type='authorRank',
                                               relation_properties={'authorRank': author_dict['authorRank']})

                elif 'fundsproject' in k:
                    fund_project_list = v.split('，')
                    for fund_project in fund_project_list:
                        target_node = dao.findOneNode(node_type='fundproject', properties={'name': fund_project})
                        if target_node is None:
                            target_node = dao.createNode(label='fundproject', properties={'name': fund_project})
                        dao.createRelationship(start_node=paper_node, end_node=target_node,
                                               relation_type=k)
                        dao.createRelationship(start_node=target_node, end_node=paper_node,
                                               relation_type=k)

                elif 'indexs' in k:
                    index_node = dao.findOneNode(node_type='index', properties={'name': v})
                    if index_node is None:
                        index_node = dao.createNode(label='index', properties={'name': v})
                    dao.createRelationship(start_node=paper_node, end_node=index_node, relation_type='postcode')
                    dao.createRelationship(start_node=index_node, end_node=paper_node, relation_type='postcode')

                else:
                    target_node = dao.findOneNode(node_type=k, properties={'name': v})
                    #print('k:{} v:{} target_node:{}'.format(k, v, target_node))
                    if target_node is None:
                        target_node = dao.createNode(label=k, properties={'name': v})
                    dao.createRelationship(start_node=paper_node, end_node=target_node,
                                           relation_type=k)
                    dao.createRelationship(start_node=target_node, end_node=paper_node,
                                           relation_type=k)

    deal_paper_list_end_time = time.time()
    print('处理paper_list并存入数据库总计时：{}'.format(deal_paper_list_end_time - deal_paper_list_start_time))
