package com.juice.timetable.data.parse;

import com.juice.timetable.data.bean.Course;
import com.juice.timetable.utils.ReadFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : soreak
 *     e-mail : sorea1k@163.com
 *     time   : 2020/04/28
 *     desc   : nothing
 *     item   : juice
 *     version: 1.0
 * </pre>
 */

public class ParseAllWeek {
    private static List<Course> couList = new ArrayList<>();

    /**
     * 解析完整的课表
     */
    public static void parseAllCourse() {
        //从文档中导入完整课表txt
        String i = ReadFile.readToString("C:\\Users\\14989\\Desktop\\网页内容\\完整课表.html");
        //Jsoup解析
        Document document = Jsoup.parse(i);
        //从div中提取出文本，内容是课表与名字
        String title = document.getElementsByTag("div").eq(0).text();
        System.out.println(title);

        ///将table左边的表格标签里的内容提取（课程名，老师，起始结束周）
        Elements leftTable = document.getElementsByTag("td").eq(1);


        for (Element el : leftTable) {
            Integer len_Tr = el.getElementsByTag("tr").size();
            //System.out.println(len_Tr);
            for (int a = 2; a < len_Tr; a++) {
                Elements oneCourse = el.getElementsByTag("tr").eq(a);
                //System.out.println(oneCourse.html());
                for (Element ele : oneCourse) {
                    Course course = new Course();
                    if (ele.getElementsByTag("td").size() < 3) {
                        break;
                    }
                    String couName = ele.getElementsByTag("td").eq(0).text();
                    course.setCouName(couName);
                    //
                    if (!"".equals(ele.getElementsByTag("td").eq(2).text())) {
                        String couTeacher = ele.getElementsByTag("td").eq(2).text();
                        course.setCouTeacher(couTeacher);
                    }
                    if (!"".equals(ele.getElementsByTag("td").eq(10).text())) {
                        //System.out.println(ele.getElementsByTag("td").eq(10).text());
                        Integer couStartWeek = Integer.valueOf(ele.getElementsByTag("td").eq(10).text().split("～")[0]);
                        course.setCouStartWeek(couStartWeek);
                        Integer couEndWeek = Integer.valueOf(ele.getElementsByTag("td").eq(10).text().split("～")[1]);
                        course.setCouEndWeek(couEndWeek);
                    }
                    couList.add(course);


                }
            }
        }
        Long couID = 0L;
        for (Course cou : couList) {
//            System.out.println(cou);
            cou.setCouID(couID);
            cou.setOnlyID(couID);
            couID++;

        }

        //将table左边的表格标签里的内容提取（）
        Elements rightTable = document.getElementsByTag("tbody").eq(3);

        for (Element element1 : rightTable) {
            Integer len_Tr1 = element1.getElementsByTag("tr").size();
            for (int l = 1; l < len_Tr1; l++) {
                Elements ele1 = element1.getElementsByTag("tr").eq(l);
                //System.out.println(ele1.html());
                for (Element el1 : ele1) {

                    Integer len_Td1 = el1.getElementsByTag("td").size();
                    for (int j = 1; j < len_Td1; j++) {
                        //去除为空的课程
                        if (!"".equals(el1.getElementsByTag("td").eq(j).text())) {
                            String tr = el1.getElementsByTag("td").eq(j).html();
                            //tr标签中td的数量
                            //String tr = el1.getElementsByTag("td").html();
                            //System.out.println(e1.html());
                            Integer len_Br = tr.split("<br>").length;
                            //System.out.println(tr.split("<br>")[0]+"======");
                            for (int a = 0; a < len_Br; a++) {
                                if (tr.split("<br>")[a].contains("班")) {
                                    String couname = tr.split("<br>")[a];
                                    //使用list对课程名字进行判断，相同的名字存储在同一个list

                                    Course course = null;
                                    // 循环List 找到 本轮解析中对应的课程对象
                                    for (Course cou : couList) {
                                        String parseName = couname.replace(" ", "");
                                        String listCouName = cou.getCouName().replace(" ", "");
                                        if (parseName.equals(listCouName)) {
                                            course = cou;
                                        }
                                    }
                                    // 如果没找到 跳出本轮循环
                                    if (course == null) {
                                        continue;
                                    }
                                    if (tr.split("<br>")[a + 1].contains("[单]")) {
                                        course.setCouWeekType(1);
                                        //使用list后，对是否已经输入过教室进行判断，无则输入，有则重新开一个list存储
                                        String couRoom = tr.split("<br>")[a + 1].substring(4, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    } else if (tr.split("<br>")[a + 1].contains("[双]")) {
                                        course.setCouWeekType(2);
                                        String couRoom = tr.split("<br>")[a + 1].substring(4, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    } else {
                                        course.setCouWeekType(0);
                                        String couRoom = tr.split("<br>")[a + 1].substring(1, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    }
                                    String id = el1.getElementsByTag("td").eq(j).attr("id");
                                    Integer couWeek = Integer.valueOf(id.substring(id.length() - 1, id.length()));
                                    course.setCouWeek(couWeek);
                                    Integer couStartNode = Integer.valueOf(id.substring(0, id.length() - 1));
                                    course.setCouStartNode(couStartNode);

                                    Integer time = Integer.valueOf(el1.getElementsByTag("td").eq(j).attr("rowspan"));
                                    Integer couEndNode = couStartNode + time - 1;
                                    course.setCouEndNode(couEndNode);
                                }


                            }

                        }


                    }
                }
            }
        }


        // 解析结束
        for (Course course : couList) {
            System.out.println(course);
        }
    }


}