<h2 align = "center"> 课题名称：知识图谱数据清洗与展示</h2>

### 一、课题要求

要求：对实验室的集群服务器上的Hbase数据库中论文、组织、还有人才数据进行建模，把人才、论文、组织以我们理解的实体作为节点，预定义它们之间的关系，从HBase里取数据，然后进行数据清洗，最后用图数据库neo4j存储知识图谱。在构建知识图谱后，需要搭建一个查询系统，使用d3.js将知识图谱进行展示。
<br></br>
### 二、课题内容

1、从60服务器上利用Hbase提供的接口，将组织、人才、论文数据取出。

2、在获取数据后，针对每张表的存储特性，对数据进行清洗。

3、将清洗完的数据按照个人理解进行模型的定义

4、按照定义的模型，将清洗完的数据存入图数据库中以构建知识图谱

5、搭建支持前后端交互的查询系统，将数据库中的知识图谱以web的形式展现给用户。
<br></br>

### 三、方案设计

#### 3.1、数据获取
本次项目的数据已经提前存储在了实验室60服务器上的Hbase数据库中，它是一个Hadoop支持的数据库，支持存储千万与亿级别的数据条目。Hbase提供了&quot;Hbase shell&quot;命令允许我们以命令行界面访问Hbase，通过&quot;list&quot;命令查询后，得到此次需要获取数据的三张表名，分别是OrgProfile、PersonProfile以及test2，它们分别是组织表、人才表以及论文表。
起初，由于不熟悉Hbase数据的导出，我使用了Linux重定向将Hbase查询的结果导出，但是遇到了乱码、格式混乱、执行效率低的问题，便放弃了用这个办法导出数据。然后咨询了学长学姐，其中王鹏华学长给我提供了很有建设性的建议：编写java代码，采用Hadoop 与Hbase 关联API将数据读出并写入到文件。于是，我开始根据这个想法进行代码实现，写了一个叫HbaseOperation的项目文件，打成了jar包并放在了home我的目录下，支持手动输入表名，通过命令&quot;java -jar HbaseOperation-1.0-SNAPSHOT.jar&quot;，能够以特定格式导出对应表的数据。

读取格式为：&quot;行键    列族名:限定词1   值1    列族名:限定词2    值2&quot;。

