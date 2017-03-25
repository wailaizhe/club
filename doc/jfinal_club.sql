/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50616
Source Host           : localhost:3306
Source Database       : jfinal_club

Target Server Type    : MYSQL
Target Server Version : 50616
File Encoding         : 65001

Date: 2017-02-16 19:13:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `account`
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickName` varchar(50) NOT NULL,
  `userName` varchar(150) NOT NULL,
  `password` varchar(150) NOT NULL,
  `salt` varchar(150) NOT NULL,
  `status` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `avatar` varchar(100) NOT NULL,
  `likeCount` int(11) NOT NULL DEFAULT '0' COMMENT '被赞次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('1', 'JFinalClub', 'test@test.com', 'a1f0917284a75c2c45dfeefd9040ce01144407c1a33d1bc3c45153ceb9d12d72', 'zmxyyZJkE-N6JjRhujp6U8l4Yu7vuQDZ', '1', '2015-06-18 09:00:19', '175.12.244.105', '0/1.jpg', '999');

-- ----------------------------
-- Table structure for `auth_code`
-- ----------------------------
DROP TABLE IF EXISTS `auth_code`;
CREATE TABLE `auth_code` (
  `id` varchar(33) NOT NULL,
  `accountId` int(11) NOT NULL,
  `expireAt` bigint(20) NOT NULL,
  `type` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of auth_code
-- ----------------------------

-- ----------------------------
-- Table structure for `document`
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `mainMenu` int(11) NOT NULL COMMENT '主菜单',
  `subMenu` int(11) NOT NULL COMMENT '子菜单',
  `title` varchar(300) NOT NULL,
  `content` text NOT NULL,
  `updateAt` datetime NOT NULL,
  `createAt` datetime NOT NULL,
  `publish` tinyint(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`mainMenu`,`subMenu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of document
