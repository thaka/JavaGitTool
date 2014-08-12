package javaGitTool;

public class JavaGitToolMain {
	
	enum FuntionType {
        GITRetrieve,
        GITCheckin,
        GITCheckinPush,
        GITPush,
        GITPull,
        Unknown
    };
    static	String	rootDirPath				=	"";		//	File we are creating
	static  String  repositoryDirPath		=	"";
	static  String  repositoryLocalPath		=	"";
	static  String  repositoryRemotePath	=	""; 
	static  String  repositoryRemoteName	=	"";
	static  String  gitCommitIdStr			=	"";
	static  String  gitRetrievePath			=	"";
	
	public static void main(String[] args) {
		System.out.println("\n******************* JavaGitToolMain  Started ****************************\n");
		// TODO Auto-generated method stub
		// Need at least 2 arguments
		if(args == null || args.length < 2) {
			System.out.println("Need at least 2 arguments");
			return;
		}
		
		JavaGitTool.identitySshFileName = args[0];
		JavaGitTool.identitySshFileName = args[1];
		
		
		System.out.println("\n*****************  End Processing JavaGitToolMain ******************\n");
	}

}
