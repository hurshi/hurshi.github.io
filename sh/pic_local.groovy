#!/usr/bin/env groovy

def replaceContent(File file){
	if(file.isDirectory()){
		for(File f :file.listFiles()){
			replaceContent(f)
		}
	}else{
		if(file.getName().toLowerCase().endsWith("md")){
			String content = file.text
			if(content.contains("](/img/posts")){
				content = content.replaceAll("\\]\\(/img/posts","\\]\\(\\.\\./img/posts")
				file.text = content
			}
		}
	}
}

File f = new File(".")
if(!f.getParentFile().getName().equals("hurshi.githu.io")){
	f = f .getParentFile();
}
String filePath = f.getAbsolutePath()+"/_posts/"
replaceContent(new File(filePath))