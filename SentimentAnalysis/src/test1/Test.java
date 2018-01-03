package test1;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import com.sun.jna.NativeLong;

import java.util.*;

import classify.classifier.ClassifierLibrary; //classify
import cluster.cluster.ClibraryCluster; //cluster
//import deepClassifier.deepClassify.DeepClassifierLibrary; //deepClassify
import com.lingjoin.deepClassify.DeepClassifierLibrary;
import com.lingjoin.fileutil.FileOperateUtils;

import docExtractor.docExtract.DocExtractLibray; //doxExtractor
import keyExtractor.keyExtractor.CLibraryKeyExtractor; //keyExtractor
import nlpir.*; //nlpir--分词
import nlpir.demo.NlpirMethod;
import sentiment.*; //sentiment
import sentiment.sentimentAnalysis.LJSentimentAnalysisLibrary.CLibrarySentimentAnalysis;
import summary.*; //summary
import summary.summary.CLibraryDS;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Test {
	static String str = "";

	public static void main(String[] args) throws Exception {
		// 选择-->输出
		str = getFileWords();
		System.out.println(str);

		int tag = 0;
		String[] names = { "1:Classify", "2:Cluster", "3:DeepClassifier", "4:DocExtractor", "5:KeyExtractor", "6:NLPIR",
				"7:Sentiment", "8:Summary", "9:Exit" };
		System.out.println("Choose your output: ");
		for (int i = 0; i < 9; i++) {
			System.out.print(names[i] + "; ");
		}
		try {
			do {
				System.out.println("\n" + "Please input the correct number: ");
				Scanner sc = new Scanner(System.in);
				tag = Integer.parseInt(sc.nextLine());
			} while( (tag > 9) || (tag <= 0) );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// String choice = Integer.toString(tag);
		System.out.println("You have choosed: " + names[tag - 1]);
		
		while (tag <= 9) {
			// tag=1 --> classify
			if (tag == 1) {
				System.out.println("Classify: ");
				System.out.println("输出结果是:" + classify(str) + "\n");
			}
			// tag=2 --> cluster
			else if (tag == 2) {
				System.out.println("Cluster: ");

				cluster(str);
			}
			// tag=3 --> deepClassifier
			else if (tag == 3) {
				System.out.println("DeepClassifier: ");
				System.out.println("输出结果是:" + deepClassify(str) + "\n");
			}
			// tag=4 --> docExtractor
			else if (tag == 4) {
				System.out.println("DocExtractor: ");
				docExtractor(str);
			}
			// tag=5 --> keyExtractor
			else if (tag == 5) {
				System.out.println("KeyExtractor: ");
				System.out.println("输出结果是:" + keyExtractor(str) + "\n");
			}
			// tag=6 --> nlpir
			else if (tag == 6) {
				System.out.println("NLPIR: ");
				nlpir(str);
			}
			// tag=7 --> sentiment
			else if (tag == 7) {
				System.out.println("Sentiment: ");
				sentiment(str);
			}
			// tag=8 --> summary
			else if (tag == 8) {
				System.out.println("Summary: ");
				summary(str);
			}
			// tag=9 --> exit
			else if (tag == 9) {
				System.out.println("EXIT!");
				break;
			}else {
				System.out.println("Please choose the right one!");
			}
			for (int i = 0; i < 9; i++) {
				System.out.print(names[i] + "; ");
			}
			System.out.println();
			System.out.println("Please input the other number: ");
			Scanner sc = new Scanner(System.in);
			tag = Integer.parseInt(sc.nextLine());
			while((tag <= 0) || (tag > 9)) {
				System.out.println("请输入正确的数字: ");
				tag = Integer.parseInt(sc.nextLine());
			}
			System.out.println("You have choosed: " + names[tag - 1]);
		}
	}

	// 请求输入文字消息
	// 使用 System.in 创建 BufferedReader
	public static String getFileWords() throws IOException {
		String content = FileOperateUtils.getFileContent("./TestTT/test3.txt", "utf-8");
		return (content);
	}

	// **********方法实现**********//
	// ***classify***//
	// 只有0军事和1政治 根据rulelist里面的文件
	public static String classify(String str) {
		boolean classify_flag = ClassifierLibrary.Instance.classifier_init("./ruleFile/rulelist.xml",
				"./AllData/Classify/DataFile");
		if (!classify_flag) {
			System.out.println("classify-初始化失败！\n");
			return ("false");
		}
		System.out.print("classify-初始化状态：" + (classify_flag == true ? "成功\n\n" : "失败\n\n"));
		String title = "1";
		String content = str;
		String classify_res = ClassifierLibrary.Instance.classifier_exec(title, content, 1);
		ClassifierLibrary.Instance.classifier_exit();
		return (classify_res);
	}

	// ***cluster***//
	public static void cluster(String str) {
		System.out.println("cluster-初始化开始....");
		boolean cluster_flag = ClibraryCluster.Instance.CLUS_Init("", "", 1);
		if (cluster_flag) {
			System.out.println("cluster-初始化成功....");
		} else {
			System.out.println("cluster-初始化失败....");
			System.out.println(ClibraryCluster.Instance.CLUS_GetLastErrMsg());
			System.exit(1);
		}
		ClibraryCluster.Instance.CLUS_SetParameter(500, 200);
		for (int i = 0; i < 7; i++) {
			String content = str;
			ClibraryCluster.Instance.CLUS_AddContent(content, "文章" + i);
		}
		ClibraryCluster.Instance.CLUS_GetLatestResult("ClusterResultByContentnew.xml");
		ClibraryCluster.Instance.CLUS_Exit();
		// 打印XML文件中的结果到Console
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(
					"C:\\software\\eclipse\\eclipse_workspace\\SentimentAnalysis\\ClusterResultByContentnew.xml");
			NodeList featureList = document.getElementsByTagName("feature");
			System.out.println("输出结果是:");
			System.out.println(featureList.item(0).getTextContent());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ***deepClassifier***//
	public static String deepClassify(String str) throws IOException {
		DeepClassifierLibrary.Instance.DC_Exit();
		// 1、训练过程--初始化
		boolean flag = DeepClassifierLibrary.Instance.DC_Init("", 1, 800, "");
		if (flag) {
			System.out.println("deepClassifier初始化成功");
		} else {
			System.out.println("deepClassifier初始化失败：" + DeepClassifierLibrary.Instance.DC_GetLastErrorMsg());
			System.exit(1);
		}
		// 2、训练过程--遍历训练分类文本的文件夹，添加所有的训练分类文本
		ArrayList list = FileOperateUtils.getAllFilesPath(new File("训练分类用文本"));
		for (int i = 0; i < list.size(); i++) {
			File f = new File(list.get(i).toString());
			String className = f.getParent();
			className = className.substring(className.lastIndexOf("\\") + 1);
			// 将训练分类文本加载到内存中
			String contentText = FileUtils.readFileToString(f, "utf-8");
			DeepClassifierLibrary.Instance.DC_AddTrain(className, contentText);
		}
		// 3、训练过程--开始训练
		DeepClassifierLibrary.Instance.DC_Train();
		// 4、训练过程--训练结束，退出
		DeepClassifierLibrary.Instance.DC_Exit();

		// 1、分类过程--初始化
		if (DeepClassifierLibrary.Instance.DC_Init("", 1, 800, "")) {
			System.out.println("train-deepClassifier初始化成功");
		} else {
			System.out.println("deepClassifier初始化失败：" + DeepClassifierLibrary.Instance.DC_GetLastErrorMsg());
			System.exit(1);
		}
		// 2、分类过程--加载训练结果
		DeepClassifierLibrary.Instance.DC_LoadTrainResult();
		// 3、分类过程--读取待分类的文本
		String result = DeepClassifierLibrary.Instance.DC_ClassifyFile("./TestTT/test3.txt");
//		String content = str;
		// String content = FileOperateUtils.getFileContent("./Test/8212.txt", "utf-8");
		System.out.println("load success");
//		System.out.println(content);
		// 4、分类过程--输出分类结果
		// System.out.println("分类结果：" +
		// DeepClassifierLibrary.Instance.DC_Classify(content));
//		String result = DeepClassifierLibrary.Instance.DC_Classify(content);
		// System.out.println(result);
		// 5、分类过程--退出
		DeepClassifierLibrary.Instance.DC_Exit();
		return (result);
	}

	// ***docExtracotr***//
	public static void docExtractor(String str) {
		if (DocExtractLibray.Instance.DE_Init("", 1, "") == 0) {
			System.out.println("初始化失败：" + DocExtractLibray.Instance.DE_GetLastErrMsg());
			System.exit(1);
		}
		System.out.println("初始化成功");
		String content = str;
		int score = DocExtractLibray.Instance.DE_ComputeSentimentDoc(content);
		System.out.println("--->score--->" + score);
		NativeLong handle = DocExtractLibray.Instance.DE_ParseDocE(content, "mgc#ngd", true,
				DocExtractLibray.ALL_REQUIRED);
		System.out.println(
				"抽取的人名为-->" + DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_PERSON));
		System.out.println("抽取的地名为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_LOCATION));
		System.out.println("抽取的机构名为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_ORGANIZATION));
		System.out.println("抽取的关键词为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_KEYWORD));
		System.out.println("抽取的文章作者为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_AUTHOR));
		System.out.println(
				"抽取的媒体为-->" + DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_MEDIA));
		System.out.println("抽取的文章对应的所在国别为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_COUNTRY));
		System.out.println("抽取的文章对应的所在省份为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_PROVINCE));
		System.out.println("抽取的文章摘要为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_ABSTRACT));
		System.out.println("输出文章的正面情感词为-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_POSITIVE));
		System.out.println("输出文章的副面情感词-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_NEGATIVE));
		System.out.println("输出文章原文-->" + content);
		System.out.println("输出文章去除网页等标签后的正文-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_DEL_HTML));
		System.out.println(
				"去除空格:" + DocExtractLibray.Instance.DE_GetResult(handle, 11).replaceAll("[　*| *| *|//s*]*", ""));
		System.out.println("自定义词(mgc)-->"
				+ DocExtractLibray.Instance.DE_GetResult(handle, DocExtractLibray.DOC_EXTRACT_TYPE_USER_DEFINED + 1));
		System.out.println("情感值---->" + DocExtractLibray.Instance.DE_GetSentimentScore(handle));
		DocExtractLibray.Instance.DE_ReleaseHandle(handle);
		System.out.println("是否安全退出-->" + DocExtractLibray.Instance.DE_Exit());
	}

	// ***keyExtractor***//
	public static String keyExtractor(String str) {
		if (CLibraryKeyExtractor.instance.KeyExtract_Init("", 1, "")) {
			System.out.println("KeyExtractor初始化成功");
		} else {
			System.out.println("KeyExtractor初始化失败");
			System.exit(-1);
		}
		String content = str;
		String keyWordsStr = CLibraryKeyExtractor.instance.KeyExtract_GetKeyWords(content, 10, true);
		CLibraryKeyExtractor.instance.KeyExtract_Exit();
		return (keyWordsStr);
	}

	// ***nlpir***//
	public static void nlpir(String str) throws IOException {
		// 文本分词
		String content = str;
		String result = NlpirMethod.NLPIR_ParagraphProcess(content, 1);
		System.out.println("文本分词:");
		System.out.println(result + "\n");
		// 文本关键词:
		result = NlpirMethod.NLPIR_GetKeyWords(content, 100, true);
		System.out.println("文本关键词:");
		System.out.println(result + "\n");
		// 添加用户自定义词:
		String newWord = "";
		System.out.print("是否需要添加自定义词(y or n): ");
		String choice1 = "";
		BufferedReader br_choice1 = new BufferedReader(new InputStreamReader(System.in));
		choice1 = br_choice1.readLine();
		if (choice1.equals("y")) {
			System.out.print("请添加自定义词: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			newWord = br.readLine();
			int flag1 = NlpirMethod.NLPIR_AddUserWord(newWord);
			System.out.println("添加新词状态：" + flag1);
			int flag2 = NlpirMethod.NLPIR_SaveTheUsrDic();
			System.out.println("存储新词状态：" + flag2);
			result = NlpirMethod.NLPIR_ParagraphProcess(content, 1);
			System.out.println("添加用户自定义词后结果:");
			System.out.println(result + "\n");
		} else {
			System.out.println("用户未进行添加操作！");
		}
		// 删除用户自定义词
		System.out.print("是否需要删除自定义词(y or n): ");
		String choice2 = "";
		BufferedReader br_choice2 = new BufferedReader(new InputStreamReader(System.in));
		choice2 = br_choice2.readLine();
		if (choice2.equals("y")) {
			String deleteWord = "";
			System.out.print("删除自定义词: ");
			BufferedReader br_delete = new BufferedReader(new InputStreamReader(System.in));
			deleteWord = br_delete.readLine();
			int flag3 = NlpirMethod.NLPIR_DelUsrWord(deleteWord);
			System.out.println("删除自定义词状态:" + flag3);
			int flag4 = NlpirMethod.NLPIR_SaveTheUsrDic();
			System.out.println("保存删除结果状态:" + flag4);
			result = NlpirMethod.NLPIR_ParagraphProcess(content, 1);
			System.out.println("删除自定义词后结果:");
			System.out.println(result + "\n");
		} else {
			System.out.println("用户未进行删词操作！");
		}
		// 文本词频
		result = NlpirMethod.NLPIR_WordFreqStat(content);
		System.out.println("文本词频:");
		System.out.println(result + "\n");
	}

	//***情感分析***//
	public static void sentiment(String str) {
		// 初始化
		int flag = CLibrarySentimentAnalysis.Instance.LJST_Inits("sentimentAnalysisData", 1, "");
		if (flag == 0) {
			System.out.println("SentimentAnalysis初始化失败");
			System.exit(0);
		} else {
			System.out.println("SentimentAnalysis初始化成功");
		}
		// 根据内容获得情感分析
		byte[] resultByte = new byte[10000];
		String content = str;
		CLibrarySentimentAnalysis.Instance.LJST_GetParagraphSent(content, resultByte);
		System.out.println("根据文本内容分析结果：\n" + new String(resultByte));// 输出分析结果
		// 退出
		CLibrarySentimentAnalysis.Instance.LJST_Exits();
	}
	//***摘要***//
	public static void summary(String str) {
		// 初始化
		boolean flag = CLibraryDS.Instance.DS_Init("", 1, "0");
		if (flag == false) {// 如果初始化失败，就打印出失败原因
			System.out.println(CLibraryDS.Instance.DS_GetLastErrMsg());
		}
		String content = str;
		System.out.println("原内容： " + content);
		System.out.println("原内容长度： " + content.length());
		String summary = CLibraryDS.Instance.DS_SingleDoc(content, 0, 100, true);
		System.out.println("摘要为： " + summary);
		System.out.println("现内容长度： " + summary.length());
		CLibraryDS.Instance.DS_Exit();
	}
} // public class()