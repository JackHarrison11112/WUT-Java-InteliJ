package com.qst.dms.entity;
import java.io.*;
import java.io.File;
public class AppendObjectOutputStream extends ObjectOutputStream {
	public  static File file=null;
	public AppendObjectOutputStream (File file)throws IOException{
		super(new FileOutputStream(file,true));
	}
	public void writeStreamHeader() throws IOException{
		if(file==null)super.writeStreamHeader();//文件不存在
		else {
			if(file.length()==0)super.writeStreamHeader();//文件为空，写入头信息
			else this.reset();//文件不为空，不要再写头信息
		}
	}

}
