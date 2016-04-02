# ����

�������� API ���� __֪����Zhihu.Inc��__ �ṩ�����ˣ�Izzy Leung����ȡ�������ֶλ�ȡ����ȡ�빲��֮��Ϊ�����ַ�֪��Ȩ������ɡ�������֪��ֹͣ������ʹ�ã����˻ἰʱɾ����ҳ����������Ŀ��  
�����Ž���������������֪��Э�顣

# API ˵��
* ֪���ձ�����Ϣ�� JSON ��ʽ���

* ��ַ�� `api` �����ִ��� API �汾�����߻���;���õ�������Ϣ

* ���ϵĽӿڣ���������ͼ���ȡ��������Ϣ��������Ϣ���н����� 2 �滻Ϊ 1.2 ���Ч����ͬ���滻Ϊ 1.1 ��õ����ϰ汾 API ����� JSON ��ʽ���滻Ϊ���ͣ��� 1.0������ߣ��� 1.3�������ô�����Ϣ��

* �������� API ʹ�õ� HTTP Method ��Ϊ `GET`

# API ����

### 1. ��������ͼ���ȡ
* URL: `http://news-at.zhihu.com/api/4/start-image/1080*1776`  
* `start-image` ��Ϊͼ��ֱ��ʣ��������¸�ʽ  

    * `320*432`
    * `480*728`
    * `720*1184`
    * `1080*1776`  

* ��Ӧʵ����

        {
            text: "? Fido Dido",
            img: "http://p2.zhimg.com/10/7b/107bb4894b46d75a892da6fa80ef504a.jpg"
        }  

* ������
    * `text` : ����ʾ��ͼƬ��Ȩ��Ϣ
    * `img` : ͼ��� URL


### 2. ����汾��ѯ
* Android: `http://news-at.zhihu.com/api/4/version/android/2.3.0`
* iOS: `http://news-at.zhihu.com/api/4/version/ios/2.3.0`
* URL ��󲿷ֵ����ִ�������װ��֪���ձ����İ汾
* ��Ӧʵ����  

    ���Ϊ���°汾ʱ

        {
            "status": 0,
            "latest": "2.2.0"
        }

    ���Ϊ���ϰ汾ʱ

        {
            "status": 1,
            "msg": "���������ݡ������ԣ�",
            "latest": "2.2.0"
        }

* ������
    * `status` : 0 �������Ϊ���°汾��1 ���������Ҫ����
    * `latest` : ������°汾�İ汾�ţ����ֵĵڶ��λ�����µİ汾�ŵ� 1��
    * `msg` : �������������Ҫ�����������£���ʾ�û���������ĶԻ�������ʾ����Ϣ


### 3. ������Ϣ
* URL: `http://news-at.zhihu.com/api/4/news/latest`  
* ��Ӧʵ����

        {
            date: "20140523",
            stories: [
                {
                    title: "�й��Ŵ��Ҿ߷�չ�������������߷壬һ������һ����ĩ����ͼ��",
                    ga_prefix: "052321",
                    images: [
                        "http://p1.zhimg.com/45/b9/45b9f057fc1957ed2c946814342c0f02.jpg"
                    ],
                    type: 0,
                    id: 3930445
                },
            ...
            ],
            top_stories: [
                {
                    title: "�̳��ͺܶ��˼�����ƼҾ�Խ��Խ�ࣨ��ͼ��",
                    image: "http://p2.zhimg.com/9a/15/9a1570bb9e5fa53ae9fb9269a56ee019.jpg",
                    ga_prefix: "052315",
                    type: 0,
                    id: 3930883
                },
            ...
            ]
        }

* ������
    * `date` : ����
    * `stories` : ��������
        * `title` : ���ű���
        * `images` : ͼ���ַ���ٷ� API ʹ��������ʽ��Ŀǰ��δ��ʹ�ö���ͼƬ�����γ��֣�__������__ `images` __���Ե����__������ʹ����ע�� ��
        * `ga_prefix` : �� Google Analytics ʹ��
        * `type` : ����δ֪
        * `id` : `url` �� `share_url` ���������֣�ӦΪ���ݵ� id��
        * `multipic` : ��Ϣ�Ƿ��������ͼƬ���������ڰ�����ͼ�������У�
    * `top_stories` : ���涥�� ViewPager ������ʾ����ʾ���ݣ������ʽͬ�ϣ�


