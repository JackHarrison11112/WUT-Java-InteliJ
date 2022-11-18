package com.qst.dms.service;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.AppendObjectOutputStream;
import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.LogRec;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
//日志业务类
public class LogRecService {
	//ArrayList<MatchedLogRec> MatchedLogs;
	// 日志数据采集
	//DataBase db;
	//int recnum;
	DBUtil du=new DBUtil();
	public LogRec inputLog() {
		LogRec log = null;
		// 建立一个从键盘接收数据的扫描器
		Scanner scanner = new Scanner(System.in);
		try {
			// 提示用户输入ID标识
			System.out.println("请输入ID标识：");
			// 接收键盘输入的整数
			int id=scanner.nextInt();
			// 获取当前系统时间
			Date nowDate=new Date();
			// 提示用户输入地址
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			String address=scanner.next();
			// 数据状态是“采集”
			int type = DataBase.GATHER;
			// 提示用户输入登录用户名
			System.out.println("请输入用户名：");
			// 接收键盘输入的字符串信息
			String user=scanner.next()
;			// 提示用户输入主机IP
			System.out.println("请输入IP：");
			// 接收键盘输入的字符串信息
			String ip=scanner.next();
			// 提示用户输入登录状态、登出状态
			System.out.println("请输入登录状态:1是登录，0是登出");
			int logType = scanner.nextInt();
			// 创建日志对象
			log = new LogRec(id, nowDate, address, type, user, ip, logType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回日志对象
		return log;
	}

	// 日志信息输出
	public void showLog(LogRec... logRecs) {
		for (LogRec e : logRecs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出，可变参数
	public void showMatchLog(MatchedLogRec... matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出,参数是集合
	public void showMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}
	public void saveMatchLog(ArrayList<MatchedLogRec> matchLogrecs) {
		// 创建一个ObjectOutputStream对象输出流，并连接文件输出流
		// 以可追加的方式创建文件输出流，数据保存到MatchedTransports.txt文件中
		File file=new File("./MatchedLogRecs.txt");
		try (AppendObjectOutputStream obs = new AppendObjectOutputStream(file)) {
			//创建AppendStream对象
			// 循环保存对象数据
			for (MatchedLogRec e : matchLogrecs) {
				if (e != null) {
					// 把对象写入到文件中
					obs.writeObject(e);
					obs.flush();
				}
			}
			// 文件末尾保存一个null对象，代表文件结束
			obs.writeObject(null);
			obs.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void saveAppendMatchLog(ArrayList<MatchedLogRec> matchLogrecs) {
		// 创建一个ObjectOutputStream对象输出流，并连接文件输出流
		// 以可追加的方式创建文件输出流，数据保存到MatchedTransports.txt文件中
		AppendObjectOutputStream obs=null;
		File file=new File("./MatchedLogRecs.txt");
		try {
			//创建AppendStream对象
			AppendObjectOutputStream.file=file;//这一句的作用是什么呢？可不可以省略
			obs=new AppendObjectOutputStream(file);
			//obs.setfile(file);
			// 循环保存对象数据
			for (MatchedLogRec e : matchLogrecs) {
				if (e != null) {
					// 把对象写入到文件中
					obs.writeObject(e);
					obs.flush();
				}
			}
		} catch (Exception ex) {			
		}finally {
			if(obs!=null) {
				try {obs.close();}catch(IOException e) {e.printStackTrace();}
			}
		}
	}
	public ArrayList<MatchedLogRec> readMatchLog(){
		ArrayList<MatchedLogRec> matchLogs = new ArrayList<>();
		// 创建一个ObjectInputStream对象输入流，并连接文件输入流，读MatchedTransports.txt文件中
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"./MatchedLogRecs.txt"))) {
			MatchedLogRec matchLog;
			// 循环读文件中的对象
			/*
			while((matchLog = (MatchedLogRec) ois.readObject())!=null) {
				matchLogs.add(matchLog);
			}//存在EOF报错无法解决*/
			//循环发，取到EOF错误则退出循环
			while (true) {
				try {
					// 将对象添加到泛型集合中
					matchLog = (MatchedLogRec) ois.readObject();
					matchLogs.add(matchLog);
				}catch(EOFException ef) {break;}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matchLogs;
	}
	public void saveMatchLogToDB(ArrayList<MatchedLogRec> matchLogs) {
		try{
		du.getConnection();
			//int[] mlogid=new int[]{matchLogs.get(i).getLogin().getId(),matchLogs.get(i).getLogout().getId()}
			String sql1 = "insert into gather_logrec(id,time,address,type,username,ip,logtype) values(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";
			String sql2 = "insert into matched_logrec(loginid,logoutid) values(?,?);";
		for(MatchedLogRec r:matchLogs){
			//String sql1 = "insert into gather_logrec values(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";
			LogRec in=r.getLogin();
			LogRec out=r.getLogout();
			du.executeUpdate(sql1,new Object[]{in.getId(),in.getTime(),in.getAddress(),in.getType(),in.getUser(),in.getIp(),
					in.getLogType(),out.getId(),out.getTime(),out.getAddress(),out.getType(),out.getUser(),out.getIp(),out.getLogType()});
			//du.executeUpdate(sql1,new Object[]{});
			du.executeUpdate(sql2,new Object[]{in.getId(),out.getId()});
		}
			du.closeAll();
		}catch(Exception e){e.printStackTrace();}
	}
	public ArrayList<MatchedLogRec> readMatchedLogFromDB(){
		ArrayList<MatchedLogRec> logList=new ArrayList<MatchedLogRec>();
		String sql2="select i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype from matched_logrec m,gather_logrec i, gather_logrec o where i.id=m.loginid and o.id=m.logoutid;";
		try{
			du.getConnection();
			ResultSet rs=du.executeQuery(sql2,null);
			while(rs.next()){
				LogRec login=new LogRec(rs.getInt(1),rs.getDate(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				LogRec logout=new LogRec(rs.getInt(8),rs.getDate(9),
						rs.getString(10),rs.getInt(11),rs.getString(12),
						rs.getString(13),rs.getInt(14));
				logList.add(new MatchedLogRec(login,logout));
			}
			du.closeAll();
		}catch (Exception e){e.printStackTrace();};
		return logList;
	}
}
	