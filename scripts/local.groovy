#!/usr/bin/env groovy

// <div class="mermaid">
// </div><!--mermaid-->

// ``mermaid
// ```\n<div style="display:none"></div>

def replaceContent(File file) {
    if (file.isDirectory()) {
        for (File f : file.listFiles()) {
            replaceContent(f)
        }
    } else {
        if (file.getName().toLowerCase().endsWith("md")) {
            String content = file.text
            if (content.contains("/img/posts")||content.contains("mermaid")) {
                content = content.replaceAll("/img/posts", "\\.\\./img/posts")
                content = content.replaceAll('<div class="mermaid">', "```mermaid")
                content = content.replaceAll('</div><!--mermaid-->', '```\n<div style="display:none"></div>')
                
                file.text = content
            }
        }
    }
}

File f = new File(getClass().protectionDomain.codeSource.location.path).getParentFile()
if (!f.getParentFile().getName().equals("hurshi.githu.io")) {
    f = f.getParentFile()
}
String filePath = f.getAbsolutePath() + "/_posts"
replaceContent(new File(filePath))