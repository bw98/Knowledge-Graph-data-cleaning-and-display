package Servlet;

import Config.SystemInfo;
import Model.RelationshipBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DisplayServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");

        resp.setContentType("text/html; charset=utf-8");
        PrintWriter out = resp.getWriter();

        String selectItem1 = req.getParameter("selectItem1");
        String selectItem2 = req.getParameter("selectItem2");
        String keyword = req.getParameter("keyword");
        String times = req.getParameter("times");//判断这次操作是用户点击查询按钮还是双击节点
        String isHide = req.getParameter("isHide");

        String nodeType = "";
        String nodeName = "";

        if ((selectItem1 != null) && (!selectItem1.equals(""))) {
            if ((selectItem2 != null) && (!selectItem2.equals(""))) {
                nodeType = selectItem2;
            }
            else nodeType = selectItem1;
        }
        nodeName = keyword;

        String json = "";
        RelationshipBean relationshipBean = new RelationshipBean();
        //用户点击的是查询按钮！
        if (times.equals("first") || isHide.equals("yes")) {
            //这里需要特别注意！！，因为更换了服务器tomcat->neo4j，所以会报500错误，
            //解决办法：将neo4j的驱动包，拷到jre中lib的ext目录下
            json = relationshipBean.lookInto(nodeType, nodeName);
        }
        //用户双击了节点！
        else {
            //执行新的查询方法（允许多条查询语句语句同时进行）
            //先将语句加入到SystemInfo中，然后在得到全部的查询语句
            String cypher = "";

//            版本一：不支持结点name属性模糊查询
            // 颜色显示那块还有bug，不会根据结点类型而分色
            StringBuffer stringBuffer = new StringBuffer("");
            stringBuffer.append("match p=(:");
            stringBuffer.append(nodeType);
            stringBuffer.append("{");
            stringBuffer.append("name:");
            stringBuffer.append("\"");
            stringBuffer.append(nodeName);
            stringBuffer.append("\"");
            stringBuffer.append("}");
            stringBuffer.append(")");
            stringBuffer.append("-[*..1]-() return p");

////            版本二：支持结点name属性模糊查询
//            //有bug，需要调试
//            StringBuffer stringBuffer = new StringBuffer("");
//            stringBuffer.append("match p=(:");
//            stringBuffer.append(nodeType);
//            stringBuffer.append(")");
//            stringBuffer.append("-[*..1]-() ");
//            stringBuffer.append("where n.name =~");
//            stringBuffer.append("\".*");
//            stringBuffer.append(nodeName);
//            stringBuffer.append(".*\"");
//            stringBuffer.append("return p");

            cypher = stringBuffer.toString();

            SystemInfo.addCypher(cypher);

            //得到所有的查询语句
            ArrayList<String> cypherArrayList = SystemInfo.getCypherArrayList();

            /*for(int i = 0;i<cypherArrayList.size();i++){
                System.out.println(cypherArrayList.get(i));
            }*/

            json = relationshipBean.lookIntos(cypherArrayList);
        }
        System.out.println("返回的json:" + json);
        out.write(json);
    }
}
