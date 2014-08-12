package javaGitTool;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschSession;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@SuppressWarnings("unused")
public class JavaGitTool {

	public static  String  knownHostFileName			=	"";
	public static  String  identitySshFileName			=	"";
	public static  String  gitAuthorName				=	"";
	public static  String  gitAuthorEmail				=	"";
	public static  String  gitCommitterName				=	"";
	public static  String  gitCommitterEmail			=	"";
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public class MySSHSessionFactory extends SshSessionFactory {
	    private  JSch j;

	    public MySSHSessionFactory() {
	        this.j = new JSch();
	    }

	    public void Initialize() throws JSchException, FileNotFoundException {
	    	try {
	    	
		    	File identitySshFile = new File(identitySshFileName);
		    	File knownHostFile = new File(knownHostFileName);
		    	
		    	if(identitySshFile.exists()) {
		    		this.j.addIdentity(identitySshFileName);
		    	} else {
		    		System.out.println("Missing identitySsh File: " + identitySshFileName);
		    		throw new FileNotFoundException("Missing identitySsh File: " + identitySshFileName);
		    	}
		    	
		    	if( knownHostFile.exists()) {
		    		this.j.setKnownHosts(knownHostFileName);
		    	} else {
		    		System.out.println("Missing knownHostFile: " + knownHostFileName);
		    		throw new FileNotFoundException("Missing knownHostFile: " + knownHostFileName);
		    	}
	    	} catch (Exception ex) {
				System.out.println(ex.getMessage());
	    		
	    	}
	    }

		@Override
		public org.eclipse.jgit.transport.RemoteSession getSession(URIish uri,
				CredentialsProvider credentialsProvider, FS fs, int tms)
				throws org.eclipse.jgit.errors.TransportException {
			
			Session session = null;
	        
	        try {
	        	session = this.j.getSession(uri.getUser(), uri.getHost());
	        	//session.SetUserInfo(new MyUserInfo());
				session.connect();
			} catch (JSchException e) {
				e.printStackTrace();
			}

	        return new JschSession(session, uri);
		}
	}
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static String gitCommit(String repositoryPath, String commitComment) throws Exception {
	    
		File gitWorkDir = new File(repositoryPath);
		String commitIdStr = null;
		
	    Git git = Git.open(gitWorkDir);

	  //Add files that are new
	    AddCommand add = git.add();
	    add.addFilepattern(".").call();
	    
		// create status
	    Boolean hasFilesToCommit = false;
		StatusCommand command = git.status();
		Status status = command.call();
		Set<String> files = status.getAdded();
		//System.out.println("getAdded files: " + files);
		
		if(files != null && files.size() > 0 ) {
			hasFilesToCommit = true;
		}
		
		files = status.getChanged();
		//System.out.println("getChanged files: " + files);
		if(files != null && files.size() > 0 ) {
			hasFilesToCommit = true;
		}
		
		files = status.getModified();
		//System.out.println("getModified files: " + files);
		if(files != null && files.size() > 0 ) {
			hasFilesToCommit = true;
		}
		
		files = status.getMissing();
		//System.out.println("getMissing files: " + files);
		
		if(files != null && files.size() > 0 ) {
			hasFilesToCommit = true;
		}
		
		DiffCommand diffCmd = git.diff();
		diffCmd.setShowNameAndStatusOnly(true);
		List<DiffEntry> listDiff = diffCmd.call();
		//System.out.println("List<DiffEntry> listDiff: " + listDiff);

	    if(hasFilesToCommit) {
	    	String formattedDate = (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date());
		    String commitMsg = commitComment != null ? commitComment : "Auto Check In on " + formattedDate;
	    	CommitCommand commit = git.commit();
	    	commit.setAll(true);
	        commit.setMessage(commitMsg);
	        commit.setAuthor(gitAuthorName, gitAuthorEmail);
	        commit.setCommitter(gitCommitterName, gitCommitterEmail);
	        //call the commit
	        commit.call();
	    	
	        System.out.println(commitMsg);
		    
	    } else {
	    	System.out.println("No files to commit ");
	    }
	    
	    // Retrieve the latest Commit Id
	    Iterator<RevCommit> iterator = git.log().call().iterator();
	    RevCommit rc2 = iterator.next();
	    commitIdStr = rc2.getId().name();
	    System.out.println("commitIdStr: " + commitIdStr);
	    
	    git.close();
	    return commitIdStr;
	    
	  }
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static void gitRetrieveCommit(String repositoryPath, String commitIdStr) throws Exception {
		File gitWorkDir = new File(repositoryPath);
	    Git git = Git.open(gitWorkDir);
	    ResetCommand gitReset = git.reset();
	    gitReset.setMode(ResetType.HARD);
	    gitReset.setRef(commitIdStr);
	    //Call GIT Reset
	    gitReset.call();
	    git.close();
	}
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static Git gitSSHInit(String localPath, String remotePath) throws Exception {
		MySSHSessionFactory sessionFactory = new JavaGitTool().new MySSHSessionFactory();
		sessionFactory.Initialize();
		SshSessionFactory.setInstance(sessionFactory);
        FileRepository localRepo = new FileRepository(localPath);
        return new Git(localRepo);
	}
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static void gitSSHCloneRepository(String localPath, String remotePath) throws Exception {
		Git git = gitSSHInit(localPath, remotePath);
        Git.cloneRepository().setURI(remotePath).setDirectory(new File(localPath)).call();
	}
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static void gitSSHPush(String localPath, String remotePath, String remoteName) throws Exception {
		Git git = gitSSHInit(localPath, remotePath);
		git.push().setPushAll().setRemote(remoteName).call();
	}
	
	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static void gitSSHPull(String localPath, String remotePath, String remoteName) throws Exception {
		Git git = gitSSHInit(localPath, remotePath);
		git.pull().setRemote(remoteName).call();
	}	

	//------------------------------------------------------------------------------------------------
	  /**
	   * @Desc:	
	   * @Param:	
	   * @Return:	
	   */ 
	public static List<String> gitRetrieveCommitIdsOrdered(String repositoryPath) throws Exception {
		File gitWorkDir = new File(repositoryPath);
		Git git = Git.open(gitWorkDir);
		Repository repo = git.getRepository();
	    RevWalk walk = new RevWalk(repo);
	    Iterator<RevCommit> revCommit = walk.iterator();
	    //EnumSet<RevSort> revSort = walk.getRevSort();)
	    //System.out.println("gitRetrieveCommitIdsOrdered revCommit: " + revCommit);
	    
	    while (revCommit.hasNext()) {
	    	System.out.println(revCommit.next());
	    }
	    
	    return null;
	}

//------------------------------------------------------------------------------------------------
  /**
   * @throws IOException 
   * @Desc:	
   * @Param:	
   * @Return:	
   */ 
	public static void cleanGitDirFiles(String folderPath) throws IOException {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File fileFolder = listOfFiles[i];
			String fileName = listOfFiles[i].getAbsolutePath();
			if(!fileName.toLowerCase().endsWith(".git")) {
				if(fileFolder.isDirectory()) {
					FileUtils.deleteDirectory(fileFolder);
				}
				
				if(fileFolder.isFile()) {
					Path filePath = fileFolder.toPath();
					try {
					    Files.delete(filePath);
					} catch (NoSuchFileException x) {
					    System.err.format("%s: no such" + " file or directory%n", filePath);
					} catch (DirectoryNotEmptyException x) {
					    System.err.format("%s not empty%n", filePath);
					} catch (IOException x) {
					    // File permission problems are caught here.
					    System.err.println(x);
					}
				}
			}
		}
	}		
}