package com.github.catvod.spider;

import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.spider.merge.G;
import com.github.catvod.spider.merge.GC;
import com.github.catvod.spider.merge.Id;
import com.github.catvod.spider.merge.g9;
import com.github.catvod.spider.merge.wb;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class DiDuan extends Spider {
    private Pattern Z = Pattern.compile("\"(https://ddrk.me/[^/]+/)");
    private Pattern n7 = Pattern.compile("^https://ddrk.me/([^/]+)/");
    private Pattern R = Pattern.compile("url\\(([^\\)]+)\\)");
    private Pattern vL = Pattern.compile("/page/(\\d+)/");

    protected HashMap<String, String> Z() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36");
        return hashMap;
    }

    public String categoryContent(String str, String str2, boolean z, HashMap<String, String> hashMap) {
        try {
            wb Z = g9.Z(G.Oa("https://ddrk.me/category/" + str + "/page/" + str2, Z()));
            JSONObject jSONObject = new JSONObject();
            int parseInt = Integer.parseInt(str2);
            GC jR = Z.jR(".page-numbers");
            int i = parseInt;
            for (int i2 = 0; i2 < jR.size(); i2++) {
                Matcher matcher = this.vL.matcher(((Id) jR.get(i2)).R("href"));
                if (matcher.find()) {
                    Integer valueOf = Integer.valueOf(Integer.parseInt(matcher.group(1)));
                    if (valueOf.intValue() > i) {
                        i = valueOf.intValue();
                    }
                }
            }
            GC jR2 = Z.jR("article");
            JSONArray jSONArray = new JSONArray();
            for (int i3 = 0; i3 < jR2.size(); i3++) {
                Id id = (Id) jR2.get(i3);
                Matcher matcher2 = this.n7.matcher(id.R("data-href"));
                if (matcher2.find()) {
                    String group = matcher2.group(1);
                    Matcher matcher3 = this.R.matcher(id.F(".post-box-image").R("style"));
                    if (matcher3.find()) {
                        String group2 = matcher3.group(1);
                        String trim = id.F(".post-box-title").i().trim();
                        String trim2 = id.F(".post-box-meta").i().trim();
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("vod_id", group);
                        jSONObject2.put("vod_name", trim);
                        jSONObject2.put("vod_pic", group2);
                        jSONObject2.put("vod_remarks", trim2);
                        jSONArray.put(jSONObject2);
                    }
                }
            }
            jSONObject.put("page", parseInt);
            jSONObject.put("pagecount", i);
            jSONObject.put("limit", 28);
            jSONObject.put("total", i * 28);
            jSONObject.put("list", jSONArray);
            return jSONObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            SpiderDebug.log(e);
            return "";
        }
    }

    public String detailContent(List<String> list) {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            String str = list.get(0);
            jSONObject2.put("vod_id", str);
            Id Z = g9.Z(G.Oa("https://ddrk.me/" + str + "/", Z()));
            jSONObject2.put("vod_name", Z.F("h1.post-title").i().trim());
            jSONObject2.put("vod_pic", Z.F("div.post > img").R("src"));
            jSONObject2.put("vod_remarks", Z.F("time").i().trim());
            for (String str2 : Z.F("div.abstract").d().split("\n")) {
                String replace = str2.replace("<br>", "").replace("<p></p>", "");
                if (replace.indexOf("类型") == 0) {
                    jSONObject2.put("type_name", replace.substring(4));
                } else if (replace.indexOf("导演") == 0) {
                    jSONObject2.put("vod_director", replace.substring(4));
                } else if (replace.indexOf("演员") == 0) {
                    jSONObject2.put("vod_actor", replace.substring(4));
                } else if (replace.indexOf("年份") == 0) {
                    jSONObject2.put("vod_year", replace.substring(4));
                } else if (replace.indexOf("制片国家/地区") == 0) {
                    jSONObject2.put("vod_area", replace.substring(8));
                } else if (replace.indexOf("简介") == 0) {
                    jSONObject2.put("vod_content", replace.substring(4));
                }
            }
            Iterator it = Z.jR(".post-page-numbers").iterator();
            int i = 1;
            while (it.hasNext()) {
                int parseInt = Integer.parseInt(((Id) it.next()).i().trim());
                if (parseInt > i) {
                    i = parseInt;
                }
            }
            ArrayList arrayList = new ArrayList();
            for (int i2 = 1; i2 <= i; i2++) {
                JSONArray jSONArray = new JSONObject((i2 == 1 ? Z : g9.Z(G.Oa("https://ddrk.me/" + str + "/" + i2 + "/", Z()))).jR("script.wp-playlist-script").vL()).getJSONArray("tracks");
                for (int i3 = 0; i3 < jSONArray.length(); i3++) {
                    JSONObject jSONObject3 = jSONArray.getJSONObject(i3);
                    arrayList.add(jSONObject3.getString("caption") + "$" + jSONObject3.getString("src1"));
                }
            }
            jSONObject2.put("vod_play_from", "国内节点");
            jSONObject2.put("vod_play_url", TextUtils.join("#", arrayList));
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(jSONObject2);
            jSONObject.put("list", jSONArray2);
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public String homeContent(boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("type_id", "drama");
            jSONObject2.put("type_name", "剧集");
            jSONArray.put(jSONObject2);
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("type_id", "movie");
            jSONObject3.put("type_name", "电影");
            jSONArray.put(jSONObject3);
            JSONObject jSONObject4 = new JSONObject();
            jSONObject4.put("type_id", "anime");
            jSONObject4.put("type_name", "动画");
            jSONArray.put(jSONObject4);
            JSONObject jSONObject5 = new JSONObject();
            jSONObject5.put("type_id", "documentary");
            jSONObject5.put("type_name", "纪录片");
            jSONArray.put(jSONObject5);
            jSONObject.put("class", jSONArray);
            GC jR = g9.Z(G.Oa("https://ddrk.me", Z())).jR("article");
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jR.size(); i++) {
                Id id = (Id) jR.get(i);
                Matcher matcher = this.n7.matcher(id.R("data-href"));
                if (matcher.find()) {
                    String group = matcher.group(1);
                    Matcher matcher2 = this.R.matcher(id.F(".post-box-image").R("style"));
                    if (matcher2.find()) {
                        String group2 = matcher2.group(1);
                        String trim = id.F(".post-box-title").i().trim();
                        String trim2 = id.F(".post-box-meta").i().trim();
                        JSONObject jSONObject6 = new JSONObject();
                        jSONObject6.put("vod_id", group);
                        jSONObject6.put("vod_name", trim);
                        jSONObject6.put("vod_pic", group2);
                        jSONObject6.put("vod_remarks", trim2);
                        jSONArray2.put(jSONObject6);
                    }
                }
            }
            jSONObject.put("list", jSONArray2);
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public String playerContent(String str, String str2, List<String> list) {
        try {
            JSONObject jSONObject = new JSONObject(G.Oa("https://v3.ddrk.me:19443/video?id=" + str2 + "&type=mix", Z()));
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("parse", "0");
            jSONObject2.put("playUrl", "");
            jSONObject2.put("url", jSONObject.getString("url"));
            jSONObject2.put("header", "");
            return jSONObject2.toString();
        } catch (Exception e) {
            e.printStackTrace();
            SpiderDebug.log(e);
            return "";
        }
    }

    public String searchContent(String str, boolean z) {
        try {
            GC jR = g9.Z(G.Oa("https://www.sogou.com/web?query=site%3Addrk.me+" + URLEncoder.encode(str), Z())).jR("div.results > div.vrwrap");
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < jR.size(); i++) {
                Id id = (Id) jR.get(i);
                Id F = id.F("h3.vr-title");
                if (F != null && F.i().trim().contains(str)) {
                    Matcher matcher = this.Z.matcher(g9.Z(G.Oa("https://www.sogou.com" + id.F("a").R("href"), Z())).d());
                    if (matcher.find()) {
                        String group = matcher.group(1);
                        wb Z = g9.Z(G.Oa(group, Z()));
                        Matcher matcher2 = this.n7.matcher(group);
                        if (matcher2.find()) {
                            String group2 = matcher2.group(1);
                            String trim = Z.F("h1.post-title").i().trim();
                            String R = Z.F("div.post > img").R("src");
                            String trim2 = Z.F("time").i().trim();
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put("vod_id", group2);
                            jSONObject.put("vod_name", trim);
                            jSONObject.put("vod_pic", R);
                            jSONObject.put("vod_remarks", trim2);
                            jSONArray.put(jSONObject);
                        }
                    }
                }
            }
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("list", jSONArray);
            return jSONObject2.toString();
        } catch (Exception e) {
            e.printStackTrace();
            SpiderDebug.log(e);
            return "";
        }
    }
}
