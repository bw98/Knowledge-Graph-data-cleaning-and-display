import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Hash;

/**
 * HBase表的增删改查等基本操作演示
 *
 * @author 王鹏华 赖伟峰
 */
public class BasicTableOperation {
    /**
     * 主函数
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("抱歉！您没有输入正确的参数个数（1），该参数是一个Hbase的表名");
            System.exit(0);
        }
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
//		deleteTable(admin, "service_recommend");//删除表

//		creatTableByName(admin, "person", "result");//建表
//		creatTableByName(admin, "service_recommend", "result");//建表
//		creatTableByName(admin, "MyA", "BasicInfo");//建表
//		creatTableByName(admin, "MyB", "BasicInfo");//建表
//		creatTableByName(admin, "MyC", "BasicInfo");//建表
//		insertOneRow(connection, "person", "personRow1", "result", "appType", "appTypeValue");//添加数据
//		insertOneRow(connection, "person", "personRow1", "result", "infold", "infoldValue");//添加数据
//		insertOneRow(connection, "person", "personRow1", "result", "infosld", "infosldValue");//添加数据
//		insertOneRow(connection, "person", "personRow1", "result", "recommendType", "recommendTypeValue");//添加数据
//		insertOneRow(connection, "person", "personRow1", "result", "userId", "userIdValue");//添加数据
//		insertOneRow(connection, "person", "personRow1", "result", "value", "valueValue");//添加数据
//
//		insertOneRow(connection, "service_recommend", "serviceRow1", "inf", "userId", "userIdValue");//添加数据
//		insertOneRow(connection, "service_recommend", "serviceRow1", "inf", "appType", "appTypeValue");//添加数据
//		insertOneRow(connection, "service_recommend", "serviceRow1", "inf", "recommendType", "recommendTypeValue");//添加数据
//		insertOneRow(connection, "service_recommend", "serviceRow1", "inf", "infold", "infoldValue");//添加数据
//		insertOneRow(connection, "service_recommend", "serviceRow1", "inf", "value", "valueValue");//添加数据
//
//		insertOneRow(connection, "MyB", "MyB001", "BasicInfo", "name", "Andy");//添加数据
//		insertOneRow(connection, "MyB", "MyB001", "BasicInfo", "age", "30");//添加数据
//		insertOneRow(connection, "MyB", "MyB002", "BasicInfo", "name", "James");//添加数据
//		insertOneRow(connection, "MyB", "MyB002", "BasicInfo", "age", "32");//添加数据
//
//		insertOneRow(connection, "MyC", "MyC001", "BasicInfo", "name", "小强");//添加数据
//		insertOneRow(connection, "MyC", "MyC001", "BasicInfo", "age", "21");//添加数据
//		insertOneRow(connection, "MyC", "MyC002", "BasicInfo", "name", "李四");//添加数据
//		insertOneRow(connection, "MyC", "MyC002", "BasicInfo", "age", "18");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "BasicInfo", "age", "26");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "BasicInfo", "sex", "male");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "ScoreInfo", "english", "82");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "ScoreInfo", "math", "92");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "BasicInfo", "name", "Anni");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "BasicInfo", "age", "26");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "BasicInfo", "sex", "female");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "ScoreInfo", "english", "92");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "ScoreInfo", "math", "89");//添加数据
        //以下四句话用于测试：是否支持中文限定词和中文值（支持！）
//		insertOneRow(connection, "MyStudents", "A00002", "BasicInfo", "姓名", "张三");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "BasicInfo", "年龄", "23");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "ScoreInfo", "英语", "85");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "ScoreInfo", "数学", "95");//添加数据
        //以下一句用于测试：添加的数据当中的列族名与创建表时的列族名不同名时，是否可行（不可以！必须先新添加列族！）
//		insertOneRow(connection, "MyStudents", "A00003", "BasicInfomation", "name", "Jenny");//添加数据
//		addColumnFamilys(admin, "MyStudents", "EduInfo", "JobInfo");//添加两个新的列族
//		insertOneRow(connection, "MyStudents", "A00001", "EduInfo", "HighSchool", "WeiCheng");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "EduInfo", "University", "Stanford");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "JobInfo", "Empolyer", "Google");//添加数据
//		insertOneRow(connection, "MyStudents", "A00001", "JobInfo", "AnnualAslary", "12000000USD");//添加数据
        //以下一行代码用于测试：是否支持中文列族名（支持！！！）
//		addColumnFamilys(admin, "MyStudents", "家庭");//添加一个新的列族（中文）
//		insertOneRow(connection, "MyStudents", "A00002", "家庭", "儿子数", "0");//添加数据
//		insertOneRow(connection, "MyStudents", "A00002", "家庭", "女儿数", "0");//添加数据
        //以下一行代码用于测试是否支持中文表名（不支持！！！）
//		creatTableByName(admin, "是否支持中文表名", "列族一", "列族二");//建表

//		printAllRecordsByTable(connection, "MyStudents");//打印表中所有数据
//        printAllRecordsByTable(connection, args[0]);//打印表中所有数据
        saveAllRecordsByTable(connection, args[0]);//保存表中所有数据到文件中
//		printRecordsByRowkey(connection, "MyStudents", args[0]);//打印表中指定行的数据
//		printRecordsByColumnFamily1(connection, "MyStudents", args[0]);//打印表中指定列族的数据
//		printRecordsByColumnFamily2(connection, "MyStudents", args[0]);//打印表中指定列族的数据

//		Table table = connection.getTable(TableName.valueOf("MyStudents"));
//		Scan scan = new Scan();
//		//以下两行addColumn是并集的关系
//		scan.addColumn(Bytes.toBytes("BasicInfo"), Bytes.toBytes("name"));
//		scan.addColumn(Bytes.toBytes("BasicInfo"), Bytes.toBytes("age"));
//		ResultScanner scanner = table.getScanner(scan);
//		printScanner(scanner);
//		table.close();

//		deleteTable(admin, "MyStaffs");//删除表

        connection.close();
    }

    /**
     * 删除表
     *
     * @param tableName
     * @param admin
     * @throws IOException
     */
    public static void deleteTable(Admin admin, String tableName) throws IOException {
        admin.disableTable(TableName.valueOf(tableName));//禁用表
        admin.deleteTable(TableName.valueOf(tableName));//删除表
        System.out.println("表" + tableName + "已成功删除！");
    }

