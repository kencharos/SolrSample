Solr sample
-----------

## Abstraction

Apache Solr and Spring boot Sample Application.

書籍および著者に関するデータを、Apache Solr で検索するサンプル。
お蔵入りしたので公開。

## 環境構築

+ 実行のためには、jdk1.8 および solr5.0 が必要なので、入れてください。
+ Solrは、solr_docs配下のファイルを Solrインストールディレクトリの  `server/solr/<任意のコア名>` フォルダを作って置いて、Solrコアを追加する(次節参照)
+ `SolrSampleApplication` クラスに 上記の Solr コアの URL を記述しているので、合わせて修正する。
+ `mvn package` で出力される SolrSamplexxx.jarを`java -jar SolrSample.xx.jar` で起動すればOK。
+ h2のデータベースは、 ユーザーディレクトリ直下の  `SolrDB` になる。
+ データベースのバックアップとコマンドは dbフォルダ参照。

## Solr について

+ solr の起動
	solrのインストールフォルダ(c:\solr-5.0.0など)に移動して、
	` bin/solr start`   
	停止は、 `bin/solr stop --all` なのだが、停止しない場合は、タスクマネージャなどでプロセスを停止。
	
+ solr コアの追加
	コア1つでsolrのスキーマ定義が一つ持てる。
	http://localhost:8983/solr/ にアクセスし、コアの追加を行う。
	コア定義は `<solrディレクトリ>/server/solr`にコア名のフォルダを作って、そこに各種ファイルを置く。
	各種ファイルは、`<solrディレクトリ>/server/configset`などに雛形がある。

## solrのスキーマ定義

+ name, contents,author - いずれも形態素解析を行う text_ja型。
	nameはタイトル、概要など。contentsは説明とか長文を対象。
	3つのフィールドをコピーフィールドとして、textにコピーすることで、
	textのみで全フィールドを検索可能にする。
	
+ category, sub_category - string型。ファセットの表示で使用し、
	categoryは、書籍とか著者とか。sub_categoryは、書籍なら紙とか電子書籍とか。
	
+　id, version 。必須フィールド。idがユニークになるように、キーワード + / + テーブルのID という風にしている。
	+ ついでにそのまま URLにできることも狙った。
+ 他 - dynamicフィールドを任意で追加可能。*_iならint型とか。

### solr-config
+ 自動補完用のautocomplete_ja　の定義がある。
　　どのフィールドに対して自動補完をさせるかなどの指定が可能。

### 課題とその解消

+ カタカナ・ひらがなは一緒にできない。
	+ kana フィールドを定義し、マッピングでひらがなもカタカナに寄せる。
	+ kana は text_cjkとして、N-Gramにすることで、Like 検索に近い検索ができるようになる。

+ スペースで区切った条件が OR なんだけど。
	+ solrconfigの /select のデフォルト値に、 q.op = AND を設定する。
	+ tokenizer が分解した単語もデフォルトでは OR となる。こちらは、text_jaのフィールドタイプを修正すると対応できる。


## spring-bootの注意点

+ tymeleafは製形式XMLである必要がある。jsを書く場合など注意。
+ tymeleafの変更時リロードを可能にするには、プロパティファイルでcacheeをオフにする。
+ ソースコードのホットリロードを行うには、 spring-load.jarを入手し、
 -javaagentオプションをVM引数に追加する。 `-javaagent:hot/springloaded-1.2.3.RELEASE.jar -noverify`
 
 http://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html





