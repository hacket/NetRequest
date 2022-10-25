# 基于Volley+Okhttp+Gson的网络请求封装

## 1、解决的问题
用于解决如下json模板解析的问题，不用每个数据bean都定义s,d,n等信息，统一定义在`BaseResponse`中
```json
{
    "s": 1,
    "d":{},
    "n": "794c0977f9bb1808b7f2d69009a635ba"
}
```
或者
```json
{
    "s": 1,
    "d":[],
    "n": "794c0977f9bb1808b7f2d69009a635ba"
}
```

## 2、提供的功能
1. GET、POST网络请求

2. 对请求进行优先级的排序

3. 网络请求的缓存和缓存的清除

4. 多级别取消请求（通过tag）

5. 和Activity生命周期的联动（Activity结束时同时取消所有网络请求）

#### 3、简单使用
**d为jsonnobj情况：**
```json
{
    "s": 1,
    "d":{
        "summary": "JAKARTA - Direktorat Tindak Pidana Narkoba Bareskrim Polri  belum lama ini menggulung sindikat narkoba internasional. Modus yang digunakan sindikat itu untuk memasukkan barang haram ke Indonesia bisa dibilang baru. ...",
        "feed": "JPNN",
        "editMode": false,
        "clickNum": 10624,
        "startTime": 0,
        "id": "8289b07c2eeb4b038d8bc3a4bfab6251",
        "homeImage": "http://s.mobile-global.baidu.com/mbrowser/mbrowser-news/id/images/5993df63fdf34b588e5480729a5373ff.jpg",
        "endTime": 0,
        "updated": "1460535485000"
    },
    "n": "794c0977f9bb1808b7f2d69009a635ba"
}
```
对应的代码：
```java
public void gsonGet() {
    String url = "http://192.168.221.105:8080/demo_obj.json";
    Map<String, String> params = new HashMap<>();
    params.put("userget", "hacket-get");
    NetUtil.getInstance().requestAsyncGet(url, params, true, ObjData.class, "request_tag",
            new NetCallback<BaseResponse<ObjData>>() {
                @Override
                public void onSuccess(String url,
                                      BaseResponse<ObjData> response) {
                    LogUtil.i(TAG, "get onsuccess");
                    ObjData data = response.data;
                    String md5 = response.md5;
                    LogUtil.i(TAG, "md5:" + md5);
                    LogUtil.i(TAG, "data:" + data);
                }

                @Override
                public void onFailed(String url, String errorMsg) {

                }
            });
}
```

**d为jsonarray情况：**
```json
{
    "s": 1,
    "d": [
        {
            "subIcon": "http://s.mobile-global.baidu.com/mbrowser/guanxing/SecondHome/imgs/2f47ce7b6d294915abe9cbbbf2103739.png",
            "size": "1",
            "name": "Sepak Bola",
            "subType": "",
            "style": "",
            "position": "4",
            "type": "k",
            "isForceAdd": "y",
            "key": "11000"
        },
        {
            "subIcon": "",
            "size": "1",
            "name": "Olahraga",
            "subType": "",
            "style": "5",
            "position": null,
            "type": "n",
            "isForceAdd": "n",
            "key": "47"
        },
        {
            "subIcon": "http://s.mobile-global.baidu.com/mbrowser/guanxing/SecondHome/imgs/c97bc398eec14e71b8096873d717ac36.png",
            "size": "1",
            "name": "Internasional",
            "subType": "",
            "style": "5",
            "position": null,
            "type": "n",
            "isForceAdd": "n",
            "key": "36"
        },
        {
            "subIcon": "http://s.mobile-global.baidu.com/mbrowser/guanxing/SecondHome/imgs/c73a232f076741e2ad537b04b43461d0.png",
            "size": "1",
            "name": "Buzzword",
            "subType": "",
            "style": "",
            "position": "x",
            "type": "w",
            "isForceAdd": "n",
            "key": "10000"
        }
    ],
    "n": "527b54efa9e6b63ddd4d71a1161d80c9"
}
```
还支持通过tag请求的取消；设置请求优先级的；同步请求；缓存的清除

## License

```
Copyright 2016 hacket

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