-- ----------------------------
INSERT INTO `document` VALUES ('1', '0', '前言', '<p>1：版本说明</p><p>2：术语约定</p><p>3：最佳实践<br/></p><p>4：</p>', '2016-09-27 16:08:48', '2016-09-25 16:40:09', '1');
INSERT INTO `document` VALUES ('1', '1', '概要', '<h2>1、文档说明</h2><p>&nbsp; &nbsp; 社区文档频道将提供全面、权威、最新的开发文档，文档内容将随着项目发展、用户反饭不断进化、更新，建议以在线的形式查阅，保障始终获取到的信息是最新最权威的<br/></p><h2>2、版权声明</h2><p>&nbsp; &nbsp; JFinal 文档频道版权归 JFinal 社区所有，未经许可不得转载</p><h2>3、扫码入社</h2><p>&nbsp; &nbsp; 扫描下方二维码关注 JFinal 官方公众号，获取社区最新动态<br/></p><p style=\"text-align: center;\"><br/><img src=\"/assets/img/jfinal_weixin_service_qr_258.png\"/></p>', '2016-09-28 21:20:57', '2016-09-25 16:41:27', '1');
INSERT INTO `document` VALUES ('1', '2', '术语约定', '<p>&nbsp; &nbsp; 文档内容将会不可避免的反复用到相同的名称、代码等，为了使文档内容简短、精要，所以在此对一些常用术语进行约定，这些约定无需记忆，了解即可</p><h2>1：AppConfig</h2><p>&nbsp; &nbsp;约定 AppConfig 为项目中的继承自 JFinalConfig 的类文件</p><h2>2：configXxx系列</h2><p>&nbsp; &nbsp;约定configXxx系列方法为 AppConfig 之中的方法，共有如下六个方 法：configConstant(...)、configRoute(...)、configEngine(...)、configHandler(...)、configInterceptor(...)、configPlugin(...)</p><p><br/></p>', '2016-09-28 21:01:13', '2016-09-25 17:56:11', '1');
INSERT INTO `document` VALUES ('2', '0', '最佳实践', '<p>1：</p>', '2016-09-27 16:26:49', '2016-09-27 16:23:05', '1');
INSERT INTO `document` VALUES ('2', '1', '概要', '<h2>1 概要</h2><p>&nbsp; &nbsp; 本章将介绍 JFinal 开发的最佳实践，合理的分层与组织结构是对复杂性最有效的管理方法，遵循最佳实践，不仅可以进一步提升开发效率，而且在项目演化的生命周期具有更好的可扩展性和可维护性，极大降低成本</p><h2>2 项目分层</h2><p>&nbsp; &nbsp; JFinal 最佳实践需划分 MVCS 四层，其中 MVC 是大家所熟知的Model、View、Controller， S是指Service业务层，Service 层是 JFinal 项目的核心，是重中之重</p><h2>3 模块划分</h2><p>&nbsp; &nbsp; 采用分而治之的策略进行模块划分，将复杂问题逐步转化为便于解决的简单问题</p><p>&nbsp; &nbsp; 模块划分采用分类的方式，对不同类别的概念进行识别分类，按类别划分模块。大的概念划分为小概念的组合，进而大类别划分为小类别的组合</p><p>&nbsp; &nbsp; 在项目初期认知还比较模糊的时候可以暂时依据 tableName 来划分。例如有三张表：project、share、feedback，则创建与这三张表同名的顶层 package，下图是 jfinal 社区的顶层 package 截图</p>', '2016-09-29 11:41:58', '2016-09-27 16:23:53', '1');
INSERT INTO `document` VALUES ('2', '2', 'Model层', '<h2>1 集中管理</h2><p>&nbsp; &nbsp; 有生命力的系统会不断进化与生长，对并发的要求会越来越高，当优化 sql 与引入 cache 这两种方式无法满足需要后，就进入了集群 + 分布式进化阶段</p><p>&nbsp; &nbsp; 分布式需要将大系统拆分成多个小型系统，而 Model 集中管理有利于这种拆分。将所有 Model 放在 common.model 这个包下非常容易抽取成一个maven 模块共享给分布式系统的其它模块</p><h2>2 使用Generator</h2><p>&nbsp; &nbsp;使用 Generator 生成的 model 符合 java bean 规范，并立即拥有 getter、setter 方法，有利于大量依靠 java bean 规范而工作的第三方工具，例如 fastjson、jackson。<br/></p><p>&nbsp; &nbsp;此外，生成的 model 拥有静态语言的好处，无需记忆字段名称，便于重构</p><h2>3 Model代码</h2><p>&nbsp; &nbsp; Model 中不要放业务逻辑，仅仅放一些纯粹 model 自己内部状态有关的通用方法，model 是要被其它业务模块共用的，不要放与具体某个业务有前端的代码，同样这也有利于未来的分布式</p><p>&nbsp; &nbsp;例如有一张 account 表，其中有一个 int status 字段，以下是代码示例：</p><pre class=\"brush:java;toolbar:false\">public&nbsp;class&nbsp;Account&nbsp;extends&nbsp;BaseAccount&lt;Account&gt;&nbsp;{\n&nbsp;&nbsp;\n&nbsp;&nbsp;public&nbsp;static&nbsp;final&nbsp;int&nbsp;STATUS_LOCK_ID&nbsp;=&nbsp;-1;&nbsp;&nbsp;//&nbsp;锁定账号\n&nbsp;&nbsp;public&nbsp;static&nbsp;final&nbsp;int&nbsp;STATUS_REG&nbsp;=&nbsp;0;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;注册、未激活\n&nbsp;&nbsp;public&nbsp;static&nbsp;final&nbsp;int&nbsp;STATUS_OK&nbsp;=&nbsp;1;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;正常、已激活\n\n&nbsp;&nbsp;public&nbsp;boolean&nbsp;isStatusOk()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;return&nbsp;getStatus()&nbsp;==&nbsp;STATUS_OK;\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;public&nbsp;boolean&nbsp;isStatusReg()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;return&nbsp;getStatus()&nbsp;==&nbsp;STATUS_REG;\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;public&nbsp;boolean&nbsp;isStatusLockId()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;return&nbsp;getStatus()&nbsp;==&nbsp;STATUS_LOCK_ID;\n&nbsp;&nbsp;}\n｝</pre><h2>4：避免Model中创建dao</h2><p>&nbsp; &nbsp; JFinal 以往提供 demo 的 Model 中做了一个不好的示范，在其中创建了 public static final Xxx dao 对象，原本是为了在查询时可以少创建一次对象，但发现有很多用户使用该 dao 对象进行了查询以外的操作，例如 save()、update()、set() 等操作</p><p>&nbsp; &nbsp; 由于 static Xxx dao 对象是全局共享的，所以会有线程安全问题，为了彻底杜绝新手的误用，jfinal 最佳实践需要将 dao 对象从 Model 中删除，转而在 Service 中创建，例如：</p><pre class=\"brush:java;toolbar:false\">public&nbsp;class&nbsp;AccountService&nbsp;{\n\n&nbsp;&nbsp;private&nbsp;Account&nbsp;dao&nbsp;=&nbsp;new&nbsp;Account();\n&nbsp;&nbsp;\n&nbsp;&nbsp;public&nbsp;Ret&nbsp;login(String&nbsp;userName,&nbsp;String&nbsp;password)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;String&nbsp;sql=&nbsp;&quot;select&nbsp;*&nbsp;from&nbsp;account&nbsp;where&nbsp;userName=?&nbsp;limit&nbsp;1&quot;;\n&nbsp;&nbsp;&nbsp;&nbsp;Account&nbsp;account&nbsp;=&nbsp;dao.findFirst(sql,&nbsp;userName);\n&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;...&nbsp;其它代码\n&nbsp;&nbsp;}\n}</pre><p>&nbsp; &nbsp;如上代码所示， dao 对象从 Account 中转移到了 AccountService 中，并声明为 private 避免外界对该对象的使用。同时也避免了线程安全问题。<br/></p>', '2016-09-29 23:37:09', '2016-09-27 16:25:40', '1');
INSERT INTO `document` VALUES ('2', '3', 'Service层', '<h2>1 用到sql的代码</h2><p>&nbsp; &nbsp;有些开发者习惯于 sql 随手就来，不管是在哪里只要有需要就直接 sql 操作数据库。这种习惯表面上会带来开发效率与便利性，但事实并非如此<br/></p><p>&nbsp; &nbsp;&nbsp;</p><h2>2 与业务有关的代码</h2><p><br/></p><h2>2 优先考虑业务层</h2><p>&nbsp; &nbsp;当要写代码时，优先考虑将代码写在 Service 层中，例如有个 RegValidator 用于注册时对表单提交的数据进行校验，其中需要判断一项 nickName 是否已被注删，在 RegValidator 中有如下代码：</p><pre class=\"brush:java;toolbar:false\">public&nbsp;class&nbsp;RegValidator&nbsp;extends&nbsp;Validator&nbsp;{\n&nbsp;&nbsp;public&nbsp;void&nbsp;validate(Controller&nbsp;c)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;String&nbsp;sql&nbsp;=&nbsp;&quot;select&nbsp;id&nbsp;from&nbsp;account&nbsp;where&nbsp;nickName=?&nbsp;limit&nbsp;1&quot;;\n&nbsp;&nbsp;&nbsp;&nbsp;Account&nbsp;account&nbsp;=&nbsp;new&nbsp;Account().findFirst(sql,&nbsp;c.getPara(&quot;nickName&quot;));\n&nbsp;&nbsp;&nbsp;&nbsp;if&nbsp;(account&nbsp;!=&nbsp;null)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;addError(&quot;msg&quot;,&nbsp;&quot;nickName&nbsp;已被注册&quot;);\n&nbsp;&nbsp;&nbsp;&nbsp;}\n&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;...&nbsp;其它代码\n&nbsp;&nbsp;}\n}</pre><p>&nbsp; &nbsp; 最佳实践是 nickName 是否被注册的代码写在 AccountService 层中，有利于代码重用，有助于未来分布式演化，有利于在业务层添加缓存等机制，将代码挪至业务层后 RegValidator 的样子：</p><pre class=\"brush:java;toolbar:false\">public&nbsp;class&nbsp;RegValidator&nbsp;extends&nbsp;Validator&nbsp;{\n&nbsp;&nbsp;public&nbsp;void&nbsp;validate(Controller&nbsp;c)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;if&nbsp;(AccountService.me.isNickNameRegisted(c.getPara(&quot;nickName&quot;)))&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;addError(&quot;msg&quot;,&nbsp;&quot;nickName&nbsp;已被注册&quot;):\n&nbsp;&nbsp;&nbsp;&nbsp;}\n&nbsp;&nbsp;}\n}</pre><p><br/></p>', '2016-09-30 00:00:51', '2016-09-27 16:26:01', '1');
INSERT INTO `document` VALUES ('2', '4', 'Controller层', '<p>待添加</p>', '2016-09-28 21:22:14', '2016-09-28 21:22:14', '1');
INSERT INTO `document` VALUES ('2', '5', 'View层', '<p>待添加</p>', '2016-09-28 21:22:55', '2016-09-28 21:22:37', '1');
INSERT INTO `document` VALUES ('2', '6', '其它', '<p>待添加</p>', '2016-09-28 21:23:33', '2016-09-28 21:23:33', '1');
INSERT INTO `document` VALUES ('3', '0', 'JFinal架构', '<p>待添加</p>', '2016-09-27 16:09:27', '2016-09-26 21:53:36', '1');
INSERT INTO `document` VALUES ('3', '1', '顶层架构', '<p>待添加</p>', '2016-09-27 16:10:09', '2016-09-26 21:14:44', '1');