### 4. ��Ϣ���ݻ�ȡ����������
* URL: `http://news-at.zhihu.com/api/4/news/3892357`  
* ʹ���� `������Ϣ` �л�õ� `id`��ƴ���� `http://news-at.zhihu.com/api/4/news/` �󣬵õ���Ӧ��Ϣ JSON ��ʽ������
* ��Ӧʵ����

        {
            body: "<div class="main-wrap content-wrap">...</div>",
            image_source: "Yestone.com ��ȨͼƬ��",
            title: "��ҹ���� �� ����Ȧ���",
            image: "http://pic3.zhimg.com/2d41a1d1ebf37fb699795e78db76b5c2.jpg",
            share_url: "http://daily.zhihu.com/story/4772126",
            js: [ ],
            recommenders": [
                { "avatar": "http://pic2.zhimg.com/fcb7039c1_m.jpg" },
                { "avatar": "http://pic1.zhimg.com/29191527c_m.jpg" },
                { "avatar": "http://pic4.zhimg.com/e6637a38d22475432c76e6c9e46336fb_m.jpg" },
                { "avatar": "http://pic1.zhimg.com/bd751e76463e94aa10c7ed2529738314_m.jpg" },
                { "avatar": "http://pic1.zhimg.com/4766e0648_m.jpg" }
            ],
            ga_prefix: "050615",
            section": {
                "thumbnail": "http://pic4.zhimg.com/6a1ddebda9e8899811c4c169b92c35b3.jpg",
                "id": 1,
                "name": "��ҹ����"
            },
            type: 0,
            id: 4772126,
            css: [
                "http://news.at.zhihu.com/css/news_qa.auto.css?v=1edab"
            ]
        }

* ������
    * `body` : HTML ��ʽ������
    * `image-source` : ͼƬ�������ṩ����Ϊ�˱��ⱻ���߷Ƿ�ʹ��ͼƬ������ʾͼƬʱ��ø������Ȩ��Ϣ��
    * `title` : ���ű���
    * `image` : ��õ�ͼƬͬ `������Ϣ` ��õ�ͼƬ�ֱ��ʲ�ͬ�������õ������������������ʹ�õĴ�ͼ��
    * `share_url` : �����߲鿴����������� SNS �õ� URL
    * `js` : ���ֻ��˵� WebView(UIWebView) ʹ��
    * `recommenders` : ��ƪ���µ��Ƽ���
    * `ga_prefix` : �� Google Analytics ʹ��
    * `section` : ��Ŀ����Ϣ
        * `thumbnail` : ��Ŀ������ͼ
        * `id` : ����Ŀ�� `id`
        * `name` : ����Ŀ������
    * `type` : ���ŵ�����
    * `id` : ���ŵ� id
    * `css` : ���ֻ��˵� WebView(UIWebView) ʹ��
        * ��֪��֪���ձ������������������ WebView(UIWebView) ʵ��

* __�ر�ע��__  
    �ڽ�Ϊ���������£�֪���ձ����ܽ�ĳ�������ձ���վ������������֪���ձ���ҳ��  
    ��Ӧʵ����

        {
            "theme_name": "��Ӱ�ձ�",
            "title": "����Ӷ������ǵĻ����ǳƣ�һ�������衭��",
            "share_url": "http://daily.zhihu.com/story/3942319",
            "js": [],
            "ga_prefix": "052921",
            "editor_name": "�޲�",
            "theme_id": 3,
            "type": 1,
            "id": 3942319,
            "css": [
                "http://news.at.zhihu.com/css/news_qa.6.css?v=b390f"
            ]
        }

    ��ʱ���ص� JSON ����ȱ�� `body`��`iamge-source`��`image`��`js` ���ԡ���� `theme_name`��`editor_name`��`theme_id` �������ԡ�`type` �� `0` ��Ϊ `1`��


### 5. ������Ϣ
* URL: `http://news.at.zhihu.com/api/4/news/before/20131119`  
* __������Ҫ��ѯ 11 �� 18 �յ���Ϣ��__`before` __�������ӦΪ__ `20131119`  
* __֪���ձ�������Ϊ 2013 �� 5 �� 19 �գ���__ `before` __������С��__ `20130520` __��ֻ����յ�����Ϣ__  
* ����Ľ���֮���������Ȼ��ý������ݣ����Ǹ�ʽ��ͬ��������Ϣ�� JSON ��ʽ  
* ��Ӧʵ����

        {
            date: "20131118",
            stories: [
                {
                    title: "��ҹʳ�� �� �ҵ�������",
                    ga_prefix: "111822",
                    images: [
                        "http://p4.zhimg.com/7b/c8/7bc8ef5947b069513c51e4b9521b5c82.jpg"
                    ],
                    type: 0,
                    id: 1747159
                },
            ...
            ]
        }

