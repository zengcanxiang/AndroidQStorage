# AndroidQStorageSample

## 是什么？

本项目是，在当前Android Q即将大规模普及的情况下，适配Android Q对存储权限做出的调整，而进行的一个实战项目。

项目中对文件的操作全部基于Uri对象和AndroidX的DocumentFile库，为大家演示Android Q通过Storage Access Framework如何操作私有目录和公共目录。

主要功能包括，

- 对Android Q 存储权限的获取
- 校验一个Uri是否有效
- 如何保存、获取MediaStore内容
- 如何通过SAF获取文件/文件夹的Uri
- 如何通过SAF传入Uri对文件进行操作

## 为什么？

作为Android的开发者，大家应该对早年Android手机被人抱怨卡顿、手机存储空间被应用占用、文件被毒瘤应用监听篡改等诸多问题。很多Android开发者自己都不愿意使用Android手机(尊重个人爱好)。

现在生态在一步一步的变好，Google对文件管理方面一步一步的收紧权限，对用户来说，无疑是一个好消息。

不过，开发者就需要辛苦跟进步伐。但是，换个角度想，如果Android生态因此受益，作为Android开发者，是不是减少“原生开发没人要了”的疑惑呢？

这个项目，是在升级兼容下，把一个比较通用，常用的操作，给大家演示出来，希望能帮到大家减少升级兼容的工作量。

## 怎么做？

除了对文件的操作，通过导入了androidx的DocumentFile库，传入uri来进行，快捷操作，其他的都是依赖于Android SDK内自带的API。

如果不访问公共目录(非MediaStore)，只需要关注`PublicDirActivity`页面。

操作公共目录的代码，主要在`SAFOperateActivity`页面。

下面是演示APP操作的GIF，希望能帮到大家。

校验Uri和保存到MediaStore:

![校验Uri和保存到MediaStore](https://pic.imgdb.cn/item/5e9138d0504f4bcb0454f97f.gif)

读取MediaStore：

![读取MediaStore](https://pic.imgdb.cn/item/5e9138d6504f4bcb0454fed3.gif)

通过SAF创建文件：

![通过SAF创建文件](https://pic.imgdb.cn/item/5e9138e2504f4bcb0455093a.gif)

通过SAF删除文件（演示中，为了明显，通过SAF选择文件后在删除，实际操作中，可以传入自己查询到的Uri）：

![通过SAF删除](https://pic.imgdb.cn/item/5e9138db504f4bcb04550416.gif)



通过SAF查找文件或者文件夹Uri：

![通过SAF查找Uri](https://pic.imgdb.cn/item/5e9138fb504f4bcb04551e9a.gif)