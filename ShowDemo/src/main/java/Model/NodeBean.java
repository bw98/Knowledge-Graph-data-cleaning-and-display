package Model;

import Dao.Neo4jDao;

import java.util.Map;

public class NodeBean {

    // 添加 nodeAttributesValue.length 个同类型的结点，它们拥有相同的属性键名
    public void addNode(String nodeType, String[] nodeAttributesName, String[][] nodeAttributesValue) {
        Neo4jDao dao = new Neo4jDao();

        for (int index = 0; index < nodeAttributesValue.length; index++) {
            String cypher = "";
            StringBuffer stringBuffer = new StringBuffer("");
            stringBuffer.append("merge (:");  // merge 匹配已存在的节点. 如果节点不存在就创建新的绑定它
            stringBuffer.append(nodeType);  // 节点类型
            stringBuffer.append("{");  // 开始添加节点属性

            int i = 0; // 需要记录 i 最后的值，以处理最后一个属性时的语句字符串拼接问题
            for (i = 0; i < nodeAttributesName.length - 1; i++) {
                stringBuffer.append(nodeAttributesName[i]);
                stringBuffer.append(":");
                stringBuffer.append("\"");
                stringBuffer.append(nodeAttributesValue[index][i]);
                stringBuffer.append("\",");
            }

            //当添加最后一个属性时需要做特殊处理
            stringBuffer.append(nodeAttributesName[i]);
            stringBuffer.append(":");
            stringBuffer.append("\"");
            stringBuffer.append(nodeAttributesValue[index][i]);
            stringBuffer.append("\"");
            stringBuffer.append("})");

            //一条cypher语句拼接完成
            cypher = stringBuffer.toString();
            //执行
            dao.executeCypher(cypher);
        }

        dao.close();
    }

    //根据标准格式"match (n:XXX{xxx:"yyy"}) return n;" 设计的查找单个结点所有属性的方法
    public Map<String, String> getNodeAttrs(String nodeType, String oneAttrKeyName, String oneAttrKeyValue) {
        Map<String, String> node_AttrKey_AttrValue_Map = null;

        String cypher = "";
        StringBuffer stringBuffer = new StringBuffer("");
        stringBuffer.append("match (n:");
        stringBuffer.append(nodeType);
        stringBuffer.append("{");
        stringBuffer.append(oneAttrKeyName);
        stringBuffer.append(":");
        stringBuffer.append("\"");
        stringBuffer.append(oneAttrKeyValue);
        stringBuffer.append("\"");
        stringBuffer.append("}) ");
        stringBuffer.append("return n");

        cypher = stringBuffer.toString();
        //执行
        Neo4jDao dao = new Neo4jDao();
        node_AttrKey_AttrValue_Map = dao.executeFindNodeCypher(cypher);
        dao.close();

        return node_AttrKey_AttrValue_Map;
    }
}