[![VtDZJ1.md.png](https://s2.ax1x.com/2019/06/04/VtDZJ1.md.png)](https://imgchr.com/i/VtDZJ1)
<h5 align = "center">图. 通过特定命令获取对应表名的所有数据</h5>
导入好数据后，由于每张表的数据量过大，不利于本地工作，我利用Linux的split命令对大文件进行了切割，分别存储在了OrgProfile、PersonProfile、test2三个文件下，并通过scp命令发送回了本地。

#### 3.2、数据清洗、模型构建与数据存储

获取了数据后，通过简单的分析发现数据并不太规整，而且还存在一人多组织、部分值为null的情况，通过简单规则进行存储会出现很多问题。

[![VtDmz6.md.png](https://s2.ax1x.com/2019/06/04/VtDmz6.md.png)](https://imgchr.com/i/VtDmz6)
<h5 align = "center">图. 不规则数据展示</h5><br></br>
所以，我写了一个python项目用于数据的清洗与数据的存储。名为“知识图谱项目”，项目结构如下：

[![VtDeRx.md.png](https://s2.ax1x.com/2019/06/04/VtDeRx.md.png)](https://imgchr.com/i/VtDeRx)
<h5 align = "center">图. python项目目录结构</h5><br></br>
其中Dao层的代码是我对py2neo库函数二次封装后的类，用于处理数据库操作，比如插入结点、插入关系、查找一个结点、查找特定结点为起点的所有一度相邻的关系等等。Data层是我要处理的文件，一共有三个，都是从Hbase中读取出来的文件，关于组织、人才、论文的文件。service层的代码负责逻辑操作，比如ReadData.py实现的是读取文件数据并转化为特定的数据结构，saveDataByNeo4j.py里有三个方法，分别对三张表的结构进行数据信息的提取并以特定的模型存储到neo4j 图数据库。main.py是这个项目的执行入口，对于整个项目，我们只需要快速修改一下main.py里头的配置信息，然后执行命令“python3 main.py”即可完成数据的清洗、模型构建与存储。
随后，启动neo4j的可视化界面，查看存储好的知识图谱。为了方便查询与支持后续的算法，我把每一个列族下的每一个限定词都当成了结点，系统会自动建立索引以加速查询效率。比如“address”这个限定词，通过这种方法，我们能够快速找到在北京的组织。

[![VtDlee.md.png](https://s2.ax1x.com/2019/06/04/VtDlee.md.png)](https://imgchr.com/i/VtDlee)
<h5 align = "center">图. neo4j中的数据展示</h5><br></br>

[![VtDuQK.md.png](https://s2.ax1x.com/2019/06/04/VtDuQK.md.png)](https://imgchr.com/i/VtDuQK)
<h5 align = "center">图. 以adress为起点，查找所有度为1关系的组织</h5><br></br>

#### 3.3. 构建系统与数据可视化
在第二部分中，我给出了neo4j自带的可视化界面的部分截图。虽然它比较好看，但是没法集成到我们的查询系统中，所以我利用 java web 相关技术（neo4j.driver，servlet，tomcat，jsp）、一个名为d3.js的前端数据可视化框架以及layui前端框架，搭建了知识图谱查询系统。
首先是每一个系统都会有注册登录界面，采用mysql存储用户信息，java web 实现后端，layui实现前端注册登录页面，并命名为“基于Neo4j的知识图谱展示项目”。由于本项目注册登录功能是额外需求，所以只完成前端页面了，后端不再用相关逻辑进行判断而是直接转发重定向到主界面。

[![VtD3od.md.png](https://s2.ax1x.com/2019/06/04/VtD3od.md.png)](https://imgchr.com/i/VtD3od)
<h5 align = "center">图. 查询系统的登录注册页面</h5><br></br>
第二，做完了登录注册页面后，根据需求，还需要完成一个主界面以进行知识图谱的可视化展示，主要是按照特定的查询需求，将已构建好的组织Profile的知识图谱展示出来。这一部分采用了MVC架构，对于Dao层，主要利用了neo4j提供给java的driver包，实现对图数据库neo4j的增删查改。Controller层主要是使用了Servlet对前台发送过来的请求进行处理，然后调用Model和Dao层的相关方法进行数据的查询和数据实体封装，最后以json格式把结点和关系的相关信息发送回前端，在前端利用d3.js将json串以知识图谱的形式展示出来。
主页面的基本布局如下：

[![VtDMLD.md.png](https://s2.ax1x.com/2019/06/04/VtDMLD.md.png)](https://imgchr.com/i/VtDMLD)
<h5 align = "center">图. 主页面的基本布局</h5><br></br>
在搜索框里可以搜索组织名，然后选择是否隐藏后续结点，最后点击查询按钮，在页面中央的空白处会显示经过d3.js处理的知识图谱。

[![VtDKsO.md.png](https://s2.ax1x.com/2019/06/04/VtDKsO.md.png)](https://imgchr.com/i/VtDKsO)
<h5 align = "center">图. 搜索框展示</h5><br></br>
借鉴neo4j自带的可视化页面，每一个结点类型都对应了不同的颜色以方便用户区分不同类型的结点；在前端代码中设定了js的focus事件，当鼠标在特定结点上时会显示其类型和名字，同时设定了双击事件，当我们用鼠标双击某个特定结点时，会自动将以其为起点、度为1关系的所有结点都显示在界面上。下图是我们搜索组织“ABB中国研究院”后，并选中“是否隐藏后续结点（否）”，展示出来的关于“ABB中国研究院”这个组织的知识图谱，它的子结点分别为它的所属成员、组织地址、组织邮编，如果进一步点击其组织地址的话，还能够显示和它在同一个地址的其他组织。

[![VtD1dH.md.png](https://s2.ax1x.com/2019/06/04/VtD1dH.md.png)](https://imgchr.com/i/VtD1dH)
<h5 align = "center">图. 搜索“ABB中国研究院”后展示的知识图谱</h5><br></br>

### 四、课题总结
在本次课题中，我负责实验室的知识图谱相关项目的数据清洗、图谱构建以及图谱展示。通过本次课题，我知识图谱的应用有了更深刻的的认识，明白了知识图谱是什么，在实际项目中知识图谱扮演的是怎样一种作用。除此之外，我还学习到了在做一个项目时，需要向他人阐述自己的想法，多听取他人的建议，集思广益，提高工程的鲁棒性、执行效率以及复用性。