-- ----------------------------
-- Table structure for `download`
-- ----------------------------
DROP TABLE IF EXISTS `download`;
CREATE TABLE `download` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fileName` varchar(280) NOT NULL,
  `descr` varchar(280) NOT NULL COMMENT '描述',
  `fileType` varchar(20) NOT NULL COMMENT '文件类型',
  `size` varchar(20) NOT NULL,
  `createDate` date NOT NULL,
  `path` varchar(280) NOT NULL,
  `downloadCount` int(11) NOT NULL,
  `isShow` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of download
-- ----------------------------
INSERT INTO `download` VALUES ('42', 'jfinal-2.2-manual.pdf', 'JFinal 手册', 'PDF', '1.39 MB', '2016-01-19', '/upload/2.2/', '140252', '1');
INSERT INTO `download` VALUES ('43', 'jfinal-2.2-all.zip', 'JFinal 2.2 all', 'ZIP', '20.26 MB', '2016-01-19', '/upload/2.2/', '127351', '1');
INSERT INTO `download` VALUES ('44', 'jfinal-2.2_demo.zip', 'JFinal demo', 'ZIP', '5.91 MB', '2016-01-19', '/upload/2.2/', '123110', '1');
INSERT INTO `download` VALUES ('45', 'GeneratorDemo.java', 'Generator demo', 'Java', '2 KB', '2016-01-19', '/upload/2.2/', '110699', '1');
INSERT INTO `download` VALUES ('46', 'jfinal-weixin-1.7-bin-with-src.jar', 'JFinal weixin 1.7', 'JAR', '258 KB', '2016-01-12', '/upload/2.2/', '11633', '0');
INSERT INTO `download` VALUES ('47', 'jfinal-weixin-1.8-bin-with-src.jar', 'JFinal Weixin 1.8', 'JAR', '279 KB', '2016-07-11', '/upload/2.2/', '13503', '1');
INSERT INTO `download` VALUES ('48', 'jfinal-weixin-1.8-lib.zip', 'JFinal Weixin 1.8 lib', 'ZIP', '4.31 MB', '2016-07-11', '/upload/2.2/', '11312', '1');
INSERT INTO `download` VALUES ('49', 'jfinal-2.2-changelog.txt', 'JFinal changelog', 'TXT', '6 KB', '2016-01-19', '/upload/2.2/', '15590', '1');

-- ----------------------------
-- Table structure for `download_log`
-- ----------------------------
DROP TABLE IF EXISTS `download_log`;
CREATE TABLE `download_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `fileName` varchar(200) NOT NULL,
  `downloadDate` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of download_log
