package test1;

import java.io.*;

public class testsynax {
	// 使用 BufferedReader 在控制台读取字符
  public static void main(String args[]) throws IOException
  {
    // 使用 System.in 创建 BufferedReader 
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String str;
    System.out.println("Enter lines of text.");
    str = br.readLine();
    System.out.println(str);   
  }
}
