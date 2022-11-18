package com.qst.dms.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.server.ServerNotActiveException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import java.io.EOFException;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.AppendObjectOutputStream;
import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.entity.Transport;

public class TransportService {
	// 物流数据采集
	DBUtil db=new DBUtil();
	public Transport inputTransport() {
		Transport trans = null;

		// 建立一个从键盘接收数据的扫描器
		Scanner scanner = new Scanner(System.in);
		try {
			// 提示用户输入ID标识
			System.out.println("请输入ID标识：");
			// 接收键盘输入的整数
			int id = scanner.nextInt();
			// 获取当前系统时间
			Date nowDate = new Date();
			// 提示用户输入地址
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			String address = scanner.next();
			// 数据状态是“采集”
			int type = DataBase.GATHER;

			// 提示用户输入登录用户名
			System.out.println("请输入货物经手人：");
			// 接收键盘输入的字符串信息
			String handler = scanner.next();
			// 提示用户输入主机IP
			System.out.println("请输入 收货人:");
			// 接收键盘输入的字符串信息
			String reciver = scanner.next();
			// 提示用于输入物流状态
			System.out.println("请输入物流状态：1发货中，2送货中，3已签收");
			// 接收物流状态
			int transportType = scanner.nextInt();
			// 创建物流信息对象
			trans = new Transport(id, nowDate, address, type, handler, reciver,
					transportType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回物流对象
		return trans;
	}

	// 物流信息输出
	public void showTransport(Transport... transports) {
		for (Transport e : transports) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配的物流信息输出，可变参数
	public void showMatchTransport(MatchedTransport... matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配的物流信息输出，参数是集合
	public void showMatchTransport(ArrayList<MatchedTransport> matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配物流信息保存，参数是集合
	public void saveMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// 创建一个ObjectOutputStream对象输出流，并连接文件输出流
		// 以可追加的方式创建文件输出流，数据保存到MatchedTransports.txt文件中
		File file=new File("./MatchedTransports.txt");
		try (ObjectOutputStream obs = new ObjectOutputStream(
				new FileOutputStream(file))) {
			// 循环保存对象数据
			for (MatchedTransport e : matchTrans) {
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
	public void saveAppendedMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// 创建一个ObjectOutputStream对象输出流，并连接文件输出流
		// 以可追加的方式创建文件输出流，数据保存到MatchedTransports.txt文件中
		File file=new File("./MatchedTransports.txt");
		AppendObjectOutputStream ops=null;
		//(ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(file)))			
		try  {
			AppendObjectOutputStream.file=file;
			ops=new AppendObjectOutputStream(file);
			// 循环保存对象数据
			for (MatchedTransport e : matchTrans) {
				if (e != null) {
					// 把对象写入到文件中
					ops.writeObject(e);
					ops.flush();
				}
			}
			// 文件末尾保存一个null对象，代表文件结束
			ops.writeObject(null);
			ops.flush();
		} catch (Exception ex) {ex.printStackTrace();}
		/*finally {
			if(ops!=null) {
				try{ops.close();}catch(IOException e) {e.printStackTrace();}
			}
		}*/
			
	}
	

	// 读匹配物流信息保存，参数是集合
	public ArrayList<MatchedTransport> readMatchedTransport() {
		ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
		// 创建一个ObjectInputStream对象输入流，并连接文件输入流，读MatchedTransports.txt文件中
			MatchedTransport matchTran;
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./MatchedTransports.txt"))){
			// 循环读文件中的对象
			while (true) {
				try {
				matchTran = (MatchedTransport) ois.readObject();
				// 将对象添加到泛型集合中
				matchTrans.add(matchTran);
				} catch (EOFException ex) {break;}
			}
			}catch(Exception e) {e.printStackTrace();}
		
		return matchTrans;
	}
	public void saveMatchTransportToDB(ArrayList<MatchedTransport> matchTrans){

		String sql1="insert into gather_transport (id,time,address,type,handler,reciver,transporttype) values(?,?,?,?,?,?,?)," +
				"(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";//插入总运输数据库语句
		String sql2="insert into matched_transport(sendid,transid,receiveid) values(?,?,?);";
		try {
			db.getConnection();
			for(MatchedTransport match:matchTrans){
				Transport send=match.getSend();
				Transport trans=match.getTrans();
				Transport receive=match.getReceive();
				db.executeUpdate(sql1,new Object[]{send.getId(),send.getTime(),send.getAddress(),send.getType(),send.getHandler(),
						send.getReciver(),send.getTransportType(),trans.getId(),trans.getTime(),trans.getAddress(),trans.getType(),trans.getHandler(),
						trans.getReciver(),trans.getTransportType(),receive.getId(),receive.getTime(),receive.getAddress(),receive.getType(),receive.getHandler(),
						receive.getReciver(),receive.getTransportType()
				});
				db.executeUpdate(sql2,new Object[]{send.getId(),trans.getId(),receive.getId()});
			}
			db.closeAll();
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	public ArrayList<MatchedTransport> readMatchedTransportFromDB(){
		ArrayList<MatchedTransport> transportList=new ArrayList<MatchedTransport>();
		String sql="select gt.id,gt.`time` ,gt.address ,gt.`type` ,gt.handler ,gt.reciver ,gt.transporttype ,gt1.id,gt1.`time` ,gt1.address ,gt1.`type`,gt1.handler ,gt1.reciver ,gt1.transporttype" +
				",gt2.id,gt2.`time` ,gt2.address ,gt2.`type` ,gt2.handler ,gt2.reciver ,gt2.transporttype " +
				"from gather_transport gt,gather_transport gt1,gather_transport gt2,matched_transport m where gt.id=m.sendid and gt1.id=m.transid and gt2.id=m.receiveid ;";
		try{
			db.getConnection();
			ResultSet rs=db.executeQuery(sql,null);
			while(rs.next()){
				Transport send=new Transport(rs.getInt(1),rs.getTime(2),rs.getString(3),
						rs.getInt(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				Transport trans=new Transport(rs.getInt(8),rs.getTime(9),rs.getString(10),
						rs.getInt(11),rs.getString(12),rs.getString(13),rs.getInt(14));
				Transport receive=new Transport(rs.getInt(15),rs.getTime(16),rs.getString(17),
						rs.getInt(18),rs.getString(19),rs.getString(20),rs.getInt(21));
				transportList.add(new MatchedTransport(send,trans,receive));
			}
			db.closeAll();
		}catch (Exception e){e.printStackTrace();}

		return transportList;
	}
}