-- ----------------------------

-- ----------------------------
-- Table structure for `favorite`
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `refType` int(11) NOT NULL COMMENT '收藏类型：1为项目，2为分享，3为反馈',
  `refId` int(11) NOT NULL COMMENT '被收藏的资源 id',
  `createAt` datetime NOT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for `feedback`
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `content` text NOT NULL,
  `createAt` datetime NOT NULL,
  `clickCount` int(11) NOT NULL DEFAULT '0',
  `report` int(11) NOT NULL DEFAULT '0',
  `likeCount` int(11) NOT NULL DEFAULT '0',
  `favoriteCount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES ('1', '1', '1', 'JFinal 新社区 feedback 反馈栏目', '<h2>热心反馈、提升品质</h2><p>&nbsp; &nbsp; JFinal 新社区反馈栏目，是用户向作者提出自己在项目使用过程之中碰到的问题或者改进建议，例如某某项目中某个功能不好用，又或者自己有更好的方案反馈给作者，项目作者可以有针对性地进行权衡和改进，有助于打造高品质的项目，从而也为用户带来更大价值。<br></p>', '2066-06-06 06:06:06', '0', '0', '9', '4');

-- ----------------------------
-- Table structure for `feedback_like`
-- ----------------------------
DROP TABLE IF EXISTS `feedback_like`;
CREATE TABLE `feedback_like` (
  `accountId` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`accountId`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of feedback_like
-- ----------------------------

-- ----------------------------
-- Table structure for `feedback_page_view`
-- ----------------------------
DROP TABLE IF EXISTS `feedback_page_view`;
CREATE TABLE `feedback_page_view` (
  `feedbackId` varchar(25) NOT NULL,
  `visitDate` date NOT NULL,
  `visitCount` int(20) NOT NULL,
  PRIMARY KEY (`feedbackId`,`visitDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of feedback_page_view
-- ----------------------------

-- ----------------------------
-- Table structure for `feedback_reply`
-- ----------------------------
DROP TABLE IF EXISTS `feedback_reply`;
CREATE TABLE `feedback_reply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `feedbackId` int(11) NOT NULL,
  `accountId` int(11) NOT NULL,
  `content` text NOT NULL,
  `createAt` datetime NOT NULL,
  `report` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of feedback_reply
-- ----------------------------

-- ----------------------------
-- Table structure for `friend`
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `accountId` int(11) NOT NULL,
  `friendId` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`accountId`,`friendId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend
-- ----------------------------

