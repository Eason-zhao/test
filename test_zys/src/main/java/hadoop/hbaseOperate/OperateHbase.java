package hadoop.hbaseOperate;

import javafx.scene.control.Tab;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.File;
import java.io.IOException;

/**
 * Created with IDEA
 * author:ZhaoYiShun
 * Date:2018/11/20
 * Time:14:59
 */
public class OperateHbase {
    public static Configuration conf;
    public static Connection conn;
    public static Admin admin;

    public static void main(String[] args) {

    }

    //初始化连接
    public void init(){
        conf=new Configuration();
        conf.set("hbase.master","192.168.2.16:9000");
        try {
            conn=ConnectionFactory.createConnection(conf);
            admin=conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //关闭连接
    public void close(){
        try {
        if(admin !=null){
                admin.close();
            }
         if(conn !=null){
            conn.close();
         }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    //建表
    public void createTable(String tableName,String[] cols) throws IOException {
        TableName tn=TableName.valueOf(tableName);
        if(admin.tableExists(tn)){
            System.out.println("该表已经存在:"+tn);
        }else {
            HTableDescriptor hTableDescriptor=new HTableDescriptor(tn);
            for(String col:cols){
                HColumnDescriptor hColumnDescriptor=new HColumnDescriptor(col);
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
        }
        close();
    }
    //删表
    public void delTable(String tableName) throws IOException {
        TableName tn=TableName.valueOf(tableName);
        if(admin.tableExists(tn)){
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        close();
    }
    //获得表信息
    public void listTables() throws IOException {
        HTableDescriptor[] hTableDescriptor=admin.listTables();
        for(HTableDescriptor ht:hTableDescriptor){
            System.out.println("存在表:"+ht.getNameAsString());
        }
    }
    //插入数据
    public void insetRow(String tableName,String rowkey,String colFamily,String col,String value) throws IOException {
        Table table=conn.getTable(TableName.valueOf(tableName));
        Put put=new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col),Bytes.toBytes(value));
        table.put(put);
        table.close();
    }
    //删除数据
    public void delRow(String tableName,String rowkey,String colFamily,String col,String value) throws IOException {
        Table table=conn.getTable(TableName.valueOf(tableName));
        Delete delete=new Delete(Bytes.toBytes(rowkey));
        table.delete(delete);
        table.close();
    }
    //按照rowkey查询
    public void getData(String tableName,String rowkey,String colFamily,String col) throws IOException {
        Table table=conn.getTable(TableName.valueOf(tableName));
        Get get=new Get(Bytes.toBytes(rowkey));
        // 获取指定列族数据
        // get.addFamily(Bytes.toBytes(colFamily));
        // 获取指定列数据
        // get.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
        Result result=table.get(get);
        showcell(result);
        table.close();

    }
    //格式化输出
    public void showcell(Result result){
        Cell[] cells=result.rawCells();
        for(Cell cell:cells){
            System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.println("Timetamp:" + cell.getTimestamp() + " ");
            System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
        }
    }
    //批量查找数据
    public void scanData(String tableName, String startRow, String stopRow) throws IOException {
        Table table=conn.getTable(TableName.valueOf(tableName));
        Scan scan=new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        ResultScanner results=table.getScanner(scan);
        for(Result result:results){
            showcell(result);
        }
        table.close();
    }



}
