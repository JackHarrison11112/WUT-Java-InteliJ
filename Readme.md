## 文件框架

![](D:\编程实验\java多线程实验\image-20221107212925250.png)

## 基于Java SE 的数据挖掘系统

基于客户端服务器端（Client-Server，C-S）模式，实现日志与物流数据信息的采集、匹配、保存、显示等功能，为数据分析挖掘提供基础支撑。

#### 界面显示

![image-20221107213206960](C:\Users\yjx\AppData\Roaming\Typora\typora-user-images\image-20221107213206960.png)

#### 服务器端









#### 客户端



#### 

#### 文件存储

使用ObjectOutPutStream类对写入对象实现序列化，导入文件中

![image-20221109092752389](C:\Users\yjx\AppData\Roaming\Typora\typora-user-images\image-20221109092752389.png)

但是每一次导入都会调用writestreamheader（）方法写上头信息，而文件读取都是从头信息读取，因此多个头信息会导致读取文件出错，因此需要创建子类Appendoutputstream继承ObjectOutPutStream类并改写writestreamheader（）方法，如果文件不为空就不写入头信息

具体实现：

```java
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
```

##### 为什么要定义file为静态成员？

因为调用构造函数时就需要调用writeStreamHeader方法写头信息，因此我们的file需要在对象实例化之前就赋值，所以使用静态成员是一个方法

##### 静态成员

**static**表示属于类，不必创建对象就可以使用，因为常量应该不依赖任何对象，**final**表示值不能改变。一般用作常量的静态成员访问权限都设置为public，因为常量应该允许所有类或对象访问。另外需要注意的是，static可以与其他修饰符组合使用，且顺序可以任意调换。

在Java中，静态成员变量的的**初始化要求在静态语句块结束之前必须完成**，即Java中静态成员变量的初始化时机有两个，在声明的同时进行初始化或在静态语句中初始化。

静态成员是属于类的，因此对其访问不需要创建对象，可以使用**<类名> . <静态成员名>**的语法调用静态成员变量。

非静态方法访问静态成员的时候，规则就比较简单，非静态成员的生命周期被静态成员所包含，因此非静态成员存在的时候，静态成员一定存在，故非静态成员任何时候都可以访问静态成员。

#### 数据库

文件存储--->数据库







## 运行方式





## 数据结构









## 