* ��ʽ��ǰͬ��ˡ����׸��


### 6. ���Ŷ�����Ϣ
* URL: `http://news-at.zhihu.com/api/4/story-extra/#{id}`  
* �������ŵ�ID����ȡ��Ӧ���ŵĶ�����Ϣ������������������ġ��ޡ���������
* ��Ӧʵ����

        {
            "long_comments": 0,
            "popularity": 161,
            "short_comments": 19,
            "comments": 19,
        }

* ������
    * `long_comments` : ����������
    * `popularity` : ��������
    * `short_comments` : ����������
    * `comments` : ��������


### 7. ���Ŷ�Ӧ�����۲鿴
* URL: `http://news-at.zhihu.com/api/4/story/4232852/long-comments`
* ʹ���� `������Ϣ` �л�õ� `id`���� `http://news-at.zhihu.com/api/4/story/#{id}/long-comments` �н� `id` �滻Ϊ��Ӧ�� `id`���õ������� JSON ��ʽ������
* ��Ӧʵ����

        {
            "comments": [
                {
                    "author": "EleganceWorld",
                    "id": 545442,
                    "content": "�Ϻ������ϣ��޾������ŸǷ��� �����ԣ�",
                    "likes": 0,
                    "time": 1413589303,
                    "avatar": "http://pic2.zhimg.com/1f76e6a25_im.jpg"
                },
                ...
            ]
        }

* ������
    * `comments` : �������б���ʽΪ���飨��ע�⣬�䳤�ȿ���Ϊ 0��
        * `author` : ��������
        * `id` : �����ߵ�Ψһ��ʶ��
        * `content` : ���۵�����
        * `likes` : ���������ޡ�������
        * `time` : ����ʱ��
        * `avatar` : �û�ͷ��ͼƬ�ĵ�ַ


### 8. ���Ŷ�Ӧ�����۲鿴
* URL: `http://news-at.zhihu.com/api/4/story/4232852/short-comments`
* ʹ���� `������Ϣ` �л�õ� `id`���� `http://news-at.zhihu.com/api/4/story/#{id}/short-comments` �н� `id` �滻Ϊ��Ӧ�� `id`���õ������� JSON ��ʽ������
* ��Ӧʵ����

        {
            "comments": [
                {
                    "author": "Xiaole˵",
                    "id": 545721,
                    "content": "�ͳ��˸������ף��Ǻ�",
                    "likes": 0,
                    "time": 1413600071,
                    "avatar": "http://pic1.zhimg.com/c41f035ab_im.jpg"
                },
                ...
            ]
        }

* ��ʽ��ǰͬ��ˡ����׸��


### 9. �����ձ��б�鿴
* URL: `http://news-at.zhihu.com/api/4/themes`
* ��Ӧʵ����

        {
            "limit": 1000,
            "subscribed": [ ],
            "others": [
                {
                    "color": 8307764,
                    "thumbnail": "http://pic4.zhimg.com/2c38a96e84b5cc8331a901920a87ea71.jpg",
                    "description": "������֪���û��Ƽ��������������Ȥζ�������",
                    "id": 12,
                    "name": "�û��Ƽ��ձ�"
                },
                ...
            ]
        }

    * ������
        * `limit` : ������Ŀ֮���ƣ���Ϊ�²⣩
        * `subscribed` : �Ѷ�����Ŀ
        * `others` : ������Ŀ
            * `color` : ��ɫ������δ֪
            * `thumbnail` : ����ʾ��ͼƬ��ַ
            * `description` : �����ձ��Ľ���
            * `id` : �������ձ��ı��
            * `name` : ����ʾ�������ձ�����


