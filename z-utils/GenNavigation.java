import javafx.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : heibaiying
 * @description : 用于生成README.md导航目录的工具类
 */
public class GenNavigation {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("请传递路径");
            return;
        }

        String dir = args[0];

        List<String> filesList = getAllFile(dir, new ArrayList<>());
        for (String filePath : filesList) {
            //  获取文件内容
            String content = getContent(filePath);
            // 获取全部标题
            List<Pair<String, String>> allTitle = getAllTitle(content);
            // 生成导航
            String nav = genNav(allTitle);
            // 写出并覆盖原文件
            write(filePath, content, nav);
        }
        System.out.println("生成目录成功！");
    }

    private static void write(String filePath, String content, String nav) {
        try {
            String newContent = "";
            if (content.contains("## 目录") && content.contains("## 正文<br/>")) {
                // 如果原来有目录则替换
                newContent = content.replaceAll("(?m)(## 目录[\\s\\S]*## 正文<br/>)", nav);
            } else {
                StringBuilder stringBuilder = new StringBuilder(content);
                // 如果原来没有目录，则title和正文一个标题间写入
                int index = content.indexOf("## ");
                stringBuilder.insert(index - 1, nav);
                newContent = stringBuilder.toString();
            }
            // 写出覆盖文件
            FileWriter fileWriter = new FileWriter(new File(filePath));
            fileWriter.write(newContent);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String genNav(List<Pair<String, String>> flagAndTitles) {
        StringBuilder builder = new StringBuilder();
        // 目录头
        builder.append("## 目录<br/>\n");
        for (Pair<String, String> ft : flagAndTitles) {
            String flag = ft.getKey();
            String title = ft.getValue();
            builder.append(genBlank(flag.length() - 2, 4));
            // Github有效目录格式: <a href="#21-预备">页面锚点</a>  url中不能出现特殊符号
            String formatTitle = title.trim().replaceAll("[.():：（）|、,，@。]", "").replace(" ", "-");
            builder.append(String.format("<a href=\"%s\">%s</a><br/>\n", "#" + formatTitle, title));
        }
        // 目录尾
        builder.append("## 正文<br/>\n");
        return builder.toString();
    }

    private static String genBlank(int i, int scale) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < i; j++) {
            for (int k = 0; k < scale; k++) {
                builder.append("&nbsp;");
            }
        }
        return builder.toString();
    }

    private static List<Pair<String, String>> getAllTitle(String content) {
        List<Pair<String, String>> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?m)^(#{2,10})\\s?(.*)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String group2 = matcher.group(2);
            if (!group2.contains("目录") && !group2.contains("正文")) {
                list.add(new Pair<>(matcher.group(1), group2));
            }
        }
        return list;
    }

    private static String getContent(String filePath) {
        StringBuilder builder = new StringBuilder();

        try {
            FileReader reader = new FileReader(filePath);
            char[] chars = new char[1024 * 1024];

            int read = 0;
            while ((read = reader.read(chars)) != -1) {
                builder.append(new String(chars, 0, read));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static List<String> getAllFile(String dir, List<String> filesList) {
        File file = new File(dir);
        //如果是文件 则不遍历
        if (file.isFile() && file.getName().endsWith(".md")) {
            filesList.add(file.getAbsolutePath());
        }
        //如果是文件夹 则遍历下面的所有文件
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    getAllFile(f.getAbsolutePath(), filesList);
                } else if (f.getName().endsWith(".md")) {
                    filesList.add(f.getAbsolutePath());
                }
            }
        }
        return filesList;
    }
}
