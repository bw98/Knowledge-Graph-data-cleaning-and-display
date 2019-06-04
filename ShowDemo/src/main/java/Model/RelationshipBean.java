package Model;

import Config.SystemInfo;
import Dao.Neo4jDao;
import Util.BuffersToJson;

import java.util.ArrayList;
import java.util.Map;

public class RelationshipBean {

    // 根据如下语句格式设计的添加关系的方法
    // match (n1:XXX1{yyy1:"xxx1"}),(n2:XXX2{yyy2:"xxx2"})
    // merge (n1)-[:RRR{sss:"rrr"}]-(n2)
    // 添加 someParametersValue.length 条类型相同的关系，它们具有相同的起点和终点
    public void addRelationship(String fromNodeType, String relType, String toNodeType, String[] someParametersName, String[][] someParametersValue) {
        Neo4jDao dao = new Neo4jDao();
        for (int index = 0; index < someParametersValue.length; index++) {
            String cypher = "";
            StringBuffer stringBuffer = new StringBuffer("");
            stringBuffer.append("match (n1:");
            stringBuffer.append(fromNodeType);
            stringBuffer.append("{");
            stringBuffer.append(someParametersName[0]);
            stringBuffer.append(":");
            stringBuffer.append("\"");
            stringBuffer.append(someParametersValue[index][0]);
            stringBuffer.append("\"");
            stringBuffer.append("})");
            stringBuffer.append(",");
            stringBuffer.append("(n2:");
            stringBuffer.append(toNodeType);
            stringBuffer.append("{");
            stringBuffer.append(someParametersName[2]);
            stringBuffer.append(":");
            stringBuffer.append("\"");
            stringBuffer.append(someParametersValue[index][2]);
            stringBuffer.append("\"");
            stringBuffer.append("}) ");
            stringBuffer.append("merge (n1)-[:");//开始添加关系
            stringBuffer.append(relType);
            stringBuffer.append("{");//开始关系属性的添加
            stringBuffer.append(someParametersName[1]);
            stringBuffer.append(":");
            stringBuffer.append("\"");
            stringBuffer.append(someParametersValue[index][1]);
            stringBuffer.append("\"");
            //（我的数据库像不满足这个条件，不是所有关系都有属性）
            //因为添加关系时，关系必须有一个属性，
            //所以，下面的循环是添加关系其他的属性（如果有的话）
            for (int i = 3; i < someParametersName.length; i++) {
                stringBuffer.append(",");
                stringBuffer.append(someParametersName[i]);
                stringBuffer.append(":");
                stringBuffer.append("\"");
                stringBuffer.append(someParametersValue[index][i]);
                stringBuffer.append("\"");
            }
            stringBuffer.append("}]-(n2)");
            //得到cypher语句
            cypher = stringBuffer.toString();
            System.out.println(cypher);
            //执行
            dao.executeCypher(cypher);
        }
        dao.close();
    }

    //按照标准格式 "match ()-[r:RRR{xxx:"yyy"}]-() return r" 设计的查找单个结点所有属性的方法
    public Map<String, String> getRelAttrs(String relType, String oneAttrKeyName, String oneAttrKeyValue) {
        Map<String, String> relation_AttrKey_AttrValue_Map = null;

        String cypher = "";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("match ()-");
        stringBuffer.append("[r:");
        stringBuffer.append(relType);
        stringBuffer.append("{");
        stringBuffer.append(oneAttrKeyName);
        stringBuffer.append(":");
        stringBuffer.append("\"");
        stringBuffer.append(oneAttrKeyValue);
        stringBuffer.append("\"");
        stringBuffer.append("}");
        stringBuffer.append("]");
        stringBuffer.append("-() ");
        stringBuffer.append("return r");

        cypher = stringBuffer.toString();

        System.out.println(cypher);
        //执行
        Neo4jDao dao = new Neo4jDao();
        relation_AttrKey_AttrValue_Map = dao.executeFindRelAttrCypher(cypher);
        dao.close();
        return relation_AttrKey_AttrValue_Map;
    }

    //写入json文件,查找
    public String lookInto(String nodeType, String nodeName) {
        String cypher = "";

        if (nodeName == null || nodeName == "") {
            StringBuffer stringBuffer1 = new StringBuffer("");
            stringBuffer1.append("match p=(:");
            stringBuffer1.append(nodeType);
            stringBuffer1.append(")");
            stringBuffer1.append("-[*..1]-() return p");

            cypher = stringBuffer1.toString();
        } else {
            StringBuffer stringBuffer2 = new StringBuffer("");
            stringBuffer2.append("match p=(:");
            stringBuffer2.append(nodeType);
            stringBuffer2.append("{");
            stringBuffer2.append("name:");
            stringBuffer2.append("\"");
            stringBuffer2.append(nodeName);
            stringBuffer2.append("\"");
            stringBuffer2.append("}");
            stringBuffer2.append(")");
            stringBuffer2.append("-[*..1]-() return p");

            cypher = stringBuffer2.toString();

            //先清空，再将cypher语句加入到SystemInfo中

        }
        SystemInfo.clearAll();
        SystemInfo.addCypher(cypher);
//System.out.println(cypher);

        Neo4jDao dao = new Neo4jDao();
        StringBuffer relationBuffer = dao.executeFindRelationCypher(cypher);
        StringBuffer relationNodesBuffer = dao.executeFindRelationNodesCypher(cypher);
        BuffersToJson toJson = new BuffersToJson(relationNodesBuffer, relationBuffer);
        //toJson.writeJson();
//System.out.println(toJson.getJson());
        dao.close();
        return toJson.getJson();
    }

    //针对的是多条查询语句
    //返回d3所需要的json
    public String lookIntos(ArrayList<String> cypherArrayList) {
        String cypher = "";

        Neo4jDao dao = new Neo4jDao();
        StringBuffer relationBuffer = dao.executeFindRelationCyphers(cypherArrayList);
        StringBuffer relationNodesBuffer = dao.executeFindRelationNodesCyphers(cypherArrayList);
        BuffersToJson toJson = new BuffersToJson(relationNodesBuffer, relationBuffer);
        //toJson.writeJson();
        dao.close();
        return toJson.getJson();
    }
}