-- ----------------------------
-- Table structure for `like_message_log`
-- ----------------------------
DROP TABLE IF EXISTS `like_message_log`;
CREATE TABLE `like_message_log` (
  `accountId` int(11) NOT NULL,
  `refType` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `createAt` datetime NOT NULL COMMENT 'creatAt用于未来清除该表中时间比较久远的记录',
  PRIMARY KEY (`accountId`,`refType`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用于保存点赞的记录，用于记录点赞后发布过系统消息，保障只发一次';

-- ----------------------------
-- Records of like_message_log
-- ----------------------------

-- ----------------------------
-- Table structure for `login_log`
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `accountId` int(11) NOT NULL,
  `loginAt` datetime NOT NULL,
  `ip` varchar(100) DEFAULT NULL,
  KEY `accountId_index` (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of login_log
-- ----------------------------

-- ----------------------------
-- Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL COMMENT '消息的主人',
  `friend` int(11) NOT NULL COMMENT '对方的ID',
  `sender` int(11) NOT NULL COMMENT '发送者',
  `receiver` int(11) NOT NULL COMMENT '接收者',
  `type` tinyint(2) NOT NULL COMMENT '0：普通消息，1：系统消息',
  `content` text NOT NULL,
  `createAt` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for `news_feed`
-- ----------------------------
DROP TABLE IF EXISTS `news_feed`;
CREATE TABLE `news_feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL COMMENT '动态创建者',
  `refType` tinyint(2) NOT NULL COMMENT '动态引用类型',
  `refId` int(11) NOT NULL DEFAULT '0' COMMENT '动态引用所关联的 id',
  `refParentType` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'reply所属的贴子类型, 与type 字段填的值一样',
  `refParentId` int(11) NOT NULL DEFAULT '0',
  `createAt` datetime NOT NULL COMMENT '动态创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of news_feed
-- ----------------------------

-- ----------------------------
-- Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `title` varchar(150) NOT NULL,
  `content` text NOT NULL,
  `createAt` datetime NOT NULL,
  `clickCount` int(11) NOT NULL DEFAULT '0',
  `report` int(11) NOT NULL DEFAULT '0',
  `likeCount` int(11) NOT NULL DEFAULT '0',
  `favoriteCount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1', '1', 'JFinal', 'JFinal 极速开发框架', '<p>JFinal 是基于 Java 语言的极速 WEB + ORM 框架，其核心设计目标是开发迅速、代码量少、学习简单、功能强大、轻量级、易扩展、Restful。在拥有Java语言所有优势的同时再拥有ruby、python、php等动态语言的开发效率！为您节约更多时间，去陪恋人、家人和朋友 :)666</p><h2>JFinal有如下主要特点：</h2><ul class=\" list-paddingleft-2\"><li><p>MVC架构，设计精巧，使用简单</p></li><li><p>遵循COC原则，零配置，无xml</p></li><li><p>独创Db + Record模式，灵活便利</p></li><li><p>ActiveRecord支持，使数据库开发极致快速</p></li><li><p>自动加载修改后的java文件，开发过程中无需重启web server</p></li><li><p>AOP支持，拦截器配置灵活，功能强大</p></li><li><p>Plugin体系结构，扩展性强</p></li><li><p>多视图支持，支持FreeMarker、JSP、Velocity</p></li><li><p>强大的Validator后端校验功能</p></li><li><p>功能齐全，拥有struts2的绝大部分功能</p></li><li><p>体积小仅339K，且无第三方依赖</p></li></ul><h2>以下是JFinal实现Blog管理的示例：</h2><h3>1：控制器(支持FreeMarker、JSP、Velocity、JSON等以及自定义视图渲染)</h3><pre>@Before(BlogInterceptor.class)\npublic&nbsp;class&nbsp;BlogController&nbsp;extends&nbsp;Controller&nbsp;{\n&nbsp;&nbsp;public&nbsp;void&nbsp;index()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;setAttr(\"blogList\",&nbsp;Blog.dao.find(\"select&nbsp;*&nbsp;from&nbsp;blog\"));\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;public&nbsp;void&nbsp;add()&nbsp;{\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;@Before(BlogValidator.class)\n&nbsp;&nbsp;public&nbsp;void&nbsp;save()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;getModel(Blog.class).save();\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;public&nbsp;void&nbsp;edit()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;setAttr(\"blog\",&nbsp;Blog.dao.findById(getParaToInt()));\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;@Before(BlogValidator.class)\n&nbsp;&nbsp;public&nbsp;void&nbsp;update()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;getModel(Blog.class).update();\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;public&nbsp;void&nbsp;delete()&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;Blog.dao.deleteById(getParaToInt());\n&nbsp;&nbsp;}\n}</pre><h3>2：Model(无xml、无annotation、无attribute、无getter、无setter)</h3><pre>public&nbsp;class&nbsp;Blog&nbsp;extends&nbsp;Model&lt;Blog&gt;&nbsp;{\n}</pre><h3>3：Validator(API引导式校验，比xml校验方便N倍，有代码检查不易出错)</h3><pre>public&nbsp;class&nbsp;BlogValidator&nbsp;extends&nbsp;Validator&nbsp;{\n&nbsp;&nbsp;protected&nbsp;void&nbsp;validate(Controller&nbsp;controller)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;validateRequiredString(\"blog.title\",&nbsp;\"titleMsg\",&nbsp;\"请输入Blog标题!\");\n&nbsp;&nbsp;&nbsp;&nbsp;validateRequiredString(\"blog.content\",&nbsp;\"contentMsg\",&nbsp;\"请输入Blog内容!\");\n&nbsp;&nbsp;}\n\n&nbsp;&nbsp;protected&nbsp;void&nbsp;handleError(Controller&nbsp;controller)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;controller.keepModel(Blog.class);\n&nbsp;&nbsp;}\n}</pre><h3>4：拦截器(在此demo中仅为示例，本demo不需要此拦截器)</h3><pre>public&nbsp;class&nbsp;BlogInterceptor&nbsp;implements&nbsp;Interceptor&nbsp;{\n&nbsp;&nbsp;public&nbsp;void&nbsp;intercept(Invocation&nbsp;inv)&nbsp;{\n&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(\"Before&nbsp;invoking&nbsp;\"&nbsp;+&nbsp;inv.getActionKey());\n&nbsp;&nbsp;&nbsp;&nbsp;inv.invoke();\n&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(\"After&nbsp;invoking&nbsp;\"&nbsp;+&nbsp;inv.getActionKey());\n&nbsp;&nbsp;}\n}</pre><h3>5：最新下载</h3><ul class=\" list-paddingleft-2\"><li><p><a href=\"/download?file=jfinal-2.2-manual.pdf\" target=\"_blank\">JFinal 手册</a></p></li><li><p><a href=\"/download?file=jfinal-2.2-all.zip\" target=\"_blank\">JFinal 2.2 all</a></p></li><li><p><a href=\"/download?file=jfinal-2.2_demo.zip\" target=\"_blank\">JFinal demo</a></p></li><li><p><a href=\"/download?file=GeneratorDemo.java\" target=\"_blank\">Generator demo</a></p></li><li><p><a href=\"/download?file=jfinal-2.2-changelog.txt\" target=\"_blank\">JFinal changelog</a></p></li></ul>', '2016-06-06 06:06:06', '0', '0', '131', '66');
INSERT INTO `project` VALUES ('2', '1', 'JFinal Weixin', 'JFinal Weixin 极速开发 SDK', '<p>JFinal Weixin 是基于 JFinal 的微信公众号极速开发 SDK，只需浏览 Demo 代码即可进行极速开发，自 JFinal Weixin 1.2 版本开始已添加对多公众号支持。</p><h2>1、WeixinConfig配置</h2><p>详情请见：JFinal weixin中的WeixinConfig配置</p><h2>2、WeixinMsgController</h2>JFinal weixin 1.8</a></p>', '2016-06-06 09:09:09', '0', '0', '26', '17');

-- ----------------------------
-- Table structure for `project_like`
-- ----------------------------
DROP TABLE IF EXISTS `project_like`;
CREATE TABLE `project_like` (
  `accountId` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`accountId`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_like
-- ----------------------------

-- ----------------------------
-- Table structure for `project_page_view`
-- ----------------------------
DROP TABLE IF EXISTS `project_page_view`;
CREATE TABLE `project_page_view` (
  `projectId` varchar(25) NOT NULL,
  `visitDate` date NOT NULL,
  `visitCount` int(20) NOT NULL,
  PRIMARY KEY (`projectId`,`visitDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_page_view
-- ----------------------------

-- ----------------------------
-- Table structure for `refer_me`
-- ----------------------------
DROP TABLE IF EXISTS `refer_me`;
CREATE TABLE `refer_me` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `referAccountId` int(11) NOT NULL COMMENT '接收者账号id',
  `newsFeedId` int(11) NOT NULL COMMENT 'newsFeedId',
  `type` tinyint(2) NOT NULL COMMENT '@我、评论我等等的refer类型',
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of refer_me
-- ----------------------------

-- ----------------------------
-- Table structure for `remind`
-- ----------------------------
DROP TABLE IF EXISTS `remind`;
CREATE TABLE `remind` (
  `accountId` int(11) NOT NULL COMMENT '用户账号id，必须手动指定，不自增',
  `referMe` int(11) NOT NULL DEFAULT '0' COMMENT '提到我的消息条数',
  `message` int(11) NOT NULL DEFAULT '0' COMMENT '私信条数',
  `fans` int(11) NOT NULL DEFAULT '0' COMMENT '粉丝增加个数',
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of remind
-- ----------------------------

-- ----------------------------
-- Table structure for `sensitive_words`
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_words`;
CREATE TABLE `sensitive_words` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(32) NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `word_pinyin` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7160 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sensitive_words
-- ----------------------------
INSERT INTO `sensitive_words` VALUES ('1', '发票', '1', 'fapiao');

-- ----------------------------
-- Table structure for `session`
-- ----------------------------
DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
  `id` varchar(33) NOT NULL,
  `accountId` int(11) NOT NULL,
  `expireAt` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of session
-- ----------------------------

-- ----------------------------
-- Table structure for `share`
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `content` text NOT NULL,
  `createAt` datetime NOT NULL,
  `clickCount` int(11) NOT NULL DEFAULT '0',
  `report` int(11) NOT NULL DEFAULT '0',
  `likeCount` int(11) NOT NULL DEFAULT '0',
  `favoriteCount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of share
-- ----------------------------
INSERT INTO `share` VALUES ('1', '1', '1', '玩转 JFinal 新社区的正确姿势', '<p>JFinal 极速开发新社区于2016年6月6号6点6分6秒正式上线了，社区将提供高品质、专业化的极速开发项目、以及项目的分享与反馈。新社区主要分为项目、分享、反馈三大模块，其用途分别为：</p><h2>1、项目<br></h2><p>发布、收集与 JFinal 极速开发有关的项目，供开发者参考、学习、使用</p><h2>2、分享<br></h2><p>针对于项目，分享有关该项目的一切有价值的知识、代码等等资源，提升开发效率</p><h2>3、反馈<br></h2><p>针对于项目，向作者反馈在使用过程中碰到的问题或者提出改进建议，用户与作者共同打造高水平项目<br></p><h2><span style=\"color: rgb(255, 0, 0);\">用户注意事项：</span></h2><ul class=\" list-paddingleft-2\" style=\"list-style-type: disc;\"><li><p>注册以后换上个人头像有利于社区氛围与文化建设</p></li><li><p>为了保障社区内容的专注与高品质，请支持只发表技术相关内容<br></p></li><li><p><span style=\"font-family: 微软雅黑; font-size: 18px;\">为提升价值、节省开发者时间，</span>低质量、非技术性内容会酌情进行清理，请见谅</p></li></ul><p>&nbsp; &nbsp; JFinal 极度关注为开发者节省时间、提升效率、带来价值，从而会坚持内容的高品质，走少而精的道路，泛娱乐化的与技术无关的内容只会无情地浪费广大开发者有限的生命，请大家支持 JFinal 极速开发社区的价值观！！！</p>', '2066-06-06 06:06:06', '0', '0', '16', '0');
INSERT INTO `share` VALUES ('2', '1', '1', 'JFinal 新社区 share分享栏目', '<h2>乐于分享、传递价值</h2>\n<p>&nbsp; &nbsp; JFinal 新社区分享栏目，用于开发者针对于本站某个项目分享出自己所拥有的有价值的资源，例如实战中具体的代码，项目使用心德、技巧等一切可以为大家节省时间、提升效率的资源。<br></p>', '2066-06-06 06:06:03', '0', '0', '8', '3');
INSERT INTO `share` VALUES ('3', '1', '2', 'JFinal Weixin 1.8 发布，微信极速 SDK', '<p>&nbsp; &nbsp; 离上一次 JFinal weixin 1.7 发布，已经过去了 6 个月。在过去的半年时间里 JFinal Weixin 紧随微信公众平台的演化，不断增加了新的 API，同时也在不断完善原有 API，力求打造一个完备的微信公众平台 SDK，让开发更快速、更开心！</p><p>&nbsp;&nbsp; &nbsp;JFinal Weixin 1.8 共有 27 项新增与改进，新增功能主要有：微信红包接口、微信支付对账单接口、消息转发到指定客服、微信连WIFI联网后下发消息事件、卡券相关事件消息、用户Tag接口、个性化菜单接口等等。1.8 版对原有代码也进行了打磨，例如去除 freemarker 了依赖，截止到今天，此版本是目前市面上 Java 版微信SDK中jar包依赖最少的一个。</p><p>&nbsp; &nbsp; 最后感谢所有对 JFinal Weixin 有贡献的开发者们：@Dreamlu @Javen205 @亻紫菜彡 @osc余书慧 @12叔 @Jimmy哥 @author @Lucare，正是你们无私的奉献让这个世界越来越美好！</p><p><br></p><p>Jar 包下载：<a href=\"http://www.jfinal.com/download?file=jfinal-weixin-1.8-bin-with-src.jar\" target=\"_blank\">http://www.jfinal.com/download?file=jfinal-weixin-1.8-bin-with-src.jar</a></p><p>非 maven 用户获取依赖的 jar包：<a href=\"http://www.jfinal.com/download?file=jfinal-weixin-1.8-lib.zip\" target=\"_blank\">http://www.jfinal.com/download?file=jfinal-weixin-1.8-lib.zip</a></p><p><span style=\"font-family: 微软雅黑; font-size: 18px;\">详细开发文档地址：<a href=\"http://git.oschina.net/jfinal/jfinal-weixin/wikis/home\" target=\"_blank\">http://git.oschina.net/jfinal/jfinal-weixin/wikis/home</a></span></p><p><br></p><p>JFinal Weixin 1.8 Change log&nbsp;</p><p>1：去掉freemarker依赖，感谢@亻紫菜彡的意见&nbsp;</p><p>2：添加个性化菜单接口&nbsp;</p><p>3：添加微信支付对账单接口&nbsp;</p><p>4：添加没有找到对应的消息和事件消息的自定义处理&nbsp;</p><p>5：添加微信连WIFI联网后下发消息事件&nbsp;</p><p>6：fixed客服接口，删除客服帐号&nbsp;</p><p>7：添加获取自动回复规则&nbsp;</p><p>8：更新ReturnCode&nbsp;</p><p>9：新增将消息转发到指定客服&nbsp;</p><p>10：更改pom.xml，打jar包时排除demo目录&nbsp;</p><p>11：添加\"获取在线客服接待信息\"&nbsp;</p><p>12：新增发送图文消息（点击跳转到图文消息页面）&nbsp;</p><p>13：添加微信红包接口，感谢@osc余书慧童鞋的贡献&nbsp;</p><p>14：Bug searchByDevice感谢@12叔&nbsp;</p><p>15：ApiConfig实现序列化，方便缓存感谢@Jimmy哥&nbsp;</p><p>16：企业付款demoWeixinTransfersController感谢@author osc就看看&nbsp;</p><p>17：新增微信支付PC-模式一、模式二demo&nbsp;</p><p>18：添加对okhttp3的支持，修复okhttp2中download误用成httpsClient&nbsp;</p><p>19：添加对直接请求msg接口的异常提示&nbsp;</p><p>20：添加IOutils.toString的字符集参数&nbsp;</p><p>21：修改成maven目录结构&nbsp;</p><p>22：添加卡券相关事件消息&nbsp;</p><p>23：优化xml解析&nbsp;</p><p>24：TemplateData,JsonKit JSON序列化错误&nbsp;</p><p>25：添加用户tag接口&nbsp;</p><p>26：修复AccessToken超时并发问题，感谢@Lucare&nbsp;</p><p>27：添加java doc，详见：<a href=\"http://www.dreamlu.net/jfinal-weixin/apidocs/\">http://www.dreamlu.net/jfinal-weixin/apidocs/</a></p><p><br></p>', '2016-07-11 11:44:30', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for `share_like`
-- ----------------------------
DROP TABLE IF EXISTS `share_like`;
CREATE TABLE `share_like` (
  `accountId` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`accountId`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of share_like
-- ----------------------------

-- ----------------------------
-- Table structure for `share_page_view`
-- ----------------------------
DROP TABLE IF EXISTS `share_page_view`;
CREATE TABLE `share_page_view` (
  `shareId` varchar(25) NOT NULL,
  `visitDate` date NOT NULL,
  `visitCount` int(20) NOT NULL,
  PRIMARY KEY (`shareId`,`visitDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of share_page_view
-- ----------------------------

-- ----------------------------
-- Table structure for `share_reply`
-- ----------------------------
DROP TABLE IF EXISTS `share_reply`;
CREATE TABLE `share_reply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shareId` int(11) NOT NULL,
  `accountId` int(11) NOT NULL,
  `content` text NOT NULL,
  `createAt` datetime NOT NULL,
  `report` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of share_reply
-- ----------------------------

-- ----------------------------
-- Table structure for `task_list`
-- ----------------------------
DROP TABLE IF EXISTS `task_list`;
CREATE TABLE `task_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0初始，1成功，2失败',
  `msg` varchar(1000) DEFAULT '' COMMENT '用substring保证长度不超出范围',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task_list
-- ----------------------------

-- ----------------------------
-- Table structure for `task_run_log`
-- ----------------------------
DROP TABLE IF EXISTS `task_run_log`;
CREATE TABLE `task_run_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `taskName` varchar(50) NOT NULL,
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task_run_log
-- ----------------------------

-- ----------------------------
-- Table structure for `upload_counter`
-- ----------------------------
DROP TABLE IF EXISTS `upload_counter`;
CREATE TABLE `upload_counter` (
  `uploadType` varchar(50) NOT NULL,
  `counter` int(11) NOT NULL,
  `descr` varchar(50) NOT NULL,
  PRIMARY KEY (`uploadType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of upload_counter
-- ----------------------------
INSERT INTO `upload_counter` VALUES ('club', '0', '记录club模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('document', '0', '记录document模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('feedback', '311', '记录feedback模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('project', '70', '记录project模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('share', '195', '记录share模块上传图片的总数量，用于生成相对路径');
