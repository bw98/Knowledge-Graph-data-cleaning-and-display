from py2neo import Graph, Node, Relationship, NodeMatcher


class Neo4jDao:

    def __init__(self, username='neo4j', password='123456789'):
        self.username = username
        self.password = password
        self.my_graph = self.connectNeo4j(username=self.username, password=self.password)

    @staticmethod
    def connectNeo4j(username: str, password: str):
        my_graph = Graph(
            "http://localhost:7474",
            username=username,
            password=password
        )
        return my_graph

    def createNode(self, label: str, properties: dict):
        """创建结点，如果结点有类型和属性的话，也一起创建

        :param label: 结点的类型
        :param properties: 多个属性键值对组成的字典，用于初始化结点的属性
        :return:创建好的结点，类型为Node
        """
        node = Node(label, **properties)
        self.my_graph.create(node)
        return node

    def createRelationship(self, start_node: Node, relation_type: str, end_node: Node, relation_properties=None):
        """创建关系，如果有关系上属性的话就一起创建

        :param start_node: 起始结点
        :param relation_type: 关系类型
        :param end_node: 结束结点
        :param relation_properties: 属性字典，如果有传入的话，则在关系上添加多个形如"属性名：属性值"的键值对
        :return: 创建好的关系对象
        """
        new_relation = Relationship(start_node, relation_type, end_node)
        new_relation.update(relation_properties)
        self.my_graph.create(new_relation)
        return new_relation

    def updateProperty(self, node_or_relation, aProperty: tuple):
        if (not isinstance(node_or_relation, Node)) and (not isinstance((node_or_relation, Relationship))):
            raise TypeError('node_or_relation 需要是 Node 或 Relationship 类型')
        node_or_relation[aProperty[0]] = aProperty[1]  # tuple的第一位存属性名，第二位存属性值
        self.my_graph.push(node_or_relation)

    @staticmethod
    def updateMultipleProperty(node_or_relation, properties: dict):
        """同时更新多个属性

        :param node_or_relation: 一个结点或关系对象
        :param properties: 多个需要更新的"属性名:属性值"键值对组成的字典
        :return:
        """
        if (not isinstance(node_or_relation, Node)) and (not isinstance((node_or_relation, Relationship))):
            raise TypeError('node_or_relation 需要是 Node 或 Relationship 类型')
        node_or_relation.update(properties)

    def findOneNode(self, node_type=None, properties=None, where=None):
        """查找一个结点

        :param node_type:结点类型，即 label，类型是str
        :param properties: 多个"属性名: 属性值"键值对组成的字典，类型是dict
        :param where: 查询子句，类型是str
        :return: 一个Node类型的结点
        """
        matcher = NodeMatcher(self.my_graph)

        if not (isinstance(node_type, str)):
            raise TypeError('查询的结点的类型必须要指定，而且node_type必须是字符串类型')

        if not (properties is None):
            if not (isinstance(properties, dict)):
                raise TypeError('properties是多个属性键值对组成的字典，它必须是dict类型')

        if not (where is None):
            if not (isinstance(where, str)):
                raise TypeError('where表示的是查询条件，它必须是字符串类型')

        if (where is None) and (properties is None):
            return matcher.match(node_type).first()

        elif (not (properties is None)) and (where is None):
            return matcher.match(node_type, **properties).first()

        elif (properties is None) and (not (where is None)):
            return matcher.match(node_type).where(where).first()

    def findAllNode(self, node_type=None, properties=None, where=None):
        """查找多个结点

        :param node_type: node_type:结点类型，即 label，类型是str
        :param properties: 多个"属性名: 属性值"键值对组成的字典，类型是dict
        :param where: 查询子句，类型是str
        :return: 多个Node类型的结点组成的list，类型是list
        """
        matcher = NodeMatcher(self.my_graph)
        if not (isinstance(node_type, str)):
            raise TypeError('查询的结点的类型必须要指定，而且node_type必须是字符串形式')
        if not (where is None):
            if not (isinstance(where, str)):
                raise TypeError('where表示的是查询条件，它必须是字符串形式')

        if (properties is None) and (where is None):
            res = matcher.match(node_type)
            if len(list(res)) > 0:
                return list(res)
            else:
                return None

        elif (not (properties is None)) and (where is None):
            res = matcher.match(node_type, **properties)
            if len(list(res)) > 0:
                return list(res)
            else:
                return None

        elif (properties is None) and (not (where is None)):
            res = matcher.match(node_type).where(where)
            if len(list(res)) > 0:
                return list(res)
            else:
                return None

    def findOneRelationship(self, nodes=None, r_type=None):
        """ 查找一条关系

        :param nodes: 要查找的结点集合，比如[起点，终点]，这个参数可以没有
        :param r_type: 要查找的关系的类型
        :return:  None 或者 一条查询结果
        """

        if (nodes is None) and (r_type is None):
            raise TypeError('nodes 和 r_type 必须有一个是非空')

        elif (not (nodes is None)) and (not (r_type is None)):
            return self.my_graph.match_one(nodes=nodes, r_type=r_type)

        elif (not (nodes is None)) and (r_type is None):
            return self.my_graph.match_one(nodes=nodes)

        elif (nodes is None) and (not (r_type is None)):
            return self.my_graph.match_one(r_type=r_type)

    def findAllRelationship(self, nodes=None, r_type=None):
        """ 查找多条关系

        :param nodes: 要查找的结点集合，比如[起点，终点]，这个参数可以没有
        :param r_type: 要查找的关系的类型
        :return:  None 或者 多条查询结果组成的list
        """

        if (nodes is None) and (r_type is None):
            raise TypeError('nodes 和 r_type 必须有一个是非空')

        elif (not (nodes is None)) and (not (r_type is None)):
            res = self.my_graph.match(nodes=nodes, r_type=r_type)
            if res is None:
                return None
            else:
                return list(res)


        elif (not (nodes is None)) and (r_type is None):
            res = self.my_graph.match(nodes=nodes)
            if res is None:
                return None
            else:
                return list(res)

        elif (nodes is None) and (not (r_type is None)):
            res = self.my_graph.match(r_type=r_type)
            if res is None:
                return None
            else:
                return list(res)

    def isExist(self, node=None, relationship=None):
        if (node is None) and (relationship is None):
            raise TypeError('要查询的 node 和 relationship 之中必须有一个存在值')

        if (not (node is None)) and isinstance(node, Node):
            return self.my_graph.exists(node)
        elif (not (relationship is None)) and isinstance(relationship, Relationship):
            return self.my_graph.exists(relationship)
        else:
            raise TypeError('要查询的 node 或 relationship 的类型并不是 Node 或 Relationship')


