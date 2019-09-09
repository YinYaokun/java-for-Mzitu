import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Response;

class DownloadImage implements Runnable
{
	private static int sum = 0;  //��¼��ҳ��
	private static int amount = 0;  //��¼��ǰ���������ص���ҳ��
	
	private int page = 0;  //��¼��ǰ�߳������ص�ҳ��
	private Document document = null;
	
	DownloadImage(int page, int su)
	{
		this.page = page;
		sum = su;
		
		if(page > amount)
			amount = page;
	}
	
	public void run()
	{
		while(amount <= sum)
		{
			download();
		}
	}
	
	public void download()
	{
		File file = new File("E://op//��ȡ//" + page + "//address.txt");
		
		String dataUrl = null;
		
		int count = 0;  //��¼��ͼ��Ŀ
		
		boolean flag = true;
		
		try
		{
			BufferedReader bufr = new BufferedReader(new FileReader(file));
			
			System.out.println("��" + page + "ҳͼ�����ؿ�ʼ...");
			
			while((dataUrl = bufr.readLine()) != null)
			{
				getConnect(dataUrl);
				
				File dir = new File("E://op//��ȡ//" + page + "//" + document.title());
				dir.mkdirs();
				
				int getPage = getDownloadPage();
				
				Thread.sleep(600);
				
				for(int num = 1; num <= getPage; num++)
				{
					File object = new File(dir, num + ".jpg");
						
					try
					{
						//�ۺ϶��ԣ���ȡ��ͼƬԴ���Ӻ�˯600���룬������ͼƬ����˯600���룬��������ȵģ��ù�֮ǰ��1500���룬��Ȼ����Լ��300���룬������ԭ���ĵ�1500����Ҫ��
						if(!object.exists())
						{
							if(num == 1)
							{
								String ss = getImageUrl();
								//Thread.sleep(1500);  //�����������pass
								Thread.sleep(600);
								startDownload(ss, object, num);
								Thread.sleep(600);
							}
							else
							{
								String ss = getImageUrl(num, dataUrl);
								//Thread.sleep(1500);  //�����������pass
								Thread.sleep(600);
								startDownload(ss, object, num);
								Thread.sleep(600);
							}
						}
						else
						{
							System.out.println(object + "---!��ͼƬ�Ѵ���!");
							//Thread.sleep(300);  //ԭ�������ع����У�������������ģ����ǣ������˯�µĻ�����������ᵼ��403
						}
					}
					
					catch(IOException e)
					{
						System.out.println("----------����----------");
						System.out.println("��ȡͼԴ���ӹ��̳������쳣!");
						//e.printStackTrace();
						Thread.sleep(600);
						String ss = getImageUrl(num, dataUrl);
						Thread.sleep(600);
						startDownload(ss, object, num);
						System.out.println("+++++�����������+++++");
					}
				}
				System.out.println(dir + ">>>����ͼ�������~");
				count++;
				//Thread.sleep(1000);  //�����������pass
			}
			System.out.println("��" + page + "ҳ������ϣ���ͼ��Ϊ��" + count);
			amount++;
			page = amount;
		}
		catch(Exception e)
		{
			System.out.println("���������������������쳣!!!");
			e.printStackTrace();
		}
	}
	
	public Document getConnect(String address)
	{
		try
		{
			document = Jsoup.connect(address).get();
			
			return document;
		}
		catch(IOException e)
		{
			System.out.println("��ͼԴ��ַ����ʧ��!");
			e.printStackTrace();
		}
		return null;
	}
	
	public int getDownloadPage()
	{
		Elements spans = document.select("span");
		
		int imagePage = 0;
		
		for(Element span : spans)
		{
			try
			{
				imagePage = Integer.parseInt(span.text());
				
				if(imagePage > 10)
					return imagePage;
			}
			catch(NumberFormatException e)
			{
				//������û��ǲ�Ҫ��ӡ�˰�
				//System.out.println("ץȡ��������Ķ���");
			}
		}
		
		return 0;
	}
	
	public String getImageUrl(int num, String url) throws Exception
	{
		url = url + "/" + num + "/";
		
		document = Jsoup.connect(url).get();
		Elements img = document.select("div.main-image").select("img");
		//Thread.sleep(500);
		return img.attr("src");
	}
	
	public String getImageUrl() throws Exception
	{
		Elements img = document.select("div.main-image").select("img");
		return img.attr("src");
	}
	
	public void startDownload(String downloadUrl, File fileName, int num) throws Exception
	{
		Response response = Jsoup.connect(downloadUrl)
			.ignoreContentType(true)
			.header("Referer", "https://i5.meizitu.net")
			.execute();
				
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(response.bodyAsBytes());
		out.close();
	}
}