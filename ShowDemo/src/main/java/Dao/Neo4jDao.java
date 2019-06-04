package Dao;

import Util.BuffersToJson;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.*;

import Config.SystemInfo;

public class Neo4jDao {
    private Driver driver = null;

    public Neo4jDao() {
        String url = "Bolt://localhost:7687";
        this.driver = GraphDatabase.driver(url, AuthTokens.basic("neo4j", "123456789"));
    }

    public Neo4jDao(String userName, String password) {
        String url = "Bolt://localhost:7687";
        this.driver = GraphDatabase.driver(url, AuthTokens.basic(userName, password));
    }

    public void close() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

    public void getNodesInfo(String cypher) {
        Map<String, String> node_AttrKey_AttrValue_Map = new HashMap<String, String>();

        try {
            Session session = this.driver.session();
            StatementResult result = session.run(cypher);

            while (result.hasNext()) {
                Record record = result.next();
                List<Value> value_list = record.values();

                for (Value i : value_list) {
                    Node node = i.asNode();
                    Iterator keys = node.keys().iterator();

                    Iterator nodeTypes = node.labels().iterator();
                    String nodeType = nodeTypes.next().toString();
                    System.out.println("节点类型：" + nodeType);
                    System.out.println("节点属性如下：");
                    while (keys.hasNext()) {
                        String attrKey = (String) keys.next();
                        String attrValue = node.get(attrKey).asString();
                        System.out.println(attrKey + "-------" + attrValue);
                    }
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPathsInfo(String cypher) {
        int count = 0;
        try {
            Session session = this.driver.session();
            //result包含了所有的path
            StatementResult result = session.run(cypher);

            while (result.hasNext()) {
                Record record = result.next();
                List<Value> value = record.values();

                for (Value i : value) {
                    Path path = i.asPath();
                    //处理路径中的关系
                    Iterator<Relationship> relationships = path.relationships().iterator();

                    //Iterator<Node> nodes = path.nodes().iterator();//得到组成path的节点

                    while (relationships.hasNext()) {
                        count++;
                        Relationship relationship = relationships.next();

                        long startNodeId = relationship.startNodeId();
                        long endNodeId = relationship.endNodeId();
                        String relType = relationship.type();

                        System.out.println("关系" + count + "： ");
                        System.out.println("关系类型：" + relType);
                        System.out.println("from " + startNodeId + "-----" + "to " + endNodeId);

                        System.out.println("关系属性如下：");

                        //得到关系属性的健
                        Iterator<String> relKeys = relationship.keys().iterator();
                        //这里处理关系属性
                        while (relKeys.hasNext()) {
                            String relKey = relKeys.next();
                            String relValue = relationship.get(relKey).asObject().toString();
                            System.out.println(relKey + "-----" + relValue);
                        }
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //增、删、改neo4j数据库中的节点
    public void executeCypher(String cypher) {
        try {
            Session session = this.driver.session();
            Transaction transaction = session.beginTransaction();
            transaction.run(cypher);
            transaction.success();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回查询到的最后一个节点所有属性的键值对
    public Map<String, String> executeFindNodeCypher(String cypher) {

        Map<String, String> node_AttrKey_AttrValue_Map = new HashMap<String, String>();

        try {
            Session session = this.driver.session();
            StatementResult result = session.run(cypher);

            // 这么写效率有点低，实际上只需要最后一条记录
            while (result.hasNext()) {
                Record record = result.next();  // 查询得到的一条记录
                List<Value> values = record.values();  // 一条记录中所有键值对组成的value对象

                for (Value i : values) {
                    Node node = i.asNode();  // 将value对象转化为Neo4j中的结点对象
                    Iterator keys = node.keys().iterator();

                    // 遍历一条记录中的所有键值对
                    while (keys.hasNext()) {
                        String attrKey = (String) keys.next(); // 得到keys当前位置的键名然后使keys迭代器加一
                        String attrValue = node.get(attrKey).asString();
                        node_AttrKey_AttrValue_Map.put(attrKey, attrValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node_AttrKey_AttrValue_Map;
    }

    //返回某单个关系的属性的键值对
    public Map<String, String> executeFindRelAttrCypher(String cypher) {

        Map<String, String> Rel_AttrKey_AttrValue_Map = new HashMap<String, String>();

        try {
            Session session = this.driver.session();
            StatementResult result = session.run(cypher);

            while (result.hasNext()) {
                Record record = result.next();
                List<Value> value = record.values();

                for (Value i : value) {
                    Map map = i.asMap();
                    Rel_AttrKey_AttrValue_Map = map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Rel_AttrKey_AttrValue_Map;
    }

    //返回关系的StringBuffer，为可视化做准备！json文件
    public StringBuffer executeFindRelationCypher(String cypher) {
        //关系的StringBuffer,json格式
        StringBuffer relationBuffer = new StringBuffer("");
        relationBuffer.append("\"links\":[");//return "links":[

        try {
            Session session = this.driver.session();
            //result包含了所有的path
            StatementResult result = session.run(cypher);

            while (result.hasNext()) {
                Record record = result.next();
                List<Value> value = record.values();

                for (Value i : value) {
                    Path path = i.asPath();
                    Iterator<Relationship> relationships = path.relationships().iterator();

                    while (relationships.hasNext()) {
                        Relationship relationship = relationships.next();

                        long startNodeId = relationship.startNodeId();
                        long endNodeId = relationship.endNodeId();
                        String relType = relationship.type();

                        //得到关系属性的健
                        Iterator<String> relKeys = relationship.keys().iterator();

                        relationBuffer.append("{");
                        relationBuffer.append("\"source\":");
                        relationBuffer.append(startNodeId);
                        relationBuffer.append(",");
                        relationBuffer.append("\"target\":");
                        relationBuffer.append(endNodeId);
                        relationBuffer.append(",");
                        relationBuffer.append("\"type\":");
                        relationBuffer.append("\"" + relType + "\"");

                        //这里处理关系属性
                        while (relKeys.hasNext()) {
                            String relKey = relKeys.next();
                            String relValue = relationship.get(relKey).asObject().toString();

                            //去除制表符
                            relValue = relValue.replaceAll("\t", "");
                            //去除换行符
                            relValue = relValue.replaceAll("\r", "");
                            //去除回车符
                            relValue = relValue.replaceAll("\n", "");

                            //将双引号换成单引号
                            relValue = relValue.replaceAll("\"", "'");

                            relationBuffer.append(",");
                            relationBuffer.append("\"" + relKey + "\"");
                            relationBuffer.append(":");
                            relationBuffer.append("\"" + relValue + "\"");
                        }
                        if (!relationships.hasNext() && !result.hasNext()) {
                            relationBuffer.append("}");  //如果是最后一个，只需要添加}即可
                        } else {
                            relationBuffer.append("},");
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relationBuffer.append("]");
        return relationBuffer;
    }

    //返回关系中节点的StringBuffer，为可视化做准备！json文件,需要增加节点的类型
    public StringBuffer executeFindRelationNodesCypher(String cypher) {
        //用一个set集合去除重复项
        Set nodeSet = new HashSet();
        StringBuffer relationNodesBuffer = new StringBuffer("");
        relationNodesBuffer.append("\"nodes\":[");

        try {
            Session session = this.driver.session();
            StatementResult result = session.run(cypher);

            while (result.hasNext()) {
                Record record = result.next();
                List<Value> value = record.values();

                for (Value i : value) {

                    Path path = i.asPath();
                    Iterator<Node> nodes = path.nodes().iterator();

                    while (nodes.hasNext()) {
                        Node node = nodes.next();
                        //在增加节点以前，先判断是否在集合中，避免重复保存
                        boolean isExist = nodeSet.contains(node.id());
                        if (isExist) continue;
                        Iterator<String> nodeKeys = node.keys().iterator();
                        relationNodesBuffer.append("{");

                        //节点属性
                        while (nodeKeys.hasNext()) {
                            String nodeKey = nodeKeys.next();
                            relationNodesBuffer.append("\"" + nodeKey + "\":");
                            //node.get(nodeKey).toString();
                            //System.out.println(node.get(nodeKey).asObject().toString());
                            String content = node.get(nodeKey).asObject().toString();

                            //去除制表符
                            content = content.replaceAll("\t", "");
                            //去除换行符
                            content = content.replaceAll("\r", "");
                            //去除回车符
                            content = content.replaceAll("\n", "");

                            //将双引号换成单引号
                            content = content.replaceAll("\"", "'");

                            relationNodesBuffer.append("\"" + content + "\",");
                        }
                        relationNodesBuffer.append("\"id\":");
                        relationNodesBuffer.append(node.id());
                        //添加节点类型！不知道为什么取得节点类型用的是labels，可能一个节点可以属于多个类别
                        //但是我们这里只属于一个类别！
                        Iterator<String> nodeTypes = node.labels().iterator();
                        //得到节点类型了！
                        String nodeType = nodeTypes.next();

                        relationNodesBuffer.append(",");
                        relationNodesBuffer.append("\"type\":");
                        relationNodesBuffer.append("\"" + nodeType + "\"");

                        //将节点添加到set集合中
                        nodeSet.add(node.id());

                        if (!nodes.hasNext() && !result.hasNext()) {
                            relationNodesBuffer.append("}");
                        } else {
                            relationNodesBuffer.append("},");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int bufferLength = relationNodesBuffer.length();
        char lastChar = relationNodesBuffer.charAt(bufferLength - 1);
        if (lastChar == ',') {
            String str = relationNodesBuffer.substring(0, relationNodesBuffer.length() - 1);
            relationNodesBuffer = relationNodesBuffer.replace(0, bufferLength, str);
        }
        relationNodesBuffer.append("]");
        close();
        return relationNodesBuffer;
    }

    //执行多条cypher语句
    //处理关系
    public StringBuffer executeFindRelationCyphers(ArrayList<String> cypherArrayList) {
        StringBuffer relationBuffer = new StringBuffer("");
        relationBuffer.append("\"links\":[");//return "links":[

        int index = 0;

        //新建一个Map,以startNodeId作为key，以endNodeId作为value，用来去重
        Map<Long, Long> start_end = new HashMap<Long, Long>();

        //不知道可不可以批处理
        //这里我就没用批处理了
        try {
            Session session = this.driver.session();

            for (int i = 0; i < cypherArrayList.size(); i++) {
                String cypher = cypherArrayList.get(i);
                StatementResult statementResult = session.run(cypher);

                while (statementResult.hasNext()) {
                    Record record = statementResult.next();
                    List<Value> list = record.values();

                    for (Value item : list) {
                        Path path = item.asPath();
                        Iterator<Relationship> rels = path.relationships().iterator();
                        while (rels.hasNext()) {
                            index++;

                            Relationship relationship = rels.next();
                            long startNodeId = relationship.startNodeId();
                            long endNodeId = relationship.endNodeId();

                            //先判断是否重复,
                            boolean isExist = start_end.get(startNodeId) != null && start_end.get(startNodeId) == endNodeId;
                            //如果重复，跳过该关系
                            if (isExist) {
                                continue;
                            }
                            //没用重复,将将该关系添加到map中
                            start_end.put(startNodeId, endNodeId);

                            String relType = relationship.type();

                            //一个新的关系到来
                            if (index == 1) {
                                relationBuffer.append("{");
                            } else {
                                relationBuffer.append(",{");
                            }
                            relationBuffer.append("\"source\":");
                            relationBuffer.append(startNodeId);
                            relationBuffer.append(",");
                            relationBuffer.append("\"target\":");
                            relationBuffer.append(endNodeId);
                            relationBuffer.append(",");
                            relationBuffer.append("\"type\":");
                            relationBuffer.append("\"" + relType + "\"");

                            Iterator<String> relKeys = relationship.keys().iterator();

                            while (relKeys.hasNext()) {
                                String relKey = relKeys.next();
                                String relKeyValue = relationship.get(relKey).asObject().toString();

                                //去除空格
                                //relKeyValue = relKeyValue.replaceAll("\\s","");
                                //去除制表符
                                relKeyValue = relKeyValue.replaceAll("\t", "");
                                //去除换行符
                                relKeyValue = relKeyValue.replaceAll("\r", "");
                                //去除回车符
                                relKeyValue = relKeyValue.replaceAll("\n", "");

                                //将双引号换成单引号
                                relKeyValue = relKeyValue.replaceAll("\"", "'");

                                relationBuffer.append(",");
                                relationBuffer.append("\"" + relKey + "\":");
                                relationBuffer.append("\"" + relKeyValue + "\"");
                            }
                            relationBuffer.append("}");
                            /*if(i==cypherArrayList.size()-1&&!rels.hasNext()){
                                relationBuffer.append("}");
                            }
                            else{
                                relationBuffer.append("},");
                            }*/
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        relationBuffer.append("]");
        return relationBuffer;
    }

    //接下来是处理节点！！，这个有点复杂了！因为需要添加颜色属性！
    //执行多条cypher语句
    public StringBuffer executeFindRelationNodesCyphers(ArrayList<String> cypherArrayList) {
        //用一个set集合去除重复项
        Set nodeSet = new HashSet();
        StringBuffer relationNodesBuffer = new StringBuffer("");
        relationNodesBuffer.append("\"nodes\":[");
        int index = 0;

        try {
            Session session = this.driver.session();

            for (int i = 0; i < cypherArrayList.size(); i++) {
                String cypher = cypherArrayList.get(i);
                StatementResult statementResult = session.run(cypher);

                while (statementResult.hasNext()) {
                    Record record = statementResult.next();
                    List<Value> list = record.values();

                    for (Value item : list) {
                        Path path = item.asPath();
                        Iterator<Node> nodes = path.nodes().iterator();

                        while (nodes.hasNext()) {

                            index++;

                            Node node = nodes.next();
                            long nodeId = node.id();
                            boolean isExist = nodeSet.contains(nodeId);
                            if (isExist) continue;

                            if (index == 1) {
                                relationNodesBuffer.append("{");
                            } else {
                                relationNodesBuffer.append(",{");
                            }

                            //没有重复，将nodeId添加到集合中
                            nodeSet.add(nodeId);

                            //得到node的类型
                            Iterator<String> nodeTypes = node.labels().iterator();
                            String nodeType = nodeTypes.next();
                            //从SystemInfo中得到NodeTypeKeys，好为新的节点类型分配颜色
                            Set<String> nodeTypeKeys = SystemInfo.getNodeTypeKeys();

                            //如果是新的节点类型，则分配颜色，并保持到SystemInfo中
                            if (!nodeTypeKeys.contains(nodeType)) {
                                int colorIndex = nodeTypeKeys.size();
                                //以防万一，所需颜色超过5，则随机一种颜色
                                if (colorIndex >= 5) {
                                    colorIndex = (int) Math.random() * 5;
                                }
                                //得到颜色集合
                                String[] colors = SystemInfo.getColor();
                                String color = colors[colorIndex];
                                //添加
                                SystemInfo.addNodeType_Color(nodeType, color);
                                //添加颜色属性
                                relationNodesBuffer.append("\"color\":");
                                relationNodesBuffer.append("\"" + color + "\"");
                            } else {
                                //说明不是新的节点
                                //通过nodeType得到该节点的颜色
                                Map nodeType_Color = SystemInfo.getNodeType_Color();
                                String color = nodeType_Color.get(nodeType).toString();
                                //添加颜色属性
                                relationNodesBuffer.append("\"color\":");
                                relationNodesBuffer.append("\"" + color + "\"");
                            }

                            //添加id
                            relationNodesBuffer.append(",");
                            relationNodesBuffer.append("\"id\":");
                            relationNodesBuffer.append(nodeId);

                            //添加type
                            relationNodesBuffer.append(",");
                            relationNodesBuffer.append("\"type\":");
                            relationNodesBuffer.append("\"" + nodeType + "\"");

                            //添加其他属性
                            Iterator<String> nodeKeys = node.keys().iterator();

                            while (nodeKeys.hasNext()) {
                                String nodeKey = nodeKeys.next();
                                String nodeKeyValue = node.get(nodeKey).asObject().toString();

                                //去除空格
                                //nodeKeyValue = nodeKeyValue.replaceAll("\\s","");
                                //去除制表符
                                nodeKeyValue = nodeKeyValue.replaceAll("\t", "");
                                //去除换行符
                                nodeKeyValue = nodeKeyValue.replaceAll("\r", "");
                                //去除回车符
                                nodeKeyValue = nodeKeyValue.replaceAll("\n", "");

                                //将双引号换成单引号
                                nodeKeyValue = nodeKeyValue.replaceAll("\"", "'");

                                relationNodesBuffer.append(",");
                                relationNodesBuffer.append("\"" + nodeKey + "\"");
                                relationNodesBuffer.append(":");
                                relationNodesBuffer.append("\"" + nodeKeyValue + "\"");
                            }
                            //添加完一个节点后
                            relationNodesBuffer.append("}");
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        relationNodesBuffer.append("]");
        return relationNodesBuffer;
    }


    public static void main(String[] args) {
        Neo4jDao dao = new Neo4jDao("neo4j", "123456789");

//        String cypher = "match (n:organization) return n";
//        dao.getNodesInfo(cypher);

//        String cypher2 = "match(a:person{name:\"周赟\"}), (b:person{name:\"吕颖\"}), p = shortestPath((a)-[:organization*..2]->(b)) return p";
//        dao.getPathsInfo(cypher2);

//        String cypher3 = "match p = (a:organization)-[*..1]-(b) where a.name<>'null' and b.name<>'null' return p limit 30";
//        StringBuffer relationBuffer = dao.executeFindRelationCypher(cypher3);
//        StringBuffer relationNodesBuffer = dao.executeFindRelationNodesCypher(cypher3);
//        BuffersToJson buffersToJson = new BuffersToJson(relationNodesBuffer, relationBuffer);
//        buffersToJson.writeJson();

//        String cypher = "match (n:person{name:\"严利华\"}) return n";
//        Map<String, String> map = dao.executeFindNodeCypher(cypher);
        dao.close();
    }
}