    private static void printRecordsByFilter(Connection connection, Filter myFilter) {

    }

    /**
     * 根据指定的表名和列族名打印出表中的记录
     *
     * @param familyName
     * @param tableName
     * @param connection
     * @throws IOException
     */
    public static void printRecordsByColumnFamily2(Connection connection, String tableName, String familyName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
//		Scan scan = new Scan().addFamily(Bytes.toBytes(familyName));
        //下面两行代码等同于上面这一行代码
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(familyName));
        ResultScanner scanner = table.getScanner(scan);
        printScanner(scanner);
        table.close();//释放资源
    }

    /**
     * 根据指定的表名和列族名打印出表中的记录
     *
     * @param connection
     * @param tableName
     * @param ColumnFamily
     * @throws IOException
     */
    private static void printRecordsByColumnFamily1(Connection connection, String tableName, String ColumnFamily) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        ResultScanner scanner = table.getScanner(Bytes.toBytes(ColumnFamily));//获得列族扫描
        printScanner(scanner);//打印该扫描
        table.close();//释放资源
    }

    /**
     * 打印这个Scanner
     *
     * @param scanner
     */
    private static void printScanner(ResultScanner scanner) {
        for (Result allResult : scanner) {
            //allResult.getRow()的返回值是一个byte数组型的行键
            byte[] rowKey = allResult.getRow();//行键
            System.out.println("row key : " + Bytes.toString(rowKey));//打印row key
            Cell[] cells = allResult.rawCells();
            for (Cell cell : cells) {
                byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
                byte[] qualifier = CellUtil.cloneQualifier(cell);//限定词（可以理解为列名）
                byte[] value = CellUtil.cloneValue(cell);//值
                System.out.println(Bytes.toString(columnFamily) + ":" + Bytes.toString(qualifier) +
                        "\t" + Bytes.toString(value));
            }
        }
    }

    /**
     * 保存Scanner的所有行键、列族名到文件的第一行中，文件个数由行键个数决定
     *
     * @param scanner
     */