if __name__ == '__main__':
    dao = Neo4jDao(username='neo4j', password='123456789')
    node1 = dao.createNode(label='Person',
                            properties={'name': 'lwf', 'living': 'xi an', 'home': '福建', 'favor singer': '周杰伦'})
    # node2 = dao.createNode(label='Person',
    #                        properties={'name': 'asjdioasjd', 'living': 'xi an', 'home': 'shan xi', 'favor singer': 'LJJ'})
    node3 = dao.createNode(label='Movie', properties={'name': "复仇者联盟4：Eng Game"})
    # relation = dao.createRelationship(start_node=node1, end_node=node2, relation_type='add')
    # relation2 = dao.createRelationship(start_node=node1, end_node=node2, relation_type='sub')
    # relation3 = dao.createRelationship(start_node=node2, end_node=node1, relation_type='add')
    relation4 = dao.createRelationship(start_node=node1, end_node=node3, relation_type='观看')
    # print(dao.findOneRelationship(r_type='sub'))
    # print(dao.findAllRelationship(r_type='sub'))
    # print(dao.findOneRelationship(nodes=[node1], r_type='watch'))
    # print(dao.findAllRelationship(nodes=[node1]))
    #
    # # result1 = dao.findOneNode(node_type='Person')
    # result1 = dao.findAllNode(node_type='Movie')
    # for my_node in result1:
    #     print('my_node is {}'.format(my_node))
    #     print('type of my_node is {}'.format(type(my_node)))
    #     # r = dao.findOneRelationship(nodes=my_node, r_type='watch')
    #     # print(r)
    #
    # result2 = dao.findOneNode(node_type='Person', properties={'home': '福建'})
    # print("result2:{}".format(result2))
    # result4 = dao.findOneNode(node_type='Person', properties={'living': 'xi an'})
    # print("result4:{}".format(result4))
    # result3 = dao.findAllNode(node_type='Person', where='_.living=~ "x.*"')
    # print("result3:{}".format(result3))
    # print("result3的类型为:{}".format(type(result3)))