### 10. �����ձ����ݲ鿴
* URL: `http://news-at.zhihu.com/api/4/theme/11`
* ʹ���� `�����ձ��б�鿴` �л����Ҫ�鿴�������ձ��� `id`��ƴ���� `http://news-at.zhihu.com/api/4/theme/` �󣬵õ���Ӧ�����ձ� JSON ��ʽ������
* ��Ӧʵ����

        {
            stories: [
                {
                    images: [
                        "http://pic1.zhimg.com/84dadf360399e0de406c133153fc4ab8_t.jpg"
                    ],
                    type: 0,
                    id: 4239728,
                    title: "ǰ������������ٿ�ͼ��"
                },
                ...
            ],
            description: "Ϊ�㷢������Ȥ�������£������� WiFi �²鿴",
            background: "http://pic1.zhimg.com/a5128188ed788005ad50840a42079c41.jpg",
            color: 8307764,
            name: "��������",
            image: "http://pic3.zhimg.com/da1fcaf6a02d1223d130d5b106e828b9.jpg",
            editors: [
                {
                    url: "http://www.zhihu.com/people/wezeit",
                    bio: "΢�� Wezeit ����",
                    id: 70,
                    avatar: "http://pic4.zhimg.com/068311926_m.jpg",
                    name: "�濵Ŵ��"
                },
                ...
            ],
            image_source: ""
        }

    * ������
        * `stories` : �������ձ��е������б�
            * `images` : ͼ���ַ��������Ϊ���顣�������ڴ����д����޸����������鳤��Ϊ 0 �������
            * `type` : ���ͣ�����δ֪
            * `title` : ��Ϣ�ı���
        * `description` : �������ձ��Ľ���
        * `background` : �������ձ��ı���ͼƬ����ͼ��
        * `color` : ��ɫ������δ֪
        * `name` : �������ձ�������
        * `image` : ����ͼƬ��Сͼ�汾
        * `editors` : �������ձ��ı༭�����û��Ƽ��ձ����д����ָ��һ�������飬�� App �е���������ʾΪ������ˡ����������ʸ������ձ��Ľ���ҳ�棬�����⣩
            * `url` : �����֪���û���ҳ
            * `bio` : ����ĸ��˼��
            * `id` : ���ݿ��е�Ψһ��ʾ��
            * `avatar` : �����ͷ��
            * `name` : ���������
        * `image_source` : ͼ��İ�Ȩ��Ϣ


### 11. ������Ϣ
* __��ע�⣡__ �� API �Կɷ��ʣ�����������δ���������µġ�֪���ձ��� App �С�
* URL: `http://news-at.zhihu.com/api/3/news/hot`  
* ��Ӧʵ����

        {
            recent: [
                {
                    news_id: 3748552,
                    url: "http://daily.zhihu.com/api/2/news/3748552",
                    thumbnail: "http://p3.zhimg.com/67/6a/676a8337efec71a100eea6130482091b.jpg",
                    title: "����Ư�����������Ը񵥴��Ĺ���Ϊʲô��û�������ѣ�"
                },
            ...
            ]
        }

* ����ͬǰ����ܵ� API ���ƣ�Ψһ��Ҫע����ǣ������ͼƬ��ַ������ʹ�� `image` ���� `thumbnail` ����
* `url` ���Կ�ֱ��ʹ�á���ע�⣬`url` �е� `api` ����Ϊ __2__���ǽ��ϰ汾��


### 12. ����ƹ�
* __��ע�⣡__ �� API ���޷����ʣ������������������ڡ�֪���ձ��� App �С�
* Android: `http://news-at.zhihu.com/api/3/promotion/android`  
* iOS: `http://news-at.zhihu.com/api/3/promotion/ios`


### 13. ��Ŀ����
* __��ע�⣡__ �� API �Կɷ��ʣ�����������δ���������µġ�֪���ձ��� App �С�
* URL: `http://news-at.zhihu.com/api/3/sections`  
* ��Ӧʵ����

        {
            data: [
                {
                    id: 1,
                    thumbnail: "http://p2.zhimg.com/10/b8/10b8193dd6a3404d31b2c50e1e232c87.jpg",
                    name: "��ҹʳ��",
                    description: "˯ǰ��ҹ���ñ��˵Ĺ����¾�"
                },
            ...
            ]
        }

* ͬ����ע��ʹ�� `thumbnail` ��ȡͼ��ĵ�ַ


### 14. ��Ŀ������Ϣ�鿴
* __��ע�⣡__ �� API �Կɷ��ʣ�����������δ���������µġ�֪���ձ��� App �С�
* URL: `http://news-at.zhihu.com/api/3/section/1`
* URL �������ּ�����Ŀ����������Ӧ��Ŀ�� `id` ����
* ��Ӧʵ����

        {
            news: [
                {
                    date: "20140522",
                    display_date: "5 �� 22 ��"
                },
            ...
            ],
            name: "��ҹʳ��",
            timestamp: 1398780001
        }

* ��ǰ��`http://news-at.zhihu.com/api/3/section/1/before/1398780001`
    * �� URL ������һ��ʱ�����ʱ������ JSON ����ĩ�˵� `timestamp` ����
