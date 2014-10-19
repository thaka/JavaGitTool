package javaGitTool;

public class JavaGitToolMain {
	
	enum FuntionType {
        GITCheckin,
        GITCheckinPush,
        GITPush,
        GITPull,
        GITReset,
        GITCleanDir,
        Unknown
    };
    static	String	rootDirPath				=	"";		//	File we are creating
	static  String  repositoryDirPath		=	"";
	static  String  repositoryLocalPath		=	"";
	static  String  repositoryRemotePath	=	""; 
	static  String  repositoryRemoteName	=	"";
	static  String  gitCommitIdStr			=	"";
	static  String  gitRetrievePath			=	"";
	static  String  gitCommitComment		=	"";

//------------------------------------------------------------------------------------------------
  /**
   * @Desc:	Main entry method
   * @Param:	
   * @Return:	
   */ 		
	public static void main(String[] args) throws Exception {
	
		FuntionType functionType =  FuntionType.Unknown;
		System.out.println("\n******************* JavaGitToolMain  Started ****************************\n");
		String operationName = "";
		// Need at least 2 arguments
		if(args != null && args.length > 1) {
			operationName = args[0].trim().toLowerCase();
			if(operationName.equalsIgnoreCase("gitSSHPull".toLowerCase())) {
				functionType = FuntionType.GITPull;
			} else if(operationName.equalsIgnoreCase("gitSSHPush".toLowerCase())) {
				functionType = FuntionType.GITPush;
			} else if(operationName.equalsIgnoreCase("gitCommitSSHPush".toLowerCase())) {
				functionType = FuntionType.GITCheckinPush;
			} else if(operationName.startsWith("gitCommit".toLowerCase())) {
				functionType = FuntionType.GITCheckin;
			} else if(operationName.startsWith("GITReset".toLowerCase())) {
				functionType = FuntionType.GITReset;
			} else if(operationName.startsWith("cleanGitDirFiles".toLowerCase())) {
				functionType = FuntionType.GITCleanDir;
			}			
		} else {
			System.out.println("Need at least 2 arguments");
			return;
		}
		
		if(args != null && args.length > 1) {
			if(functionType == FuntionType.GITPull && args.length >= 6) {
				JavaGitTool.identitySshFileName		= args[1].trim();
				JavaGitTool.knownHostFileName		= args[2].trim();
				repositoryLocalPath 				= args[3].trim();
				repositoryRemotePath 				= args[4].trim();
				repositoryRemoteName		 		= args[5].trim();
		        JavaGitTool.gitSSHPull(repositoryLocalPath, repositoryRemotePath, repositoryRemoteName);
			} else if(functionType == FuntionType.GITPush && args.length >= 6) {
				JavaGitTool.identitySshFileName		= args[1].trim();
				JavaGitTool.knownHostFileName		= args[2].trim();
				repositoryLocalPath 				= args[3].trim();
				repositoryRemotePath 				= args[4].trim();
				repositoryRemoteName		 		= args[5].trim();
		        JavaGitTool.gitSSHPush(repositoryLocalPath, repositoryRemotePath, repositoryRemoteName);
			} else if(functionType == FuntionType.GITCheckinPush && args.length >= 8) {
				JavaGitTool.identitySshFileName	= args[1].trim();
				JavaGitTool.knownHostFileName	= args[2].trim();
				repositoryLocalPath 			= args[3].trim();
				repositoryRemotePath 			= args[4].trim();
				repositoryRemoteName		 	= args[5].trim();
				JavaGitTool.gitAuthorName 		= args[6].trim();
				JavaGitTool.gitCommitterName	= JavaGitTool.gitAuthorName;
				JavaGitTool.gitAuthorEmail	 	= args[7].trim();
				JavaGitTool.gitCommitterEmail	= JavaGitTool.gitAuthorEmail;
				if(args.length >= 9) {
					gitCommitComment 	= args[8].trim();
				} else {
					gitCommitComment = null;
				}
				JavaGitTool.gitCommit(repositoryLocalPath, gitCommitComment);
		        JavaGitTool.gitSSHPush(repositoryLocalPath, repositoryRemotePath, repositoryRemoteName);
			} else if(functionType == FuntionType.GITCheckin && args.length >= 5) {
				repositoryDirPath 				= args[1].trim();
				JavaGitTool.gitAuthorName 		= args[2].trim();
				JavaGitTool.gitCommitterName	= JavaGitTool.gitAuthorName;
				JavaGitTool.gitAuthorEmail	 	= args[3].trim();
				JavaGitTool.gitCommitterEmail	= JavaGitTool.gitAuthorEmail;
				if(args.length >= 5) {
					gitCommitComment 	= args[4].trim();
				} else {
					gitCommitComment = null;
				}
				JavaGitTool.gitCommit(repositoryLocalPath, gitCommitComment);
			} else if(functionType == FuntionType.GITReset && args.length >= 3) {
				repositoryDirPath 	= args[1].trim();
				gitCommitIdStr		= args[1].trim();
				JavaGitTool.gitRetrieveCommit(repositoryLocalPath, gitCommitIdStr);
			} else if(functionType == FuntionType.GITCleanDir && args.length >= 2) {
				repositoryDirPath 	= args[1].trim();
		        JavaGitTool.cleanGitDirFiles(repositoryDirPath);
			} else {
				System.out.println("There are not enought parameters for operation: " + operationName);
			}
		} else {
			System.out.println("There are not parameters ");
			
		}
		
		
		System.out.println("\n*****************  End Processing JavaGitToolMain ******************\n");
	}
}