//    private void saveCsvHeader(ResultScanner scanner) {
//        //最终会利用rowkey_name、rowkey_to_columnFamilies、rowkey_to_qualifiers的数据导出为rowkey个文件
//        HashSet<String> rowkey_name = new HashSet<String>();
//        HashMap<String, HashSet<String>> rowkey_to_columnFamilies = new HashMap<String, HashSet<String>>();
//        HashMap<String, HashSet<String>> rowkey_to_qualifiers = new HashMap<String, HashSet<String>>();
//
//        Iterator<Result> iter = scanner.iterator();
//
//        if (!iter.hasNext()) {
//            return;
//        } else {
//            String prev_rowkey = "";
//            //先获取第一个行键的第一条信息
////            Result result1 = iter.next();
////            byte[] rk = result1.getRow();
////            rowkey_name.add(Bytes.toString(rk));
////            prev_rowkey = Bytes.toString(rk);
//            HashSet<String> family_for_rowkey = new HashSet<String>();
//            HashSet<String> qualifier_for_rowkey = new HashSet<String>();
////            Cell[] cells = result1.rawCells();
////            for (Cell cell : cells) {
////                byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
////                byte[] qualifier = CellUtil.cloneQualifier(cell);//列标志符（可以理解为列名）
////                family_for_rowkey.add(Bytes.toString(columnFamily));
////                qualifier_for_rowkey.add(Bytes.toString(qualifier));
////            }
////            rowkey_to_columnFamilies.put(prev_rowkey, family_for_rowkey);
////            rowkey_to_qualifiers.put(prev_rowkey, qualifier_for_rowkey);
//
//            //存储第1到n-1个行键内的所有信息
//            while (iter.hasNext()) {
//                Result result = iter.next();
//                byte[] rowKey = result.getRow();//得到此条数据对应的行键
//                if (rowkey_name.contains(Bytes.toString(rowKey))) {
//                    Cell[] many_cell = result.rawCells();
//                    for (Cell cell : many_cell) {
//                        byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
//                        byte[] qualifier = CellUtil.cloneQualifier(cell);//列标志符（可以理解为列名）
//                        family_for_rowkey.add(Bytes.toString(columnFamily));
//                        qualifier_for_rowkey.add(Bytes.toString(qualifier));
//                    }
//                } else {
//                    rowkey_to_columnFamilies.put(prev_rowkey, family_for_rowkey);
//                    rowkey_to_qualifiers.put(prev_rowkey, qualifier_for_rowkey);
//                    family_for_rowkey = new HashSet<String>();
//                    qualifier_for_rowkey = new HashSet<String>();
//                    rowkey_name.add(Bytes.toString(rowKey));
//                    Cell[] many_cell = result.rawCells();
//                    for (Cell cell : many_cell) {
//                        byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
//                        byte[] qualifier = CellUtil.cloneQualifier(cell);//列标志符（可以理解为列名）
//                        family_for_rowkey.add(Bytes.toString(columnFamily));
//                        qualifier_for_rowkey.add(Bytes.toString(qualifier));
//                    }
//                }
//            }
//
//            //存储最后一个行键对应的表
//            ...
//
//            //将得到的map存为多个文件
//            ...
//        }
//    }


    /**
     * 保存Scanner的数据到文件中
     *
     * @param scanner
     */
    private static void saveScanner(ResultScanner scanner, String tableName) {
        try {
            FileWriter fw = new FileWriter(tableName + ".txt");
            Iterator<Result> iter = scanner.iterator();
            while (iter.hasNext()) {
                Result result = iter.next();
                byte[] rowKey = result.getRow();//得到此条数据对应的行键
                StringBuilder result_string = new StringBuilder();
                result_string.append("rowkey\t");
                result_string.append(Bytes.toString(rowKey));
                result_string.append("\t");
                Cell[] cells = result.rawCells();
                for (Cell cell : cells) {
                    byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
                    byte[] qualifier = CellUtil.cloneQualifier(cell);//限定词（可以理解为列名）
                    byte[] value = CellUtil.cloneValue(cell);//值
                    result_string.append(Bytes.toString(columnFamily));
                    result_string.append(":");
                    result_string.append(Bytes.toString(qualifier));
                    result_string.append("\t");
                    result_string.append(Bytes.toString(value));
                    result_string.append("\t");
                }
                result_string.append("\n");
                fw.write(result_string.toString());
            }
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据指定的表名和行键打印出表中的记录
     *
     * @param connection
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    private static void printRecordsByRowkey(Connection connection, String tableName, String rowKey) throws IOException {
        System.out.println("开始打印表" + tableName + "中rowkey为" + rowKey + "的记录......");
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));//获取该行的Get对象
        if (!table.exists(get)) {
            System.out.println("row key : " + rowKey + "不存在！");
            System.exit(0);
        }
        Result oneRowResult = table.get(get);//根据行键获取一行数据
        Cell[] cells = oneRowResult.rawCells();//获取该行的Cell数组
        System.out.println("row key : " + rowKey);//打印row key
        for (Cell cell : cells) {
            byte[] columnFamily = CellUtil.cloneFamily(cell);//列族名
            byte[] qualifier = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);//值
            System.out.println(Bytes.toString(columnFamily) + ":" + Bytes.toString(qualifier) +
                    "\t" + Bytes.toString(value));
        }
        System.out.println("打印表" + tableName + "中rowkey为" + rowKey + "的记录！完成！");
        table.close();//释放资源
    }

    /**
     * 打印出一个HBase表里的所有记录
     *
     * @param connection
     * @param tableName
     * @throws IOException
     */
    private static void printAllRecordsByTable(Connection connection, String tableName) throws IOException {
        System.out.println("开始打印表" + tableName + "所有记录......");
        Table table = connection.getTable(TableName.valueOf(tableName));
        ResultScanner scanner = table.getScanner(new Scan());//获得全表扫描
        printScanner(scanner);//打印该扫描
        System.out.println("打印该表" + tableName + "所有记录！完成！");
        table.close();
    }

    private static void saveAllRecordsByTable(Connection connection, String tableName) throws IOException {
        System.out.println("开始保存表" + tableName + "所有记录......");
        Table table = connection.getTable(TableName.valueOf(tableName));
        ResultScanner scanner = table.getScanner(new Scan());//获得全表扫描
        saveScanner(scanner, tableName);//保存该扫描
        System.out.println("保存该表" + tableName + "所有记录！完成！");
        table.close();
    }

    /**
     * 该方法用于根据给定的表名和列族名，往指定的表中添加列族。
     * 列族名是可变参数
     * 该方法是一个可变参数列表方法。
     *
     * @param admin
     * @param tableName
     * @param columnFamilyNames
     * @throws IOException
     */
    public static void addColumnFamilys(Admin admin,
                                        String tableName, String... columnFamilyNames) throws IOException {
        for (int i = 0; i < columnFamilyNames.length; i++) {
            admin.addColumn(TableName.valueOf(tableName), new HColumnDescriptor(columnFamilyNames[i]));
        }
        System.out.println("列族添加成功！");
    }

    /**
     * 该方法用于根据给定的表名、行键、列族名、限定词、值添加一行数据
     *
     * @param connection
     * @throws IOException
     */
    public static void insertOneRow(Connection connection, String tableName,
                                    String rowKey, String columnFamily, String qualifier, String value) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        table.put(put);
        table.close();
        System.out.println("添加成功！");
    }

    /**
     * 该方法用于根据传入的表名和列族名创建一个表。
     * 列族名参数是可变参数。
     * 该方法是一个可变参数列表方法。
     *
     * @param admin
     * @param tableName
     * @throws IOException
     */
    public static void creatTableByName(Admin admin, String tableName, String... columnFamilyName) throws IOException {
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
        if (!admin.tableExists(TableName.valueOf(tableName))) {
            //该循环用于添加列族
            for (int i = 0; i < columnFamilyName.length; i++) {
                hTableDescriptor.addFamily(new HColumnDescriptor(columnFamilyName[i]));
            }
            admin.createTable(hTableDescriptor);
            System.out.println(tableName + "已成功创建！");
        } else {
            System.out.println("表已存在！");
            System.exit(0);
        }
    }

}
