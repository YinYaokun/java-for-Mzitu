import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class WriteURL implements Runnable
{
	private static int num = 0;
	
	WriteURL(int num)
	{
		this.num = num;
	}
	
	public void run()
	{
		File parent = null;
		File file = null;
		BufferedWriter bufw = null;
		String url = null;
		String writeUrl = null;
		Document document = null;
		
		for(int x = 1; x <= num; x++)
		{
			try
			{
				url = "https://www.mzitu.com/page/" + x + "/";
				
				parent = new File("E://op//��ȡ//" + x);
				file = new File(parent, "address.txt");
				
				if(!file.exists())
				{
					parent.mkdirs();
					
					bufw = new BufferedWriter(new FileWriter(file));
					
					parent.mkdir();
					
					System.out.println("����ץȡ��" + x + "ҳ��ͼԴ��ַ...");
					
					document = Jsoup.connect(url).get();
					Elements lis = document.select("#pins").select("li");
					
					for(Element li : lis)
					{
						writeUrl = li.select("a").attr("href");
						bufw.write(writeUrl);
						bufw.newLine();
						bufw.flush();
					}
					
					bufw.flush();
					System.out.println("��" + x + "ҳͼԴ��ַץȡ���!");
					
					if(x % 2 == 0)
						Thread.sleep(1000);  //��ͣһ�£��������з�����
				}
				else
					System.out.println("��" + x + "ҳ��ͼԴ��ַ�Ѵ���!");
			}
			catch(Exception e)
			{
				System.out.println("ͼԴ��ַץȡ�����쳣!!!");
				e.printStackTrace();
			}
			finally
			{
				if(bufw != null)
					try
					{
						bufw.close();
					}
					catch(IOException e)
					{
						System.out.println("��ץȡͼԴ��ַ������ʱ�������쳣!");
						e.printStackTrace();
					}
					
				Mz.flag = false;
			}
		}
	}
}