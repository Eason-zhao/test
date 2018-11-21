package hadoop.hdfsOperate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IDEA
 * author:ZhaoYiShun
 * Date:2018/11/20
 * Time:13:44
 */
public class FileOperateTest {
    public static DistributedFileSystem dfs=new DistributedFileSystem();
    public static String nameNodeuri="hdfs://master:8020";

    public static void main(String[] args) throws Exception {
        FileOperateTest fot =new FileOperateTest();
        fot.initFileSystem();
        fot.testMkDir();
        fot.testDelDir();
        fot.testFileList();
    }
    //初始化连接
    public void initFileSystem() throws Exception {
        System.out.println("开始初始化客户端");
        //设置hadoop的登录名
        System.setProperty("HADOOP_USER_NAME", "hdfs");
        dfs.initialize(new URI(nameNodeuri),new Configuration());
        System.out.println("客户端连接成功");
        Path workingDirectory=dfs.getWorkingDirectory();
        System.out.println("工作目录为："+workingDirectory);

    }
    //创建文件夹
    public void testMkDir() throws IOException {
        boolean flag=dfs.mkdirs(new Path("/tmp/test/test"));
        System.out.println("目录创建结果:"+(flag?"创建成功":"创建失败"));
    }
    //删除目录文件
    public void testDelDir() throws IOException {
        dfs.delete(new Path("/tmp/test/test"),false);
    }
    //获得文件列表信息
    public void testFileList() throws IOException {
        RemoteIterator fileList=dfs.listFiles(new Path("/"),true);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        while (fileList.hasNext()){
            LocatedFileStatus fileStatus= (LocatedFileStatus) fileList.next();
            //权限
            FsPermission permission=fileStatus.getPermission();
            //拥有者
            String owner=fileStatus.getOwner();
            //组
            String group=fileStatus.getGroup();
            //文件大小
            long len=fileStatus.getLen();
            long modificationTime=fileStatus.getModificationTime();
            Path path=fileStatus.getPath();
            System.out.println("permission:"+permission);
            System.out.println("owner:"+owner);
            System.out.println("group:"+group);
            System.out.println("len:"+len);
            System.out.println("modificationTime:"+sdf.format(new Date(modificationTime)));
            System.out.println("path:"+path);
        }
    }
    //文件上传
    public void testUploadFullFile() throws IOException {
        FSDataOutputStream out=dfs.create(new Path("/uploadFile.txt"),true);
        FileInputStream in = new FileInputStream(FileOperateTest .class.getResource("uploadFile.txt").getFile());
        org.apache.commons.io.IOUtils.copy(in, out);

    }
    //下载文件
    public void testDownloadFile() throws IOException {
        dfs.copyToLocalFile(false,new Path("/uploadFullFile.txt"), new Path("E://test"),true);
        System.out.println("下载完成");

 }



}
