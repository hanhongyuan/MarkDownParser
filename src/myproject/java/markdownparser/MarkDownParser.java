package myproject.java.markdownparser;

import java.io.*;
import java.util.*;

public class MarkDownParser {
    // �洢ȥ����׺���� markdown �ļ���
    private String fileName = null;
    // ���д洢 markdown �ļ�
    private ArrayList<String> mdList = new ArrayList();
    // �洢 markdown �ļ���ÿһ�ж�Ӧ����
    private ArrayList<String> mdListType = new ArrayList();

    /**
     * �ṩ 2 �ֹ��췽�������½���ʱ�ṩ�� markdown �ļ�������������� readMarkdownFile ���������ļ�
     * �������������� readMarkdownFile ���������ļ�
     */
    public MarkDownParser() {}
    public MarkDownParser(String fileName) {
        readMarkdownFile(fileName);
    }
    
    /**
     * ͨ���ļ�����ȡһ�� markdown �ļ�
     * @param һ�� String ���͵��ļ���
     * @return ����ļ����ڣ����� true�����򣬷���false
     */
    public boolean readMarkdownFile(String fileName) {
        try {
            mdList.clear();
            mdListType.clear();
            
            fileName = fileName.trim();
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader dis = new InputStreamReader(fis, "UTF-8");
            BufferedReader mdFile = new BufferedReader(dis);
            this.fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
            
            // ��ȡ makedown �ļ�
            String mdLine;
            mdList.add(" ");
            while((mdLine = mdFile.readLine()) != null) {
                if(mdLine.isEmpty()){
                    mdList.add(" ");
                }
                else {
                    mdList.add(mdLine);
                }
            }
            mdList.add(" ");
            mdFile.close();
            return true;
        }
        catch(IOException e) {
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * ����һ�� html �ļ����������ת����� html ���
     * @param һ�� String ���͵��ļ���
     * @return ����ļ����ڣ����� true�����򣬷���false
     */
    public boolean createHtmlFile() {
        return createHtmlFile(fileName + ".html");
    }
    private boolean createHtmlFile(String fileName) {
        defineAreaType();
        defineLineType();
        translateToHtml();
        try {
            fileName = fileName.trim();
            FileOutputStream fis = new FileOutputStream(fileName);
            OutputStreamWriter dis = new OutputStreamWriter(fis, "UTF-8");
            BufferedWriter htmlFile = new BufferedWriter(dis);
            
            // д�� html ͷ��
            htmlFile.write("<!DOCTYPE html>");
            htmlFile.newLine();
            htmlFile.write("<html>"); 
            htmlFile.newLine();
            htmlFile.write("<head>"); 
            htmlFile.newLine();
            htmlFile.write("<title>"+ fileName + "</title>");
            htmlFile.newLine();
            htmlFile.write("<link type='text/css' rel='stylesheet' href='main.css'/>");
            htmlFile.newLine(); 
            htmlFile.write("</head>");
            htmlFile.newLine();
            htmlFile.write("<body>");
            htmlFile.newLine();
            
            // д�� html ����
            if(mdListType.size() == mdList.size()) {
                for(int i = 0; i < mdList.size(); i++){
                    /*htmlFile.write(mdListType.get(i));
                    for(int j = 20 - mdListType.get(i).length(); j>=0 ;j--) {
                        htmlFile.write(".");
                    }
                    htmlFile.write(mdList.get(i));*/
                    htmlFile.write( mdList.get(i));
                    htmlFile.newLine();
                }
            }

            // д�� html β��
            htmlFile.write("</body>");
            htmlFile.newLine();
            htmlFile.write("</html>"); 
            
            htmlFile.flush();
            htmlFile.close();
            return true;
        }
        catch(IOException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * �ж�ÿһ�� markdown �﷨��Ӧ�� html ����
     * @param ��
     * @return ��
     */
    private void defineAreaType() {
        // ��λ������
        ArrayList<String> tempList = new ArrayList();
        ArrayList<String> tempType = new ArrayList();
        tempType.add("OTHER");
        tempList.add(" ");
        boolean codeBegin = false, codeEnd = false;
        for(int i = 1; i < mdList.size() - 1; i++){
            String line = mdList.get(i);
            if(line.length() > 2 && line.charAt(0) == '`' && line.charAt(1) == '`' && line.charAt(2) == '`') {
                // ���������
                if(!codeBegin && !codeEnd) {
                    tempType.add("CODE_BEGIN");
                    tempList.add(" ");
                    codeBegin = true;
                }
                // �뿪������
                else if(codeBegin && !codeEnd) {
                    tempType.add("CODE_END");
                    tempList.add(" ");
                    codeBegin = false;
                    codeEnd = false;
                }
                else {
                    tempType.add("OTHER");
                    tempList.add(line);
                }
            }
            else {
                tempType.add("OTHER");
                tempList.add(line);
            }
        }
        tempType.add("OTHER");
        tempList.add(" ");

        mdList = (ArrayList<String>)tempList.clone();
        mdListType = (ArrayList<String>)tempType.clone();
        tempList.clear();
        tempType.clear();

        // ��λ��������ע�����������������ʽ
        boolean isCodeArea = false;
        tempList.add(" ");
        tempType.add("OTHER");
        for(int i = 1; i < mdList.size() - 1; i++){
            String line = mdList.get(i);
            String lastLine = mdList.get(i - 1);
            String nextLine = mdList.get(i + 1);

            if(mdListType.get(i) == "CODE_BEGIN") {
                isCodeArea = true;
                tempList.add(line);
                tempType.add("CODE_BEGIN");
                continue;
            }
            if(mdListType.get(i) == "CODE_END") {
                isCodeArea = false;
                tempList.add(line);
                tempType.add("CODE_END");
                continue;
            }
            
            // ����������������ʽ
            if(!isCodeArea) {
                // ����������
                if(line.length() > 2 && line.charAt(0) == '>' && lastLine.charAt(0) != '>' && nextLine.charAt(0) == '>') {
                    tempList.add(" ");
                    tempList.add(line);
                    tempType.add("QUOTE_BEGIN");
                    tempType.add("OTHER");
                }
                // �뿪������
                else if(line.length() > 2 && line.charAt(0) == '>' && lastLine.charAt(0) == '>' && nextLine.charAt(0) != '>') {
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("OTHER");
                    tempType.add("QUOTE_END");
                    
                }
                // ����������
                else if(line.length() > 2 && line.charAt(0) == '>' && lastLine.charAt(0) != '>' && nextLine.charAt(0) != '>') {
                    tempList.add(" ");
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("QUOTE_BEGIN");
                    tempType.add("OTHER");
                    tempType.add("QUOTE_END");
                    
                }
                // ���������б���
                else if((line.charAt(0) == '-' && lastLine.charAt(0) != '-' && nextLine.charAt(0) == '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) != '+' && nextLine.charAt(0) == '+') ||
                        (line.charAt(0) == '*' && lastLine.charAt(0) != '*' && nextLine.charAt(0) == '*')){
                    tempList.add(" ");
                    tempList.add(line);
                    tempType.add("UNORDER_BEGIN");
                    tempType.add("OTHER");
                }
                // �뿪�����б���
                else if((line.charAt(0) == '-' && lastLine.charAt(0) == '-' && nextLine.charAt(0) != '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) == '+' && nextLine.charAt(0) != '+') ||
                        (line.charAt(0) == '*' && lastLine.charAt(0) == '*' && nextLine.charAt(0) != '*')){
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("OTHER");
                    tempType.add("UNORDER_END");
                }
                // ���������б���
                else if((line.charAt(0) == '-' && lastLine.charAt(0) != '-' && nextLine.charAt(0) != '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) != '+' && nextLine.charAt(0) != '+') ||
                        (line.charAt(0) == '*' && lastLine.charAt(0) != '*' && nextLine.charAt(0) != '*')){
                    tempList.add(" ");
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("UNORDER_BEGIN");
                    tempType.add("OTHER");
                    tempType.add("UNORDER_END");
                }
                // ���������б���
                else if((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9')  && (line.charAt(1) == '.')) &&
                        !(lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (lastLine.charAt(1) == '.')) &&
                        (nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (nextLine.charAt(1) == '.'))){
                    tempList.add(" ");
                    tempList.add(line);
                    tempType.add("ORDER_BEGIN");
                    tempType.add("OTHER");
                }
                // �뿪�����б���
                else if((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9')  && (line.charAt(1) == '.')) &&
                        (lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (lastLine.charAt(1) == '.')) &&
                        !(nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (nextLine.charAt(1) == '.'))){
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("OTHER");
                    tempType.add("ORDER_END");
                }
                // ���������б���
                else if((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9')  && (line.charAt(1) == '.')) &&
                        !(lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (lastLine.charAt(1) == '.')) &&
                        !(nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9')  && (nextLine.charAt(1) == '.'))){
                    tempList.add(" ");
                    tempList.add(line);
                    tempList.add(" ");
                    tempType.add("ORDER_BEGIN");
                    tempType.add("OTHER");
                    tempType.add("ORDER_END");
                }
                // ����
                else {
                    tempList.add(line);
                    tempType.add("OTHER");
                }
            }
            else {
                tempList.add(line);
                tempType.add("OTHER");
            }
        }
        tempList.add(" ");
        tempType.add("OTHER");
        
        mdList = (ArrayList<String>)tempList.clone();
        mdListType = (ArrayList<String>)tempType.clone();
        tempList.clear();
        tempType.clear();
    }
    
    /**
     * �ж�ÿһ�� markdown �﷨��Ӧ�� html ����
     * @param ��
     * @return ��
     */
    private void defineLineType() {
        Stack<String> st = new Stack();
        for(int i = 0; i < mdList.size(); i++){
            String line = mdList.get(i);
            String typeLine = mdListType.get(i);
            if(typeLine == "QUOTE_BEGIN" || typeLine == "UNORDER_BEGIN" || typeLine == "ORDER_BEGIN" || typeLine == "CODE_BEGIN") {
                st.push(typeLine);
            }
            else if(typeLine == "QUOTE_END" || typeLine == "UNORDER_END" || typeLine == "ORDER_END" || typeLine == "CODE_END") {
                st.pop();
            }
            else if(typeLine == "OTHER") {
                if(!st.isEmpty()) {
                    // ������
                    if(st.peek() == "QUOTE_BEGIN") {
                        mdList.set(i, line.trim().substring(1).trim());
                    }
                    // �����б���
                    else if(st.peek() == "UNORDER_BEGIN") {
                        mdList.set(i, line.trim().substring(1).trim());
                        mdListType.set(i, "UNORDER_LINE");
                    }
                    // �����б���
                    else if(st.peek() == "ORDER_BEGIN") {
                        mdList.set(i, line.trim().substring(2).trim());
                        mdListType.set(i, "ORDER_LINE");
                    }
                    // ������
                    else {
                        mdListType.set(i, "CODE_LINE");
                    }
                }
                line = mdList.get(i);
                typeLine = mdListType.get(i);
                // ����
                if(line.trim().isEmpty()) {
                    mdListType.set(i, "BLANK_LINE");
                    mdList.set(i, "");
                }
                // ������
                else if(line.trim().charAt(0) == '#') {
                    mdListType.set(i, "TITLE");
                    mdList.set(i, line.trim());
                }
            }
        }
    }

    /**
     * ����ÿһ�е����ͣ��� markdown ��� ת���� html ���
     * @param ��
     * @return ��
     */
    private void translateToHtml() {
        for(int i = 0; i < mdList.size(); i++){
            String line = mdList.get(i);
            String typeLine = mdListType.get(i);
            // �ǿ���
            if(typeLine == "BLANK_LINE") {
                mdList.set(i, "");
            }
            // ����ͨ�ı���
            else if(typeLine == "OTHER") {
                mdList.set(i, "<p>" + translateToHtmlInline(line.trim()) + "</p>");
            }
            // �Ǳ�����
            else if(typeLine == "TITLE") {
                int titleClass = 1;
                for(int j = 1; j < line.length(); j++) {
                    if(line.charAt(j) == '#') {
                        titleClass++;
                    }
                    else {
                        break;
                    }
                }
                mdList.set(i, "<h" + titleClass + ">"+ translateToHtmlInline(line.substring(titleClass).trim()) +"</h" + titleClass + ">");
            }
            // �������б���
            else if(typeLine == "UNORDER_BEGIN") {
                mdList.set(i, "<ul>");
            }
            else if(typeLine == "UNORDER_END") {
                mdList.set(i, "</ul>");
            }
            else if(typeLine == "UNORDER_LINE") {
                mdList.set(i, "<li>" + translateToHtmlInline(line.trim()) + "</li>");
            }
            // �������б���
            else if(typeLine == "ORDER_BEGIN") {
                mdList.set(i, "<ol>");
            }
            else if(typeLine == "ORDER_END") {
                mdList.set(i, "</ol>");
            }
            else if(typeLine == "ORDER_LINE") {
                mdList.set(i, "<li>" + translateToHtmlInline(line.trim()) + "</li>");
            }
            // �Ǵ�����
            else if(typeLine == "CODE_BEGIN") {
                mdList.set(i, "<pre>");
            }
            else if(typeLine == "CODE_END") {
                mdList.set(i, "</pre>");
            }
            else if(typeLine == "CODE_LINE") {
                mdList.set(i, "<code>" + line + "</code>");
            }
            // ��������
            else if(typeLine == "QUOTE_BEGIN") {
                mdList.set(i, "<blockquote>");
            }
            else if(typeLine == "QUOTE_END"){
                mdList.set(i, "</blockquote>");
            }
        }
    }

    /**
     * �����ڵ� markdown ���ת���ɶ�Ӧ�� html
     * @param mark ���
     * @return html ���
     */
    private String translateToHtmlInline( String line) {
        String html = "";
        for(int i=0; i<line.length();i++) {
            // ͼƬ
            if(i < line.length() - 4 && line.charAt(i) == '!' && line.charAt(i + 1) == '[') {
                int index1 = line.indexOf(']', i + 1);
                if(index1 != -1 && line.charAt(index1 + 1) == '(' && line.indexOf(')', index1 + 2) != -1){
                    int index2 = line.indexOf(')', index1 + 2);
                    String picName = line.substring(i + 2, index1);
                    String picPath = line.substring(index1 + 2, index2);
                    line = line.replace(line.substring(i, index2 + 1), "<img alt='" + picName + "' src='" + picPath + "' />");
                }
            }
            // ����
            if(i < line.length() - 3 && ((i > 0 && line.charAt(i) == '[' && line.charAt(i - 1) != '!') || (line.charAt(0) == '['))) {
                int index1 = line.indexOf(']', i + 1);
                if(index1 != -1 && line.charAt(index1 + 1) == '(' && line.indexOf(')', index1 + 2) != -1){
                    int index2 = line.indexOf(')', index1 + 2);
                    String linkName = line.substring(i + 1, index1);
                    String linkPath = line.substring(index1 + 2, index2);
                    line = line.replace(line.substring(i, index2 + 1), "<a href='" + linkPath + "'> " + linkName + "</a>");
                }
            }
            // ��������
            if(i < line.length() - 1 && line.charAt(i) == '`' && line.charAt(i + 1) != '`') {
                int index = line.indexOf('`', i + 1);
                if(index != -1) {
                    String quoteName = line.substring(i + 1, index);
                    line = line.replace(line.substring(i, index + 1), "<code>" + quoteName + "</code>");
                }
            }
            // ����
            if(i < line.length() - 2 && line.charAt(i) == '*' && line.charAt(i + 1) == '*') {
                int index = line.indexOf("**", i + 1);
                if(index != -1) {
                    String quoteName = line.substring(i + 2, index );
                    line = line.replace(line.substring(i, index + 2), "<strong>" + quoteName + "</strong>");
                }
            }
            // б��
            if(i < line.length() - 2 && line.charAt(i) == '*' && line.charAt(i + 1) != '*') {
                int index = line.indexOf('*', i + 1);
                if(index != -1 && line.charAt(index + 1) != '*') {
                    String quoteName = line.substring(i + 1, index);
                    line = line.replace(line.substring(i, index + 1), "<i>" + quoteName + "</i>");
                }
            }
        }
        return line;
    }
    public static void main(String[] args) {
        MarkDownParser md = new MarkDownParser("afile.md");
        md.createHtmlFile();
    }
}
