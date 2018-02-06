# smartcn-dict
lucene中smartcn分词器使用自带的词库，在工作中，需要扩展词库，而自带的词库是使用自然语言处理生成的词库，如果我们已有词库或者不想通过自然语言处理构建词库时，可使用该工具构造词库，使用方法如下：
1. 创建词库文件，词库每一行格式为：#词汇# #词频#，其中，词频可以不存在，当词频不存在时，工具会随机生成100以内的伪词频
2. 使用com.tiktok01.smartcn包中的CmdApp类，或者编译后的jar包，jar包参数如下：
	-srcdict: 自建词库文件路径
	-coremem: 原始词库coredict.mem文件的路径，可为空，不为空则合并
	-target: dict文件生成目录，会在该目录中生成new_coredict.dct和new_bigramdict.dct文件
3. 词库使用方法:
	方法一: 修改并编译smartcn包的源码
	方法二: 参考源码中test目录的做法，重写Smartcn的Analyzer类型，反射重新加载dict文件，
