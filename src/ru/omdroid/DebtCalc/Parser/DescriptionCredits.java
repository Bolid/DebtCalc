package ru.omdroid.DebtCalc.Parser;


import android.os.Environment;
import android.util.Log;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class DescriptionCredits {
    final String TAG = "DescriptionCredits";
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;
    Element element;
    Document doc;
    TransformerFactory transformerFactory;
    Transformer transformer;
    DOMSource domSource;
    StreamResult sr;
    String filepath = Environment.getExternalStorageDirectory().toString();
    String filename = "descriptionCredits.xml";
    File file = new File(filepath, filename);

    public DescriptionCredits(){
        if (file.isFile()){
            file.delete();
        }
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        }
        catch (ParserConfigurationException pce) {
            Log.e("APPLICATION", "ОШИБКА ПОЛУЧЕНИЯ АТТРИБУТА:", pce);
        }
        catch (TransformerConfigurationException tce) {
            Log.e(TAG, "Ошибка создания файла истории: ", tce);
        }
        createDocumentDescription();
    }

    private void createDocumentDescription(){
        try{
            file = new File(filepath);
            file.mkdir();
            file = new File(filepath, filename);
            doc = documentBuilder.newDocument();
            element = doc.createElement("Credit");
            doc.appendChild(element);
            domSource = new DOMSource(doc);
            sr = new StreamResult(file);
            transformer.transform(domSource, sr);
        } catch (TransformerException e) {
            Log.e(TAG, "Ошибка создания описателя: ", e);
        }
    }

    public void setParamCredit(String item, String sumCredit, String termCredit, String percendCredit, String pereplata){
        /*if (file.isFile() == false)
            createDocumentDescription();*/
        try {
            doc = documentBuilder.parse(file);
            element = doc.getDocumentElement();
            Element childelement = doc.createElement("Data");
            element.appendChild(childelement);

            Attr attr = doc.createAttribute("item");
            attr.setValue(item);
            childelement.setAttributeNode(attr);

            attr = doc.createAttribute("sumCredit");
            attr.setValue(sumCredit);
            childelement.setAttributeNode(attr);

            attr = doc.createAttribute("termCredit");
            attr.setValue(termCredit);
            childelement.setAttributeNode(attr);

            attr = doc.createAttribute("percendCredit");
            attr.setValue(percendCredit);
            childelement.setAttributeNode(attr);

            attr = doc.createAttribute("pereplata");
            attr.setValue(pereplata);
            childelement.setAttributeNode(attr);

            domSource = new DOMSource(doc);
            sr = new StreamResult(file);
            transformer.transform(domSource, sr);
            Log.v(TAG, "Данные сохранены");
        } catch (TransformerException te){
            Log.e("Error", String.valueOf(te));
        } catch (Exception e){
            Log.e(TAG, "Неизвестная ошибка: ", e);
        }
    }

    public ArrayList getParamCredit(String item){
        ArrayList list = new ArrayList();
        int indexList = 0;
        try{
            Log.v("APPLICATION","Файл: " + filepath+filename);
            doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Data");
            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Log.v("APPLICATION","Элемент: " + node.getNodeName());
                Element getelement = (Element)node;
                    if (item.equals(getelement.getAttribute("item"))){
                        list.add(indexList++, getelement.getAttribute("sumCredit"));
                        list.add(indexList++, getelement.getAttribute("termCredit"));
                        list.add(indexList++, getelement.getAttribute("percendCredit"));
                        if (getelement.getAttribute("pereplata").equals(""))
                            list.add("0");
                        else
                            list.add(indexList++, getelement.getAttribute("pereplata"));
                    }
            }
            Log.v(TAG, "Длина list (история): "+list.size());
        }
        catch (NullPointerException npe){
            Log.e(TAG, "ОШИБКА ПОЛУЧЕНИЯ АТТРИБУТА:", npe);
        }
        catch (Exception e) {
            Log.e(TAG,"Неизвестная ошибка: ",e);
        }
        return list;
    }
}
